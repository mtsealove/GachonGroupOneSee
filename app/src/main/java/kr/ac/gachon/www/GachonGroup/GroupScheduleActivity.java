package kr.ac.gachon.www.GachonGroup;

import android.content.Intent;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.List;

import kr.ac.gachon.www.GachonGroup.Calendar.OneDayDecorator;
import kr.ac.gachon.www.GachonGroup.Calendar.SaturdayDecorator;
import kr.ac.gachon.www.GachonGroup.Calendar.SundayDecorator;
import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseCalendar;
import kr.ac.gachon.www.GachonGroup.modules.FirebaseHelper;

public class GroupScheduleActivity extends AppCompatActivity {
    TextView groupNameTV;
    MaterialCalendarView calendar;
    LinearLayout scheduleLayout;
    TextView noScheduleTV;
    FirebaseCalendar firebaseCalendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_schedule);

        Intent intent=getIntent();
        final String groupName=intent.getStringExtra("groupName");
        groupNameTV= findViewById(R.id.groupNameTV);
        groupNameTV.setText(groupName);

        calendar= findViewById(R.id.Calendar);
        calendar.addDecorators(new SundayDecorator(), new SaturdayDecorator(), new OneDayDecorator());

        scheduleLayout= findViewById(R.id.group_schedule_layout);
        noScheduleTV= findViewById(R.id.no_scheduleTV);

        firebaseCalendar=new FirebaseCalendar(GroupScheduleActivity.this);
        //특정 날짜 클릭 이벤트
        calendar.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                int Year = date.getYear();
                int Month = date.getMonth() + 1;
                int Day = date.getDay();

                String shot_Day = Year + "," + Month + "," + Day;

                FirebaseHelper helper=new FirebaseHelper();
                firebaseCalendar.Add_EventDayEvent(groupName, shot_Day, scheduleLayout, noScheduleTV);
                calendar.clearSelection();
            }
        });
        firebaseCalendar.Add_EventDay(groupName, calendar);

    }

    public void close(View v) {
        finish();
    }

}
