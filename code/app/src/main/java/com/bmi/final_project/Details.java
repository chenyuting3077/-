package com.bmi.final_project;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Details {
    private ArrayList<Detail> details= new ArrayList<>();
    public Details(EventList eventList, String cate){
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        for(int i = 0; i < eventList.getCounts();++i){
            if (eventList.getevent(i).getcategory().equals(cate)) {
                Calendar cal = Calendar.getInstance(), start = Calendar.getInstance(), end = Calendar.getInstance();
                String t = "";
                long h = 0;
                try {
                    start = eventList.getLowerTime(i);
                    t = format.format(start.getTime());
                    end = eventList.getUpperTime(i);
                    h = (end.getTimeInMillis() - start.getTimeInMillis()) / 60000;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Boolean isfind = false;
                for (int j = 0; j < details.size(); ++j) {
                    if (details.get(j).time.equals(t)) {
                        details.get(j).addhr(h);
                        isfind = true;
                        break;
                    }
                }
                if(!isfind) {
                    details.add(new Detail(h, t, start));
                }
            }
        }
    }
    public Detail get(int index){
        return details.get(index);
    }

    public int getCounts(){
        return details.size();
    }

    public void clear(){
        details.clear();
    }

    public class Detail{
        private long hr = 0;
        private String time = "";
        private Calendar timeCal = Calendar.getInstance();
        public Detail(long h, String t, Calendar cal){
            hr = h;
            time = t;
            timeCal = cal;
        }
        public String getTime(){ return time; }
        public void addhr(long t){ hr += t; }
        public long gethr(){ return hr; }
        public float gethrF(){ return ((float)hr/(float)60.0);}
    }
}
