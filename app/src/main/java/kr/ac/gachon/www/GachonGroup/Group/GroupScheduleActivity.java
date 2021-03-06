package kr.ac.gachon.www.GachonGroup.Group;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import kr.ac.gachon.www.GachonGroup.Calendar.OneDayDecorator;
import kr.ac.gachon.www.GachonGroup.Calendar.SaturdayDecorator;
import kr.ac.gachon.www.GachonGroup.Calendar.SundayDecorator;
import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseCalendar;
import kr.ac.gachon.www.GachonGroup.R;
import kr.ac.gachon.www.GachonGroup.etc.Alert;

public class GroupScheduleActivity extends AppCompatActivity {  //동아리 일정 액티비티
    TextView groupNameTV;
    MaterialCalendarView calendar;
    LinearLayout scheduleLayout;
    TextView noScheduleTV;
    FirebaseCalendar firebaseCalendar;
    private Button AddScheduleBtn;
    private int Year, Month, Day;
    private boolean is_manager;
    private String groupName, userGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_schedule);

        //데이터 받아오기
        Intent intent=getIntent();
        groupName=intent.getStringExtra("groupName");   //동아리 이름
        is_manager=intent.getBooleanExtra("is_manager", false); //관리자 여부
        userGroup=intent.getStringExtra("userGroup");   //사용자의 동아리

        groupNameTV= findViewById(R.id.groupNameTV);
        groupNameTV.setText(groupName);

        calendar= findViewById(R.id.Calendar);
        calendar.addDecorators(new SundayDecorator(), new SaturdayDecorator(), new OneDayDecorator());  //달력에 표시될 장식 추가

        scheduleLayout= findViewById(R.id.group_schedule_layout);
        noScheduleTV= findViewById(R.id.no_scheduleTV);
        AddScheduleBtn=findViewById(R.id.AddScheduleBtn);

        firebaseCalendar=new FirebaseCalendar(GroupScheduleActivity.this);
        init();
        //관리자이며 자신의 동아리 일 경우 일정 추가 활성화  //관리자 포함
        if(is_manager&&userGroup.equals(groupName)||userGroup.equals("관리자")) WriteAble();
    }
    //초기화
    private void init() {
        //특정 날짜 클릭 시 해당하는 일정 출력
        calendar.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                Year = date.getYear();
                Month = date.getMonth() + 1;
                Day = date.getDay();

                String shot_Day = Year + "," + Month + "," + Day;

                firebaseCalendar.Add_EventDayEvent(groupName, shot_Day, scheduleLayout, noScheduleTV, is_manager, userGroup);
                calendar.clearSelection();
            }
        });
        firebaseCalendar.Add_EventDay(groupName, calendar); //동아리에 해당하는 일정 추가
    }

    //일정 추가 활성화
    private void WriteAble() {
        AddScheduleBtn.setVisibility(View.VISIBLE);
        AddScheduleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //레이아웃의 모든 뷰 제거
                scheduleLayout.removeAllViews();
                //일정 추가용 레이아웃 활성화
                LayoutInflater inflater=getLayoutInflater();
                View InputLayout=inflater.inflate(R.layout.sub_add_schedule, null);
                final EditText scheduleET=InputLayout.findViewById(R.id.ScheduleET);
                Button AddScheduleBtn=InputLayout.findViewById(R.id.WriteScheduleBtn);
                scheduleLayout.addView(InputLayout);
                //추가 버튼
                AddScheduleBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Alert alert=new Alert(GroupScheduleActivity.this);
                        alert.MsgDialogChoice("작성한 내용을\n등록하시겠습니까?", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String EventName=scheduleET.getText().toString();
                                if(EventName.length()==0) Toast.makeText(GroupScheduleActivity.this, "일정을 입력해주세요", Toast.LENGTH_SHORT).show();
                                else
                                    try {
                                        firebaseCalendar.AddEvent(groupName, Year, Month, Day, EventName);  //DB에 등록
                                        init();
                                    } catch (Exception e) {
                                    }
                                    Alert.dialog.cancel();
                            }
                        });
                    }
                });
            }
        });
    }


    public void close(View v) {
        finish();
    }

}
