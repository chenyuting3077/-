package com.bmi.final_project.ui;

import android.app.DatePickerDialog;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Constraints;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bmi.final_project.EventDBHelper;
import com.bmi.final_project.MainActivity;
import com.bmi.final_project.R;
import com.bmi.final_project.ui.home.HomeViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class calenderFragment extends Fragment {
    private HomeViewModel homeViewModel;
    private GridLayout GLayout;
    private LinearLayout[][]  ground= new LinearLayout[6][7];
    private TextView[][] textDate = new  TextView[6][7];
    private Calendar calendar;
    private Integer month,year;
    private TextView text_date;
    private ImageButton btn_left;
    private ImageButton btn_right;
    private SQLiteDatabase db;
    private EventDBHelper eventDBHelper;
    private Map<String, String> map = new HashMap<>();
    private FloatingActionButton fab;
    private DatePickerDialog datePickerDialog;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_calendar, container, false);
        GLayout = root.findViewById(R.id.gridlayout);
        text_date = root.findViewById(R.id.text_date);
        btn_left = root.findViewById(R.id.btn_leftArrow);
        btn_right = root.findViewById(R.id.btn_rightArrow);
        btn_left.setOnClickListener(listener_left);
        btn_right.setOnClickListener(listener_right);
        if (getContext() == null){
            return root;
        }
        EventDBHelper  dbHelper = new EventDBHelper(getContext());
        db = dbHelper.getReadableDatabase();
        //db.delete("event",null,null);
        /*fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);*/
        setTextBackground();
        init();
        text_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });
        datePickerDialog=new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(i,i1,i2);
                SimpleDateFormat format = new SimpleDateFormat("yyyy年 MM月");
                text_date.setText(format.format(calendar.getTime()));
                setDateCalender();
            }
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        return root;
    }
    private void showEvent(){
        if (getContext() == null){
            return;
        }
        Cursor c = db.rawQuery("select * from Event", null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                String start = c.getString(3);
                String end = c.getString(4);
                start=start.replace("\n","");
                end=end.replace("\n","");
                SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Calendar start_time = Calendar.getInstance();
                Calendar end_time = Calendar.getInstance();
                try {
                    start_time.setTime(dateFormat.parse(start));
                    start_time.set(Calendar.HOUR_OF_DAY,0);
                    start_time.set(Calendar.MINUTE,0);
                    end_time.setTime(dateFormat.parse(end));
                    end_time.set(Calendar.HOUR_OF_DAY,23);
                    end_time.set(Calendar.MINUTE,59);
                }catch (Exception e){
                    e.printStackTrace();
                    return;
                }
                Calendar mycal = calendar;
                //if (start_time.before(calendar) && Integer.parseInt(start_time[1]) <= calendar.get(Calendar.MONTH)+1 ){
                for(int i = 0; i < 6; ++i){
                    for(int j = 0; j < 7; ++j){
                        int date = Integer.parseInt(textDate[i][j].getText().toString());
                        mycal.set(Calendar.DAY_OF_MONTH,date);
                        if (start_time.before(mycal) && mycal.before(end_time) && textDate[i][j].getCurrentTextColor() == Color.BLACK){
                            TextView event = new TextView(getContext());
                            event.setBackgroundColor(Color.parseColor (map.get(c.getString(2))));
                            event.setWidth(135);
                            event.setTextSize(9);
                            event.setGravity(Gravity.CENTER);
                            event.setTextColor(Color.BLACK);
                            event.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            event.setText(c.getString(1));
                            ground[i][j].addView(event);
                        }
                    }
                }
                //}
            } while (c.moveToNext());
        }
        c.close();
    }

    private void init(){
        if (getContext() == null){
            return;
        }
        GLayout.removeAllViews();
        GLayout.setColumnCount(7);
        GLayout.setRowCount(5);
        calendar = Calendar.getInstance();
        int startweek = set_calender()-1;
        int max_day = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        text_date.setText(year + "年 " + (month+1) + "月");
        for(int i=0;i<6;++i){
            for(int j=0;j<7;++j){
                ground[i][j] = new LinearLayout(getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(135,240);
                ground[i][j].setLayoutParams(params);
                ground[i][j].setOrientation(LinearLayout.VERTICAL);
                textDate[i][j]=new TextView(getContext());
                textDate[i][j].setId(i*6+j);
                textDate[i][j].setWidth(135);
                textDate[i][j].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                if (i==5&&j==6){textDate[i][j].setBackground(getResources().getDrawable(R.drawable.calender_item_rightcorner));}
//                else if(i==5&&j==0){textDate[i][j].setBackground(getResources().getDrawable(R.drawable.calender_item_leftcorner));}
//                else{ textDate[i][j].setBackground(getResources().getDrawable(R.drawable.calender_item_background));}
                textDate[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                textDate[i][j].setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                ground[i][j].addView(textDate[i][j]);

                ground[i][j].setId(100+i*6+j);
                if (i==5&&j==6){ground[i][j].setBackground(getResources().getDrawable(R.drawable.calender_item_rightcorner));}
                else if(i==5&&j==0){ground[i][j].setBackground(getResources().getDrawable(R.drawable.calender_item_leftcorner));}
                else{ ground[i][j].setBackground(getResources().getDrawable(R.drawable.calender_item_background));}

                final int indi = i, indj = j;
                ground[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(textDate[indi][indj].getCurrentTextColor() == Color.BLACK) {
                            MainActivity act = (MainActivity) v.getContext();
                            NavController navController = Navigation.findNavController(act, R.id.nav_host_fragment);
                            Bundle bundle = new Bundle();
                            SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
                            Calendar cal = calendar;
                            cal.set(Calendar.DAY_OF_MONTH,Integer.parseInt(textDate[indi][indj].getText().toString()));
                            bundle.putString("date", format.format(cal.getTime()));
                            navController.navigate(R.id.action_nav_calendar_to_nav_home, bundle);
                            Toast.makeText(getActivity(), format.format(cal.getTime()), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
//                ground[i][j].setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//                GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
//                ground[i][j].setLayoutParams(lp);
                GLayout.addView(ground[i][j]);
            }
        }
        setDateCalender();
    }

    private int set_calender() {
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        calendar.set(year,month,1);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    View.OnClickListener listener_left = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            calendar.add(Calendar.MONTH,-1);
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            text_date.setText(year + "年 " + (month+1) + "月");
            setDateCalender();
        }
    };

    View.OnClickListener listener_right = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            calendar.add(Calendar.MONTH,1);
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            text_date.setText(year + "年 " + (month+1) + "月");
            setDateCalender();
        }
    };

    private void setDateCalender(){
        if (getContext() == null){
            return;
        }
        calendar.add(Calendar.MONTH,-1);
        int max_last_month =  calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.MONTH,+1);
        for(int i=0;i<6;++i) {
            for (int j = 0; j < 7; ++j) {
                ground[i][j].removeAllViews();
                textDate[i][j]=new TextView(getContext());
                textDate[i][j].setId(i*6+j);
                textDate[i][j].setWidth(135);
                textDate[i][j].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                textDate[i][j].setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                ground[i][j].addView(textDate[i][j]);

                int startweek = set_calender()-1;
                int max_day = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                if (i * 7 + j >= startweek && i * 7 + j + 1<= max_day + startweek) {
                    textDate[i][j].setText(Integer.toString(i * 7 + j - startweek + 1));
                    textDate[i][j].setTextColor(Color.parseColor("#000000"));
                }
                else if (i * 7 + j + 1 > max_day + startweek){
                    textDate[i][j].setText(Integer.toString(i * 7 + j + 1 - max_day - startweek));
                    textDate[i][j].setTextColor(Color.parseColor("#35000000"));
                }else{
                    textDate[i][j].setText(Integer.toString(max_last_month - startweek +j+1));
                    textDate[i][j].setTextColor(Color.parseColor("#35000000"));
                }
            }
        }
        showEvent();
    }

    private void setTextBackground(){
        map.put("EAT", "#FF6A6A");
        map.put("READ", "#B4FFAF");
        map.put("MEETING", "#95D5FF");
        map.put("SLEEP", "#8678FF");
        map.put("GAME", "#FFFF37");
        map.put("ACTIVITY", "#FFAFFC");
        map.put("SOCIAL", "#FFCD57");
        map.put("SPORT", "#6BFFFF");
        map.put("CLEAN", "#C1A454");
    }
}
