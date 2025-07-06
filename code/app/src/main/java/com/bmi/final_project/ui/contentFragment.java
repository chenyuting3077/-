package com.bmi.final_project.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bmi.final_project.ChartEventAdapter;
import com.bmi.final_project.Details;
import com.bmi.final_project.EventDetailsAdapter;
import com.bmi.final_project.EventList;
import com.bmi.final_project.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class contentFragment extends Fragment {
    private Map<String, Integer> iconMap = new HashMap<>();
    private Map<String, String> event_Title = new HashMap<>();
    private ImageView content_img;
    private TextView content_title,text_percent, text_barDate,text_complete_rate,text_error_rate;
    private String category = "", percentage = "", timetype = "Day", datatype = "Event";
    private EventList event,practial;
    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
    private EventDetailsAdapter EDA;
    private RecyclerView details_recyclrview;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_content, container, false);
        initMap();
        findView(root);
        text_barDate.addTextChangedListener(textWatcher);
        if (getArguments() != null) {
            category = this.getArguments().getString("category");
            percentage = this.getArguments().getString("percentage");
            timetype = this.getArguments().getString("timetype");
            datatype = this.getArguments().getString("datatype");
            content_img.setImageResource(iconMap.get(category));
            content_title.setText(event_Title.get(category));
            text_percent.setText(percentage);
        }
        try {
            showResult();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return root;
    }

    public void onStop() {
        super.onStop();
        text_barDate.removeTextChangedListener(textWatcher);
    }

    TextWatcher textWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {}
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                showResult();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    };

    public void findView(View root){
        content_img = root.findViewById(R.id.content_img);
        content_title = root.findViewById(R.id.content_title);
        text_percent = root.findViewById(R.id.text_percent);
        text_barDate = getActivity().findViewById(R.id.text_barDate);
        text_complete_rate = root.findViewById(R.id.text_complete_rate);
        text_error_rate = root.findViewById(R.id.text_error_rate);
        details_recyclrview = root.findViewById(R.id.details_recyclrview);
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
        event_Title.put("EAT","用餐");
        event_Title.put("READ","閱讀");
        event_Title.put("MEETING","會議");
        event_Title.put("SLEEP","休息");
        event_Title.put("GAME","娛樂");
        event_Title.put("ACTIVITY","活動");
        event_Title.put("SOCIAL","社交");
        event_Title.put("SPORT","運動");
        event_Title.put("CLEAN","打掃");
    }
    public void showResult() throws ParseException {
        if (getContext() == null){
            return;
        }
        try {
            calendar.setTime(format.parse(text_barDate.getText().toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        event = new EventList(timetype,"Event",getContext(),calendar);
        practial = new EventList(timetype,"Practical",getContext(),calendar);
        long reach = 0, miss = 0, total = 0;
        for (int i = 0 ; i < event.getCounts(); ++i){
            if(event.getevent(i).getcategory().equals(category)) {
                Calendar eScal = event.getevent(i).getStartCalendar();
                Calendar eEcal = event.getevent(i).getEndCalendar();
                for (int j = 0; j < practial.getCounts(); ++j) {
                    if (practial.getevent(j).getcategory().equals(category)) {
                    //if(practial.getevent(j).getname().equals("reading")){
                        Calendar pScal = practial.getevent(j).getStartCalendar();
                        Calendar pEcal = practial.getevent(j).getEndCalendar();
                        if (eScal.before(pEcal) && eEcal.after(pScal)) {
                            Calendar start, end;
                            if (eScal.before(pScal)) {
                                start = pScal;
                            } else {
                                start = eScal;
                            }
                            if (eEcal.after(pEcal)) {
                                end = pEcal;
                            } else {
                                end = eEcal;
                            }
                            reach += (end.getTimeInMillis() - start.getTimeInMillis()) / 60000;
                            miss += (Math.abs(eScal.getTimeInMillis() - pScal.getTimeInMillis()) / 60000);
                            miss += (Math.abs(eEcal.getTimeInMillis() - pEcal.getTimeInMillis()) / 60000);
                        }
                    }
                }
            }
        }
        total = reach + miss;
        if(total != 0){
            text_complete_rate.setText(String.valueOf((float) ((reach * 100) / total)) + "%");
            text_error_rate.setText(String.valueOf((float) ((miss * 100) / total)) + "%");
        }
        EventList eventlist = new EventList(timetype, datatype, getContext(), calendar);
        Details details = new Details(eventlist, category);
        details_recyclrview.removeAllViewsInLayout();
        EDA = new EventDetailsAdapter(getContext(), details);
        details_recyclrview.setAdapter(EDA);
        details_recyclrview.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
