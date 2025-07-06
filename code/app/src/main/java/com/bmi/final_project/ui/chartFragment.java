package com.bmi.final_project.ui;

import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bmi.final_project.ChartEventAdapter;
import com.bmi.final_project.Event;
import com.bmi.final_project.EventList;
import com.bmi.final_project.Event_Percentage;
import com.bmi.final_project.R;
import com.bmi.final_project.ui.home.HomeViewModel;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class chartFragment extends Fragment {
    private enum ChartTimeMode {
        DATE, MONTH, WEEK, YEAR
    }

    private enum ChartActualMode {
        ACTUAL, Prepare;
    }

    long[] value = {0, 0, 0, 0, 0, 0, 0, 0, 0};
    long total = 0;
    private String[] typename = {"用餐", "閱讀", "會議", "休息", "娛樂", "活動", "社交", "運動", "打掃"};
    private String[] type = {"EAT", "READ", "MEETING", "SLEEP", "GAME", "ACTIVITY", "SOCIAL", "SPORT", "CLEAN"};
    private ChartActualMode chartActualMode;
    private ChartTimeMode chartTimeMode;
    private HomeViewModel homeViewModel;
    private PieChart pieChart;
    private Button btnActual, btnPrepare;
    private Button btnDate, btnMonth, btnYear, btnWeek;
    private RecyclerView recyclerView;
    private TextView text_Date;
    private SQLiteDatabase db;
    private final EventList eventList = new EventList();
    private ChartEventAdapter CEA;
    private Handler handler;
    private EventList eventlist;
    private FloatingActionButton fab;
    private String timetype = "Day", datatype = "Event";
    public static final int[] COLORFUL_COLORS = {  //新增顏色
            Color.parseColor("#FF6A6A"),
            Color.parseColor("#B4FFAF"),
            Color.parseColor("#95D5FF"),
            Color.parseColor("#8678FF"),
            Color.parseColor("#FFFF37"),
            Color.parseColor("#FFAFFC"),
            Color.parseColor("#FFCD57"),
            Color.parseColor("#6BFFFF"),
            Color.parseColor("#C1A454"),
    };
    private Calendar calendar = Calendar.getInstance();
    private double percentage = 0.0;
    private double[] typeVal = new double[9];

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_chart, container, false);
        findView(root);
        setOnclickListener();
        /*init*/
        chartActualMode = ChartActualMode.Prepare;
        chartTimeMode = ChartTimeMode.DATE;
        /**/
        chartActualModeChange();
        chartTimeModeChange();
        text_Date.addTextChangedListener(textWatcher);
        try {
            updatedata();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return root;
    }
    public void onStop() {
        super.onStop();
        text_Date.removeTextChangedListener(textWatcher);
    }
    TextWatcher textWatcher = new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    updatedata();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
    };

    public void findView(View root) {
        pieChart = root.findViewById(R.id.pieChart);
        btnActual = root.findViewById(R.id.btn_actual);
        btnPrepare = root.findViewById(R.id.btn_prepare);
        btnDate = root.findViewById(R.id.btn_date);
        btnMonth = root.findViewById(R.id.btn_month);
        btnWeek = root.findViewById(R.id.btn_week);
        btnYear = root.findViewById(R.id.btn_year);
        recyclerView = root.findViewById(R.id.recycle_event);
        text_Date = getActivity().findViewById(R.id.text_barDate);
    }


    /*====================ModeChange==============================================================*/
    public void chartActualModeChange() {
        if (chartActualMode.equals(ChartActualMode.ACTUAL)) {// 實際執行
            btnActual.setBackgroundResource(R.drawable.chart_selected_background);
            btnPrepare.setBackgroundResource(R.drawable.chart_unselected_background);
        } else {//預先規劃
            btnPrepare.setBackgroundResource(R.drawable.chart_selected_background);
            btnActual.setBackgroundResource(R.drawable.chart_unselected_background);
        }
    }

    public void updatedata() throws ParseException {
        if (getContext() == null){
            return;
        }
        SimpleDateFormat barformat = new SimpleDateFormat("yyyy年MM月dd日");
        try {
            calendar.setTime(barformat.parse(text_Date.getText().toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        eventlist = new EventList(timetype, datatype, getContext(), calendar);
        value = new long[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
        total = 0;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar scal = Calendar.getInstance(), ecal = Calendar.getInstance();
        for (int i = 0; i < eventlist.getCounts(); ++i) {
            int typeid = eventlist.getTypeId(i);
            scal = eventlist.getLowerTime(i);
            ecal = eventlist.getUpperTime(i);
            long during = (ecal.getTimeInMillis() - scal.getTimeInMillis()) / 60000;
            total += during;
            value[typeid] += during;
        }
        eventList.clear();
        for (int i = 0, ind = 0; i < 9; ++i) {
            if (value[i] != 0) {
                eventList.add(new Event(typename[i], type[i], "", "", ""));
                eventList.getevent(ind).setPercent((float) ((value[i] * 100) / total));
                ++ind;
            }
        }
        eventList.settTimetype(timetype);
        eventList.settDatatype(datatype);
        recyclerView.removeAllViewsInLayout();
        CEA = new ChartEventAdapter(getContext(), eventList);
        recyclerView.setAdapter(CEA);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        createPieChart();
    }

    public void createPieChart() {
        ArrayList<PieEntry> visitors = new ArrayList<>();
        int[] colors = new int[9];
        for (int i = 0, ind = 0; i < 9; ++i) {
            if (value[i] != 0) {
                visitors.add(new PieEntry(((float) value[i] / 60), typename[i]));
                colors[ind] = COLORFUL_COLORS[i];
                ++ind;
            }
        }

        PieDataSet pieDataSet = new PieDataSet(visitors, "各項時間比例(hr)");
        pieDataSet.setFormLineWidth(100f);
        pieDataSet.setColors(colors);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("各項時間比例(hr)");
        pieChart.animate();
        pieChart.invalidate();
    }

    public void chartTimeModeChange() {
        String colorTrans = "#00FFFFFF";
        String colorLightGreen = "#D8FF7B";
        btnDate.setBackgroundColor(Color.parseColor(colorTrans));
        btnMonth.setBackgroundColor(Color.parseColor(colorTrans));
        btnYear.setBackgroundColor(Color.parseColor(colorTrans));
        btnWeek.setBackgroundColor(Color.parseColor(colorTrans));

        if (chartTimeMode.equals(ChartTimeMode.DATE)) {// 實際執行
            btnDate.setBackgroundColor(Color.parseColor(colorLightGreen));
        } else if (chartTimeMode.equals(ChartTimeMode.MONTH)) {//預先規劃
            btnMonth.setBackgroundColor(Color.parseColor(colorLightGreen));
        } else if (chartTimeMode.equals((ChartTimeMode.YEAR))) {
            btnYear.setBackgroundColor(Color.parseColor(colorLightGreen));
        } else if (chartTimeMode.equals(ChartTimeMode.WEEK)) {
            btnWeek.setBackgroundColor(Color.parseColor(colorLightGreen));
        }
    }

    /*=============================OnclickListener Actual/Prepare=================================*/
    public void setOnclickListener() {
        btnActual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datatype = "Practical";
                chartActualMode = ChartActualMode.ACTUAL;
                chartActualModeChange();
                try {
                    updatedata();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        btnPrepare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datatype = "Event";
                chartActualMode = ChartActualMode.Prepare;
                chartActualModeChange();
                try {
                    updatedata();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timetype = "Day";
                chartTimeMode = ChartTimeMode.DATE;
                chartTimeModeChange();
                try {
                    updatedata();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        btnMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timetype = "Month";
                chartTimeMode = ChartTimeMode.MONTH;
                chartTimeModeChange();
                try {
                    updatedata();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        btnWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timetype = "Week";
                chartTimeMode = ChartTimeMode.WEEK;
                chartTimeModeChange();
                try {
                    updatedata();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        btnYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timetype = "Year";
                chartTimeMode = ChartTimeMode.YEAR;
                chartTimeModeChange();
                try {
                    updatedata();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}