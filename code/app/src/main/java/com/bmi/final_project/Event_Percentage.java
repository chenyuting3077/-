package com.bmi.final_project;

import java.util.HashMap;
import java.util.Map;

public class Event_Percentage {
    private Map<String, Integer> index = new HashMap<>();
    private double[] value = new double[9];
    private double[] percentage = new double[9];
    private double total = 0.0;
    public Event_Percentage(){
        index.put("EAT", 0);
        index.put("READ", 1);
        index.put("MEETING", 2);
        index.put("SLEEP", 3);
        index.put("GAME", 4);
        index.put("ACTIVITY", 5);
        index.put("SOCIAL", 6);
        index.put("SPORT", 7);
        index.put("CLEAN", 8);
    }
    public void addData(String key, double val){
        value[index.get(key)] += val;
        total = 0;
        cal();
    }
    public double getData(String ind){
        return percentage[index.get(ind)];
    }
    public void cal(){
        for(int i = 0; i < 9; ++i){
            total = total + value[i];
        }
        for(int i = 0; i < 9; ++i){
            percentage[i] = value[i] / total;
        }
    }
}
