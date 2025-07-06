package com.bmi.final_project;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class EventList {
    private ArrayList<Event> eventlist = new ArrayList<>();
    private EventDBHelper eventDBHelper;
    private PracticalDBHelper practicalDBHelper;
    private Calendar startcal, endcal;
    private SQLiteDatabase db;
    private String[] type= {"EAT","READ","MEETING","SLEEP","GAME","ACTIVITY","SOCIAL","SPORT","CLEAN"};
    private String query = "", start = "", end = "", ttype = "",dbtype = "";

    public EventList(String timetype, String datatype, Context context, Calendar calendar) {
        dbtype = datatype;
        ttype = timetype;
        startcal = Calendar.getInstance();
        endcal = Calendar.getInstance();
        startcal.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
        endcal.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
        startcal.set(Calendar.HOUR_OF_DAY, 0);
        startcal.set(Calendar.MINUTE, 0);
        endcal.set(Calendar.HOUR_OF_DAY, 23);
        endcal.set(Calendar.MINUTE, 59);
        int day = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        switch (datatype) {
            case "Event":
                eventDBHelper = new EventDBHelper(context);
                db = eventDBHelper.getReadableDatabase();
                //db.delete("Event",null,null);
                break;
            case "Practical":
                practicalDBHelper = new PracticalDBHelper(context);
                db = practicalDBHelper.getReadableDatabase();
                //db.delete("Practical",null,null);
                break;
        }
        switch (timetype) { //
            case "Day":
                break;
            case "Week":
                if (day == 0) {
                    startcal.add(Calendar.DATE, -6);
                } else {
                    startcal.add(Calendar.DATE, -(day - 1));
                    endcal.add(Calendar.DATE, 7 - day);
                }
                break;
            case "Month":
                startcal.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
                endcal.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                int i = calendar.get(Calendar.DAY_OF_MONTH);
                break;
            case "Year":
                startcal.set(calendar.get(Calendar.YEAR), 0, 1);
                endcal.set(calendar.get(Calendar.YEAR), 11, 31);
                break;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        start = format.format(startcal.getTime());
        end = format.format(endcal.getTime());
        query = "SELECT * FROM " + datatype + " WHERE start_time <= '" + end + "' AND end_time >= '" + start + "'";
        //query = "select * from Event";
        Cursor c = db.rawQuery(query, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                Event event = new Event(c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5));
                eventlist.add(event);
            } while (c.moveToNext());
        }
        c.close();
    }

    public EventList(String datatype, Context context){
        dbtype = datatype;
        switch (datatype) {
            case "Event":
                eventDBHelper = new EventDBHelper(context);
                db = eventDBHelper.getReadableDatabase();
                break;
            case "Practical":
                practicalDBHelper = new PracticalDBHelper(context);
                db = practicalDBHelper.getReadableDatabase();
                break;
        }
    }

    public EventList(){ }

    public ArrayList<Event> getlist() {
        return eventlist;
    }

    public Event getevent(int index) {
        return eventlist.get(index);
    }

    public String getstarttime() {
        return start;
    }

    public String getendtime() {
        return end;
    }

    public int getCounts(){return eventlist.size();}

    public int getTypeId(int index){
        for(int i = 0; i < 9; ++i){
            if (eventlist.get(index).getcategory().equals(type[i])){
                return i;
            }
        }
        return -1;
    }

    public Calendar getLowerTime(int index) throws ParseException {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        cal.setTime(format.parse(eventlist.get(index).getstrTime()));
        return (cal.after(startcal))? cal:startcal;
    }


    public Calendar getUpperTime(int index) throws ParseException {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        cal.setTime(format.parse(eventlist.get(index).getendTime()));
        return (cal.before(endcal))? cal:endcal;
    }

    public boolean isExist(Calendar scal, Calendar ecal){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String scalString = format.format(scal.getTime()), ecalString = format.format(ecal.getTime());
        String myquery = "SELECT * FROM " + dbtype + " WHERE start_time <= '" + ecalString + "' AND end_time >= '" + scalString + "'";
        Cursor c = db.rawQuery(myquery, null);
        int num = c.getCount();
        c.close();
        return (num > 0);
    }

    public Event getNowEvent(Calendar now) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for(int i = 0; i < eventlist.size(); ++i){
            Calendar scal = Calendar.getInstance(), ecal =  Calendar.getInstance();
            scal.setTime(format.parse(eventlist.get(i).getstrTime()));
            ecal.setTime(format.parse(eventlist.get(i).getendTime()));
            if(scal.before(now) && ecal.after(now)){
                return eventlist.get(i);
            }
        }
        return new Event();
    }

    public void clear(){
        eventlist.clear();
    }

    public void add(Event event){
        eventlist.add(event);
    }

    public void settTimetype(String timetype){
        ttype = timetype;
    }

    public void settDatatype(String datatype){
        dbtype = datatype;
    }

    public String getTtype(){
        return ttype;
    }

    public String getDbtype(){
        return dbtype;
    }
}
