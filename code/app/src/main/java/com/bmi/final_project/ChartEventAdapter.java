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

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChartEventAdapter extends RecyclerView.Adapter<ChartEventAdapter.ViewHolder> {
    private Map<String, Integer> iconMap = new HashMap<>();
    private final EventList localDataSet;
    private Context mContext;
    private ImageView event_cata;
    private TextView event_name,event_percent;
    public ChartEventAdapter(Context context,EventList localDataSet) {
        this.localDataSet = localDataSet;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.recycle_item_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        event_name.setText(localDataSet.getevent(position).getname());
        event_cata.setImageResource(iconMap.get(localDataSet.getevent(position).getcategory()));
        event_percent.setText(localDataSet.getevent(position).getpercent() + "%");
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                MainActivity act = (MainActivity) view.getContext();
                NavController navController = Navigation.findNavController(act,R.id.nav_host_fragment);
                Bundle bundle = new Bundle();
                bundle.putString("category",localDataSet.getevent(position).getcategory());
                bundle.putString("percentage",localDataSet.getevent(position).getpercent()+"%");
                bundle.putString("timetype",localDataSet.getTtype());
                bundle.putString("datatype",localDataSet.getDbtype());
                navController.navigate(R.id.action_nav_chart_to_nav_content,bundle);
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
            initMap();
            event_cata = itemView.findViewById(R.id.event_cata);
            event_name = itemView.findViewById(R.id.event_name);
            event_percent = itemView.findViewById(R.id.event_percent);
        }
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

    public void clear(){
        localDataSet.clear();
    }
}