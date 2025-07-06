package com.bmi.final_project;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bmi.final_project.ui.addEventFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.ActionBar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private Map<String, Integer> iconMap = new HashMap<>();
    private AppBarConfiguration mAppBarConfiguration;
    private ImageButton btn_addEvent,btn_menu, btnEat,btnRead,btnMeet,btnSleep,btnGame,btnActivity,btnSocial,btnSport,btnClean;
    private NavController navController;
    private ImageButton plus;
    private Calendar calendar = Calendar.getInstance(), barcal = Calendar.getInstance();
    private TextView text_date, time;
    private FrameLayout frame_time;
    final Activity activity = this;
    boolean istiming=true;
    private Timer timer = null;//計時器
    private TimerTask timerTask = null;
    private FloatingActionButton fab;
    private int sec=0;
    private String starttime, endtime, eventname, category, note;
    private SQLiteDatabase db;
    private DatePickerDialog datePickerDialog;
    ImageView start_catagory;

    private static Context mContext;
    public Context getContext() {return mContext;}
    public boolean getIsTiming(){
        return istiming;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        fab = findViewById(R.id.fab);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        mContext = getApplicationContext();
        Toolbar toolbar = findViewById(R.id.toolbar);
        text_date = findViewById(R.id.text_barDate);
        time=findViewById(R.id.time);
        frame_time=findViewById(R.id.frame_time);
        plus = findViewById(R.id.btn_addEvent);
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        text_date.setText(format.format(barcal.getTime()));
        setSupportActionBar(toolbar);
        initMap();

        datePickerDialog=new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                barcal.set(i,i1,i2);
                SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
                text_date.setText(format.format(barcal.getTime()));
            }
        },barcal.get(Calendar.YEAR),barcal.get(Calendar.MONTH),barcal.get(Calendar.DAY_OF_MONTH));

        PracticalDBHelper dbHelper = new PracticalDBHelper(this);
        db = dbHelper.getReadableDatabase();

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_calendar, R.id.nav_addEvent, R.id.nav_chart,R.id.nav_home)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);



        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        final ActionBar actionBar = getSupportActionBar();
//        actionBar.setCustomView(R.layout.action_bar_plus);
//        actionBar.setDisplayShowTitleEnabled(false);
//        actionBar.setDisplayShowCustomEnabled(true);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        setOnclickListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void setOnclickListener(){
        final Activity activitys = this;
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(activitys, R.id.nav_host_fragment);
                navController.navigate(R.id.nav_addEvent);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(istiming){
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                    View myview = getLayoutInflater().inflate(R.layout.fragment_start_time,null);
                    start_catagory = myview.findViewById(R.id.image_add_category);
                    final TextView add_eventName = myview.findViewById(R.id.add_eventName);
                    final TextView noteText = myview.findViewById(R.id.noteText);
                    Button start_summit = myview.findViewById(R.id.start_summit);
                    Calendar now = Calendar.getInstance();
                    EventList eventList = new EventList("Day","Event",mContext,now);
                    try {
                        Event event = new Event(eventList.getNowEvent(now));
                        if(!event.getname().equals("")){
                            add_eventName.setText(event.getname());
                            start_catagory.setImageResource(iconMap.get(event.getcategory()));
                            category = event.getcategory();
                        }else{
                            category = "EAT";
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    mBuilder.setView(myview);
                    final AlertDialog dialog = mBuilder.create();
                    dialog.show();
                    start_summit.setOnClickListener(new View.OnClickListener(){
                        public void onClick(View view){
                            if (!add_eventName.getText().toString().equals("")) {
                                istiming=!istiming;
                                eventname = add_eventName.getText().toString();
                                note = noteText.getText().toString();
                                sec = 0;
                                time.setText("00:00:00");
                                calendar.getInstance();
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                starttime = format.format(calendar.getTime());
                                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) fab.getLayoutParams();
                                lp.gravity = Gravity.BOTTOM | GravityCompat.START;
                                lp.leftMargin=200;
                                fab.setImageDrawable(getResources().getDrawable(R.drawable.stop));
                                frame_time.setVisibility(View.VISIBLE);
                                fab.setLayoutParams(lp);
                                timer=new Timer();
                                startTime();
                                dialog.dismiss();;
                            }
                            else{
                                Toast.makeText(MainActivity.this, "Please input a event name!", Toast.LENGTH_LONG).show();
                            }
                        }

                    });
                    start_catagory.setOnClickListener(new View.OnClickListener(){
                        public void onClick(View view){
                            AlertDialog.Builder myBuilder = new AlertDialog.Builder(MainActivity.this);
                            View mview = getLayoutInflater().inflate(R.layout.fragment_event_catagory,null);
                            myBuilder.setView(mview);
                            final AlertDialog catagory_dialog = myBuilder.create();
                            catagory_dialog.show();
                            btnEat = mview.findViewById(R.id.start_btn_eat);
                            btnRead = mview.findViewById(R.id.start_btn_read);
                            btnMeet = mview.findViewById(R.id.start_btn_meet);
                            btnSleep = mview.findViewById(R.id.start_btn_sleep);
                            btnGame = mview.findViewById(R.id.start_btn_game);
                            btnActivity = mview.findViewById(R.id.start_btn_activity);
                            btnSocial = mview.findViewById(R.id.start_btn_social);
                            btnSport = mview.findViewById(R.id.start_btn_sport);
                            btnClean = mview.findViewById(R.id.start_btn_clean);
                            btnEat.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    category = "EAT";
                                    start_catagory.setImageResource(R.drawable.event_1);
                                    catagory_dialog.dismiss();
                                }
                            });
                            btnRead.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    category = "READ";
                                    start_catagory.setImageResource(R.drawable.event_2);
                                    catagory_dialog.dismiss();
                                }
                            });
                            btnMeet.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    category = "MEETING";
                                    start_catagory.setImageResource(R.drawable.event_3);
                                    catagory_dialog.dismiss();
                                }
                            });
                            btnSleep.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    category = "SLEEP";
                                    start_catagory.setImageResource(R.drawable.event_4);
                                    catagory_dialog.dismiss();
                                }
                            });
                            btnGame.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    category = "GAME";
                                    start_catagory.setImageResource(R.drawable.event_5);
                                    catagory_dialog.dismiss();
                                }
                            });
                            btnActivity.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    category = "ACTIVITY";
                                    start_catagory.setImageResource(R.drawable.event_6);
                                    catagory_dialog.dismiss();
                                }
                            });
                            btnSocial.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    category = "SOCIAL";
                                    start_catagory.setImageResource(R.drawable.event_7);
                                    catagory_dialog.dismiss();
                                }
                            });
                            btnSport.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    category = "SPORT";
                                    start_catagory.setImageResource(R.drawable.event_8);
                                    catagory_dialog.dismiss();
                                }
                            });
                            btnClean.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    category = "CLEAN";
                                    start_catagory.setImageResource(R.drawable.event_9);
                                    catagory_dialog.dismiss();
                                }
                            });
                        }
                    });
                }
                else{
                    istiming=!istiming;
                    stopTime();
                    calendar.add(Calendar.SECOND, +sec);
                    SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    endtime =format.format(calendar.getTime());
                    ContentValues cv = new ContentValues();
                    cv.put("event_name", eventname);
                    cv.put("category", category);
                    cv.put("start_time", starttime.replace("\n",""));
                    cv.put("end_time", endtime.replace("\n",""));
                    cv.put("note",note);
                    db.insert("Practical", null, cv);
                }
            }
        });
        text_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });
    }

    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HH:mm:ss");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            String formattedDate = simpleDateFormat.format(new Date((long)(sec*1000)));
            time.setText(formattedDate);
            startTime(); };
    };

    private void startTime() {
        if(timer==null)
            timer = new Timer();
        timerTask = new TimerTask() {

            @Override
            public void run() {
                sec += 1;
                Message message = Message.obtain();
                message.arg1=sec;
                mHandler.sendMessage(message);//傳送訊息
            }
        };
        timer.schedule(timerTask, 1000);//1000ms執行一次
    }
    /**
     * 停止自動減時
     */
    private void stopTime() {
        if(timer!=null)
            timer.cancel();
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) fab.getLayoutParams();
        lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        lp.setMargins(0,0,0,85);
        fab.setImageDrawable(getResources().getDrawable(R.drawable.play_button));
        frame_time.setVisibility(View.INVISIBLE);
        fab.setLayoutParams(lp);
    }
    public void initMap(){
        iconMap.put("EAT", R.drawable.event_1);
        iconMap.put("READ", R.drawable.event_2);
        iconMap.put("MEETING", R.drawable.event_3);
        iconMap.put("SLEEP", R.drawable.event_4);
        iconMap.put("GAME", R.drawable.event_5);
        iconMap.put("ACTIVITY", R.drawable.event_6);
        iconMap.put("SOCIAL", R.drawable.event_7);
        iconMap.put("SPORT", R.drawable.event_8);
        iconMap.put("CLEAN", R.drawable.event_9);
    }


}