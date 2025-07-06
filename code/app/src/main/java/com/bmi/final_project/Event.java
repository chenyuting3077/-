package com.bmi.final_project;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Event {
    private String name = "";
    private String category;
    private String strTime;
    private String endTime;
    private String note;
    private float percent;
    private Boolean notify;
    private Boolean repeat;
    public Event(String Name, String cata, String sTime, String eTime, String Note){
        name = Name;
        category = cata;
        strTime = sTime;
        endTime = eTime;
        note = Note;
    }public Event(Event event){
        name = event.name;
        category = event.category;
        strTime = event.strTime;
        endTime = event.endTime;
        note = event.note;
        percent = event.percent;
    }
    public Event(){}
    public String getname(){return name;}
    public String getcategory(){return category;}
    public String getstrTime(){return strTime;}
    public String getendTime(){return endTime;}
    public String getnote(){return note;}
    public float getpercent(){return percent;}
    public void setPercent(float val){
        percent = val;
    }
    public Calendar getStartCalendar() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(format.parse(strTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return cal;
    }

    public Calendar getEndCalendar() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(format.parse(endTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return cal;
    }
}
