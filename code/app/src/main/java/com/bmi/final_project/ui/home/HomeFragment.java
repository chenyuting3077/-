package com.bmi.final_project.ui.home;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.bmi.final_project.Event;
import com.bmi.final_project.EventDBHelper;
import com.bmi.final_project.EventList;
import com.bmi.final_project.MainActivity;
import com.bmi.final_project.PracticalDBHelper;
import com.bmi.final_project.R;
import com.bmi.final_project.ui.addEventFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class HomeFragment extends Fragment {

    private RelativeLayout timeline;
    private Map<String, Integer> iconMap = new HashMap<>();
    private Map<String, Integer> backgroundMap = new HashMap<>();
    private TextView text_barDate;
    private Calendar today;
    String newCategory = "";
    private TextView timeline_dialog_name,timeline_dialog_strTime,timeline_dialog_endTime;
    private ImageView timeline_dialog_icon;
    private EditText timeline_dialog_note;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    Boolean isSelectStart = true;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日"),  timeformat =new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private Calendar calendar = Calendar.getInstance(), start = calendar, end = calendar;
    private double myUnit = 52;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        text_barDate = getActivity().findViewById(R.id.text_barDate);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        //初始化数据
        timeline = root.findViewById(R.id.timeline);
        today = Calendar.getInstance();
        datePickerDialog=new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(i,i1,i2);
                Calendar calendar = Calendar.getInstance();
                timePickerDialog.updateTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
                timePickerDialog.show();
            }
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        timePickerDialog=new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                calendar.set(Calendar.HOUR_OF_DAY,i);
                calendar.set(Calendar.MINUTE,i1);
                SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String thisdate=format.format(calendar.getTime());
                thisdate = thisdate.replace("\n","");
                if(isSelectStart){
                    timeline_dialog_strTime.setText(thisdate);
                    start.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE));
                    start.set(Calendar.HOUR_OF_DAY,i);
                    start.set(Calendar.MINUTE,i1);
                }else{
                    timeline_dialog_endTime.setText(thisdate);
                    end.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE));
                    end.set(Calendar.HOUR_OF_DAY,i);
                    end.set(Calendar.MINUTE,i1);
                }
            }
        },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false);
        if (getArguments() != null) {
            String date = getArguments().getString("date", "");
            text_barDate.setText(date);
            try {
                today.setTime(format.parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        text_barDate.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    today.setTime(format.parse(text_barDate.getText().toString()));
                    getEvent();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        initMap();
        try {
            getEvent();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        /*滑動顯示*/
        final ScrollView scrollView = root.findViewById(R.id.scrollView);
        final int startHourInt = today.get(Calendar.HOUR);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0,(px_to_dp((startHourInt)*myUnit)));// 改變滾動條的位置
            }
        };
        Handler handler = new Handler();
        handler.postDelayed(runnable, 200);
        /**/
        return root;
    }

    public void getEvent() throws ParseException {
        if (getContext() == null){
            return;
        }
        timeline.removeAllViews();
        EventList eventlist = new EventList("DAY", "Event", getContext(), today);
        EventList practicallist = new EventList("DAY", "Practical", getContext(), today);
        SimpleDateFormat thisFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (int i = 0; i < eventlist.getCounts(); ++i) {
            Calendar scal = eventlist.getLowerTime(i);
            Calendar ecal = eventlist.getUpperTime(i);

            //event//
            Event event = eventlist.getevent(i);
            String eventName = event.getname();
            String category = event.getcategory();
            String startStr = thisFormat.format(scal.getTime());
            String endStr = thisFormat.format(ecal.getTime());
            String note = event.getnote();
            //practical//

            addEvent(eventName, category, startStr, endStr, note,"Event", eventlist.getevent(i).getstrTime(), eventlist.getevent(i).getendTime());

        }

        for (int i=0;i<practicallist.getCounts();++i) {
            Calendar praScal = practicallist.getLowerTime(i);
            Calendar praEcal = practicallist.getUpperTime(i);

            Event practical = practicallist.getevent(i);
            String praName = practical.getname();
            String praCategory = practical.getcategory();
            String praStartStr = thisFormat.format(praScal.getTime());
            String practicalEndStr = thisFormat.format(praEcal.getTime());
            String practicalNote = practical.getnote();
            addEvent(praName, praCategory, praStartStr, practicalEndStr,practicalNote , "Practical", practicallist.getevent(i).getstrTime(), practicallist.getevent(i).getendTime());
            //addEvent(praName, praCategory, "2021-1-13 18:00", "2021-1-13 23:59",practicalNote , "Practical", practicallist.getevent(i).getstrTime(), practicallist.getevent(i).getendTime());
        }
    }

    public void addEvent(final String name, final String category, final String start_time, final String end_time, final String note, final String type, final String realStart, final String realEnd){
        if (getContext() == null){
            return;
        }
        //xxxx-xx-xx xx:xx
        Double startHourDouble,endHourDouble,startMinDouble,endMinDouble,heightDouble;
        RelativeLayout eventOnTimeline = new RelativeLayout(getContext());
        ImageView icon_imageView = new ImageView(getContext());
        TextView name_textView = new TextView(getContext());
        TextView note_textView = new TextView(getContext());
        RelativeLayout.LayoutParams imageLayoutParams = new RelativeLayout.LayoutParams(px_to_dp(30.0),px_to_dp(30.0));
        RelativeLayout.LayoutParams nameLayoutParams = new RelativeLayout.LayoutParams(px_to_dp(270.0),px_to_dp(60.0));
        RelativeLayout.LayoutParams noteLayoutParams = new RelativeLayout.LayoutParams(px_to_dp(270.0),px_to_dp(200.0));

        icon_imageView.setImageResource(iconMap.get(category));

        imageLayoutParams.setMargins(px_to_dp(5.0),px_to_dp(15.0),px_to_dp(5.0),px_to_dp(5.0));
        nameLayoutParams.setMargins(px_to_dp(40.0),px_to_dp(0.0),px_to_dp(5.0),px_to_dp(5.0));
        noteLayoutParams.setMargins(px_to_dp(40.0),px_to_dp(55.0),10,10);

        icon_imageView.setLayoutParams(imageLayoutParams);
        name_textView.setLayoutParams(nameLayoutParams);
        note_textView.setLayoutParams(noteLayoutParams);
        /*設定名字*/
        name_textView.setText(name);
        name_textView.setTextSize(20);
        name_textView.setGravity(Gravity.CENTER_VERTICAL);
        name_textView.setTextColor(Color.BLACK);
        name_textView.setTypeface(name_textView.getTypeface(),Typeface.BOLD);
        /*備註*/
        note_textView.setText(note);
        note_textView.setTextSize(15);
        note_textView.setTextColor(Color.BLACK);
        /**/
        eventOnTimeline.addView(icon_imageView);
        eventOnTimeline.addView(name_textView);
        eventOnTimeline.addView(note_textView);

        String[] startArray = start_time.split("-|-| |:|:");
        String[] endArray = end_time.split("-|-| |:|:");

        startHourDouble = Double.parseDouble(startArray[3]);
        endHourDouble = Double.parseDouble(endArray[3]);
        startMinDouble = Double.parseDouble(startArray[4]);
        endMinDouble = Double.parseDouble(endArray[4]);

        heightDouble = ((endHourDouble+endMinDouble/60) - (startHourDouble+ startMinDouble/60));
        Double leftMargin;
        if (type == "Event"){
            leftMargin = 10.0;
        }
        else{//Practical
            leftMargin = 135.0 + 15.0;
        }
        ///set container ///
        RelativeLayout.LayoutParams containerLayoutParams = new RelativeLayout.LayoutParams(px_to_dp(135.0), px_to_dp(heightDouble*myUnit));
        containerLayoutParams.setMargins(px_to_dp(leftMargin), px_to_dp((startHourDouble+ startMinDouble/60)*myUnit) ,px_to_dp(10.0),px_to_dp(10.0));


        eventOnTimeline.setBackgroundResource(backgroundMap.get(category));
        eventOnTimeline.setLayoutParams(containerLayoutParams);
        eventOnTimeline.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (getContext() == null){
                    return;
                }
                if (getActivity() == null){
                    return;
                }
                Button timeline_dialog_delete,timeline_dialog_submit;
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                View myview = getLayoutInflater().inflate(R.layout.fragment_timeline_click,null);
                timeline_dialog_name = myview.findViewById(R.id.timeline_dialog_name);
                timeline_dialog_strTime = myview.findViewById(R.id.timeline_dialog_strTime);
                timeline_dialog_endTime = myview.findViewById(R.id.timeline_dialog_endTime);
                timeline_dialog_icon = myview.findViewById(R.id.timeline_dialog_icon);
                timeline_dialog_note = myview.findViewById(R.id.timeline_dialog_note);
                timeline_dialog_delete = myview.findViewById(R.id.timeline_dialog_delete);
                timeline_dialog_submit = myview.findViewById(R.id.timeline_dialog_submit);

                timeline_dialog_name.setText(name);
                timeline_dialog_strTime.setText(realStart);
                timeline_dialog_endTime.setText(realEnd);
                timeline_dialog_icon.setImageResource(iconMap.get(category));
                timeline_dialog_note.setText(note);
                setListener(type);
                mBuilder.setView(myview);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();
                newCategory = category;
                timeline_dialog_submit.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                      String newName = timeline_dialog_name.getText().toString();
                      String newStart = timeline_dialog_strTime.getText().toString();
                      String newEnd = timeline_dialog_endTime.getText().toString();
                      String newNote = timeline_dialog_note.getText().toString();
                      if (!newName.equals("")) {
                          Calendar start = Calendar.getInstance(), end = Calendar.getInstance();
                          try {
                              start.setTime(timeformat.parse(newStart));
                              end.setTime(timeformat.parse(newEnd));
                          } catch (ParseException e) {
                              e.printStackTrace();
                          }
                          if (start.before(end)) {
                              /*EventList eventList = new EventList(type, getContext());
                              if(!eventList.isExist(start,end)) {*/
                              editEvent(type, new Event(newName, newCategory, newStart, newEnd, newNote), start_time);
                              try {
                                  getEvent();
                              } catch (ParseException e) {
                                  e.printStackTrace();
                              }
                              dialog.dismiss();
                             /*}
                             else{
                                  Toast.makeText(getActivity(), "The time between your start time and end time does exit other activity!", Toast.LENGTH_LONG).show();
                              }*/
                          }
                          else{
                              Toast.makeText(getActivity(), "End time should after than start time!", Toast.LENGTH_LONG).show();
                          }
                      }
                      else{
                          Toast.makeText(getActivity(), "Please input a event name!", Toast.LENGTH_LONG).show();
                      }
                  }
                });
                timeline_dialog_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ImageButton btnEat,btnRead,btnMeet,btnSleep,btnGame,btnActivity,btnSocial,btnSport,btnClean;
                        AlertDialog.Builder myBuilder = new AlertDialog.Builder(getActivity());
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
                                newCategory = "EAT";
                                timeline_dialog_icon.setImageResource(R.drawable.event_1);
                                catagory_dialog.dismiss();
                            }
                        });
                        btnRead.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                newCategory = "READ";
                                timeline_dialog_icon.setImageResource(R.drawable.event_2);
                                catagory_dialog.dismiss();
                            }
                        });
                        btnMeet.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                newCategory = "MEETING";
                                timeline_dialog_icon.setImageResource(R.drawable.event_3);
                                catagory_dialog.dismiss();
                            }
                        });
                        btnSleep.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                newCategory = "SLEEP";
                                timeline_dialog_icon.setImageResource(R.drawable.event_4);
                                catagory_dialog.dismiss();
                            }
                        });
                        btnGame.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                newCategory = "GAME";
                                timeline_dialog_icon.setImageResource(R.drawable.event_5);
                                catagory_dialog.dismiss();
                            }
                        });
                        btnActivity.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                newCategory = "ACTIVITY";
                                timeline_dialog_icon.setImageResource(R.drawable.event_6);
                                catagory_dialog.dismiss();
                            }
                        });
                        btnSocial.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                newCategory = "SOCIAL";
                                timeline_dialog_icon.setImageResource(R.drawable.event_7);
                                catagory_dialog.dismiss();
                            }
                        });
                        btnSport.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                newCategory = "SPORT";
                                timeline_dialog_icon.setImageResource(R.drawable.event_8);
                                catagory_dialog.dismiss();
                            }
                        });
                        btnClean.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                newCategory = "CLEAN";
                                timeline_dialog_icon.setImageResource(R.drawable.event_9);
                                catagory_dialog.dismiss();
                            }
                        });
                    }
                });
                timeline_dialog_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteEvent(type,realStart);
                        try {
                            getEvent();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                });
            }
        });
        timeline.addView(eventOnTimeline);
    }

    /*choice*/
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

        backgroundMap.put("EAT", R.drawable.timeline_event1);
        backgroundMap.put("READ", R.drawable.timeline_event2);
        backgroundMap.put("MEETING", R.drawable.timeline_event3);
        backgroundMap.put("SLEEP", R.drawable.timeline_event4);
        backgroundMap.put("GAME", R.drawable.timeline_event5);
        backgroundMap.put("ACTIVITY", R.drawable.timeline_event6);
        backgroundMap.put("SOCIAL", R.drawable.timeline_event7);
        backgroundMap.put("SPORT", R.drawable.timeline_event8);
        backgroundMap.put("CLEAN", R.drawable.timeline_event9);
    }

    private int px_to_dp(Double input){
        Float tmp = input.floatValue();
        Resources r = getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,tmp , r.getDisplayMetrics());
    }

    private void deleteEvent(String tableName, String str_time){
        if (getContext() == null){
            return;
        }
        EventDBHelper eventDBHelper = new EventDBHelper(getContext());
        PracticalDBHelper practicalDBHelper = new PracticalDBHelper(getContext());
        SQLiteDatabase db = (tableName == "Event")?eventDBHelper.getWritableDatabase():practicalDBHelper.getWritableDatabase();

        db.delete(tableName,"start_time = '"+str_time+"'",null);
    }

    private void editEvent(String tableName,Event event, String str_time){
        if (getContext() == null){
            return;
        }
        ContentValues cv = new ContentValues();
        cv.put("event_name", event.getname());
        cv.put("category", event.getcategory());
        cv.put("start_time", event.getstrTime());
        cv.put("end_time", event.getendTime());
        cv.put("note", event.getnote());
        EventDBHelper eventDBHelper = new EventDBHelper(getContext());
        PracticalDBHelper practicalDBHelper = new PracticalDBHelper(getContext());
        SQLiteDatabase db = (tableName == "Event")?eventDBHelper.getWritableDatabase():practicalDBHelper.getWritableDatabase();
        db.update(tableName, cv, "start_time = '"+str_time+"'",null);
    }

    public void setListener(String datatype){
        if (datatype.equals("Practical")){
            return;
        }
        timeline_dialog_strTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSelectStart=true;
                Calendar calendar = Calendar.getInstance();
                datePickerDialog.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
        timeline_dialog_endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSelectStart=false;
                Calendar calendar = Calendar.getInstance();
                datePickerDialog.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
    }
}