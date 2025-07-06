package com.bmi.final_project.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.bmi.final_project.EventDBHelper;
import com.bmi.final_project.EventList;
import com.bmi.final_project.MainActivity;
import com.bmi.final_project.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class addEventFragment extends Fragment {
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private Button summit, note_summit;
    private SQLiteDatabase db;
    private EventDBHelper eventDBHelper;
    private EditText eventName, note_content;
    private FrameLayout frameLayout;
    private TextView noteTextView;
    private String note = "";
    private Calendar start = Calendar.getInstance(),end = Calendar.getInstance();
    private enum CategoryType{
        EAT,READ,MEETING,SLEEP,GAME,ACTIVITY,SOCIAL,SPORT,CLEAN
    }
    ImageButton btnEat,btnRead,btnMeet,btnSleep,btnGame,btnActivity,btnSocial,btnSport,btnClean;
    EditText startTime,endTime;
    ImageView imageAddCategory;
    CategoryType categoryType;
    Boolean isSelectStart=true;
    FloatingActionButton fab;
    Calendar calendar = Calendar.getInstance();

    @Override
    public void onPause() {
        super.onPause();
        NavHostFragment navHostFragment = (NavHostFragment)getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if(!(navHostFragment.getChildFragmentManager().getFragments().get(0) instanceof addEventFragment)) {
            fab.setVisibility(View.VISIBLE);
            boolean istiming = ((MainActivity) getActivity()).getIsTiming();
            if (!istiming)
                frameLayout.setVisibility(View.VISIBLE);
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_addevent, container, false);
        fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
        frameLayout=getActivity().findViewById(R.id.frame_time);
        frameLayout.setVisibility(View.INVISIBLE);
        findView(root);
        setAOnclickListener();
        categoryType = CategoryType.EAT;
        categoryTypeChange();
        if (getContext() == null){
            return root;
        }
        eventDBHelper = new EventDBHelper(getContext());
        db = eventDBHelper.getReadableDatabase();
        //db.delete("event",null,null);
        return root;
    }

    public void onViewCreated(View view, Bundle savedInstanceState){
        startTime.setShowSoftInputOnFocus(false);
        endTime.setShowSoftInputOnFocus(false);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        startTime.setText(format.format(start.getTime()));
        end.add(Calendar.MINUTE, 10);
        endTime.setText(format.format(end.getTime()));
        final GregorianCalendar calendar=new GregorianCalendar ();
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
                String thisdate = format.format(calendar.getTime());
                thisdate = thisdate.replace("\n","");
                if(isSelectStart){
                    startTime.setText(thisdate);
                    start.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE));
                    start.set(Calendar.HOUR_OF_DAY,i);
                    start.set(Calendar.MINUTE,i1);
                }else{
                    endTime.setText(thisdate);
                    end.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE));
                    end.set(Calendar.HOUR_OF_DAY,i);
                    end.set(Calendar.MINUTE,i1);
                }
            }
        },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false);
    }

    public void findView(View root){
        btnEat = root.findViewById(R.id.btn_eat);
        btnRead = root.findViewById(R.id.btn_read);
        btnMeet = root.findViewById(R.id.btn_meet);
        btnSleep = root.findViewById(R.id.btn_sleep);
        btnGame = root.findViewById(R.id.btn_game);
        btnActivity = root.findViewById(R.id.btn_activity);
        btnSocial = root.findViewById(R.id.btn_social);
        btnSport = root.findViewById(R.id.btn_sport);
        btnClean = root.findViewById(R.id.btn_clean);
        eventName = root.findViewById(R.id.add_eventName);
        startTime=root.findViewById(R.id.editTextDate);
        endTime=root.findViewById(R.id.add_endTime);
        summit = root.findViewById(R.id.summit);
        imageAddCategory = root.findViewById(R.id.image_add_category);
        noteTextView = root.findViewById(R.id.noteTextView);
    }

    public void categoryTypeChange (){
        if (categoryType.equals(CategoryType.EAT)){
            imageAddCategory.setImageResource(R.drawable.event_1);
        }
        else if(categoryType.equals(CategoryType.READ)){
            imageAddCategory.setImageResource(R.drawable.event_2);
        }
        else if(categoryType.equals(CategoryType.MEETING)){
            imageAddCategory.setImageResource(R.drawable.event_3);
        }
        else if(categoryType.equals(CategoryType.SLEEP)){
            imageAddCategory.setImageResource(R.drawable.event_4);
        }
        else if(categoryType.equals(CategoryType.GAME)){
            imageAddCategory.setImageResource(R.drawable.event_5);
        }
        else if(categoryType.equals(CategoryType.ACTIVITY)){
            imageAddCategory.setImageResource(R.drawable.event_6);
        }
        else if(categoryType.equals(CategoryType.SOCIAL)){
            imageAddCategory.setImageResource(R.drawable.event_7);
        }
        else if(categoryType.equals(CategoryType.SPORT)){
            imageAddCategory.setImageResource(R.drawable.event_8);
        }
        else if(categoryType.equals(CategoryType.CLEAN)){
            imageAddCategory.setImageResource(R.drawable.event_9);
        }
    }


    /*===================onClickListener===================*/
    public void setAOnclickListener(){
        btnEat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryType = CategoryType.EAT;
                categoryTypeChange();
            }
        });
        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryType = CategoryType.READ;
                categoryTypeChange();
            }
        });
        btnMeet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryType = CategoryType.MEETING;
                categoryTypeChange();
            }
        });
        btnSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryType = CategoryType.SLEEP;
                categoryTypeChange();
            }
        });
        btnGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryType = CategoryType.GAME;
                categoryTypeChange();
            }
        });
        btnActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryType = CategoryType.ACTIVITY;
                categoryTypeChange();
            }
        });
        btnSocial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryType = CategoryType.SOCIAL;
                categoryTypeChange();
            }
        });
        btnSport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryType = CategoryType.SPORT;
                categoryTypeChange();
            }
        });
        btnClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryType = CategoryType.CLEAN;
                categoryTypeChange();
            }
        });
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSelectStart=true;
                Calendar calendar = Calendar.getInstance();
                datePickerDialog.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSelectStart=false;
                Calendar calendar = Calendar.getInstance();
                datePickerDialog.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
        summit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!eventName.getText().toString().equals("")) {
                    if (start.before(end)) {
                        if (getContext() == null){
                            return;
                        }
                        EventList eventList = new EventList("Event",getContext());
                        if(!eventList.isExist(start,end)) {
                            ContentValues cv = new ContentValues();
                            cv.put("event_name", eventName.getText().toString());
                            if (categoryType.equals(CategoryType.EAT)) {
                                cv.put("category", "EAT");
                            } else if (categoryType.equals(CategoryType.READ)) {
                                cv.put("category", "READ");
                            } else if (categoryType.equals(CategoryType.MEETING)) {
                                cv.put("category", "MEETING");
                            } else if (categoryType.equals(CategoryType.SLEEP)) {
                                cv.put("category", "SLEEP");
                            } else if (categoryType.equals(CategoryType.GAME)) {
                                cv.put("category", "GAME");
                            } else if (categoryType.equals(CategoryType.ACTIVITY)) {
                                cv.put("category", "ACTIVITY");
                            } else if (categoryType.equals(CategoryType.SOCIAL)) {
                                cv.put("category", "SOCIAL");
                            } else if (categoryType.equals(CategoryType.SPORT)) {
                                cv.put("category", "SPORT");
                            } else if (categoryType.equals(CategoryType.CLEAN)) {
                                cv.put("category", "CLEAN");
                            }
                            cv.put("start_time", startTime.getText().toString().replace("\n",""));
                            cv.put("end_time", endTime.getText().toString().replace("\n",""));
                            cv.put("note", note);
                            db.insert("Event", null, cv);
                            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                            navController.navigateUp();
                        }
                        else{
                            Toast.makeText(getActivity(), "The time between your start time and end time does exit other activity!", Toast.LENGTH_LONG).show();
                        }
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
        noteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder myBuilder = new AlertDialog.Builder(getActivity());
                View myview = getLayoutInflater().inflate(R.layout.fragment_note,null);
                myBuilder.setView(myview);
                final AlertDialog note_dialog = myBuilder.create();
                note_dialog.show();
                note_content = myview.findViewById(R.id.note_Text);
                note_summit = myview.findViewById(R.id.note_summit);
                note_content.setText(note);
                note_summit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        note = note_content.getText().toString();
                        note_dialog.dismiss();
                    }
                });
            }
        });
    }

}
