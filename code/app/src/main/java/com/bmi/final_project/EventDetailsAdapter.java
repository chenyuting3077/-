package com.bmi.final_project;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EventDetailsAdapter extends RecyclerView.Adapter<EventDetailsAdapter.ViewHolder> {
    private final Details localDataSet;
    private Context mContext;
    private TextView detail_time,detail_hour;
    public EventDetailsAdapter(Context context,Details localDataSet) {
        this.localDataSet = localDataSet;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.recycle_event_details, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        detail_time.setText(localDataSet.get(position).getTime());
        float hr = localDataSet.get(position).gethrF();
        hr = (float)(Math.round(hr*10))/10;
        detail_hour.setText(String.valueOf(hr)+"hr");
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return localDataSet.getCounts();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            View view = layoutInflater.inflate(R.layout.recycle_event_details, null, false);
            //View myview = getLayoutInflater().inflate(R.layout.recycle_event_details,null);
            detail_time = itemView.findViewById(R.id.detail_time);
            detail_hour = itemView.findViewById(R.id.detail_hour);
        }
    }

    public void clear(){
        localDataSet.clear();
    }
}