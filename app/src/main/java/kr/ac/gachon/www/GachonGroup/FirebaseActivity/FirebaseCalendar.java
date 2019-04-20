package kr.ac.gachon.www.GachonGroup.FirebaseActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import kr.ac.gachon.www.GachonGroup.Calendar.EventdayDecorator;
import kr.ac.gachon.www.GachonGroup.R;
import kr.ac.gachon.www.GachonGroup.etc.Alert;

public class FirebaseCalendar extends AppCompatActivity {   //firebase를 이용한 달력
    final Context context;
    FirebaseDatabase database;
    final Alert alert;
    public FirebaseCalendar(Context context) {
        this.context=context;
        database=FirebaseDatabase.getInstance();
        alert=new Alert(context);
    }

    //달력에 이벤트 닷 추가
    public void Add_EventDay(final String GroupName, final MaterialCalendarView calendarView) {
        final HashSet<CalendarDay> days=new HashSet<>();
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.child("Groups").child(TrimName(GroupName)).child("Schedule").getChildren()) {   //해당 동아리 스케줄에 날짜 포함 시
                    String eventDate=snapshot.child("EventDate").getValue(String.class);
                    String dates[]=eventDate.split(",");
                    int year=Integer.parseInt(dates[0]);
                    int month=Integer.parseInt(dates[1])-1;
                    int day=Integer.parseInt(dates[2]);
                    days.add(CalendarDay.from(year, month, day));   //날짜 리스트 추가
                }
                calendarView.addDecorators(new EventdayDecorator(days));    //리스트에 속한 날짜에 닷 표시
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //특정 날짜 클릭 시 해당 날짜의 스케줄 표시
    public void Add_EventDayEvent(final String GroupName, final String Day, final LinearLayout layout, final TextView no_Schedule, final boolean isManager, final String userGroup) {
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                boolean exist=false;
                layout.removeAllViews();    //모든 뷰 제거
                layout.addView(no_Schedule);    //일정 없음 추가
                for(DataSnapshot snapshot:dataSnapshot.child("Groups").child(TrimName(GroupName)).child("Schedule").getChildren()) {
                    if(snapshot.child("EventDate").getValue(String.class).equals(Day)) {    //DB 상의 날짜와 일치하는 경우
                        final String date=(snapshot.child("EventDate").getValue(String.class).split(","))[2];
                        final String fullDate=snapshot.child("EventDate").getValue(String.class);
                        final String name=snapshot.child("EventName").getValue(String.class);
                        final String ScheduleID=Integer.toString(snapshot.child("id").getValue(Integer.class));
                        final LayoutInflater inflater=LayoutInflater.from(context);
                        View sub=inflater.inflate(R.layout.sub_schedule, null);
                        TextView SchDate= sub.findViewById(R.id.schdateTV); //날짜 표시 TV
                        TextView SchName= sub.findViewById(R.id.schNameTV); //일정 이름 표시 TV
                        SchDate.setText(date+"일");
                        SchName.setText(name);
                        layout.addView(sub);    //화면에 추가
                        exist=true;
                        //해당 일정 추가
                        if(isManager&&userGroup.equals(GroupName)||userGroup.equals("관리자")) {    //관리자이며 자신의 동아리일 경우
                            sub.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {    //길게 눌러서
                                    ListView listView=new ListView(context);
                                    listView.setDivider(null);
                                    ArrayList<String> arrayList=new ArrayList<>();
                                    arrayList.add("삭제");    //삭제 표시
                                    arrayList.add("수정");    //수정 표시
                                    ArrayAdapter adapter=new ArrayAdapter(context, R.layout.dropown_item_custom, arrayList);
                                    listView.setAdapter(adapter);
                                    listView.setDivider(null);
                                    AlertDialog.Builder builder=new AlertDialog.Builder(context);
                                    builder.setView(listView);
                                    final AlertDialog dialog=builder.create();
                                    dialog.show();
                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            Alert alert=new Alert(context);
                                            switch (position) {
                                                case 0: //삭제 선택 시
                                                    alert.MsgDialogChoice("일정을 삭제하시겠습니까?", new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            removeSchedule(GroupName,  fullDate, name);  //일정 삭제
                                                            Alert.dialog.cancel();
                                                        }
                                                    });
                                                    dialog.cancel();
                                                    break;
                                                case 1: //수정 선택 시
                                                    final AlertDialog inputDialog;
                                                    AlertDialog.Builder builder=new AlertDialog.Builder(context);
                                                    View layout=inflater.inflate(R.layout.dialog_input_new_schedule, null);
                                                    builder.setView(layout);
                                                    final EditText new_Schedule_name=layout.findViewById(R.id.new_schedule_nameET);
                                                    new_Schedule_name.setText(name);    //기종 일정명 출력
                                                    Button ok=layout.findViewById(R.id.okay);
                                                    final DatePicker datePicker=layout.findViewById(R.id.date_picker);
                                                    String[] dates=fullDate.split(","); //기존 일정
                                                    int year=Integer.parseInt(dates[0]), month=Integer.parseInt(dates[1])-1, date=Integer.parseInt(dates[2]);
                                                    datePicker.init(year, month, date, new DatePicker.OnDateChangedListener() { //원래 일정 날짜로 초기화
                                                        @Override
                                                        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                                        }
                                                    });

                                                    inputDialog=builder.create();
                                                    inputDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
                                                    inputDialog.show(); //수정 다이얼로그 출력

                                                    ok.setOnClickListener(new View.OnClickListener() {//수정 버튼 누르면
                                                        @Override
                                                        public void onClick(View v) {
                                                            String NewScheduleName=new_Schedule_name.getText().toString();
                                                            int year=datePicker.getYear();
                                                            int month=datePicker.getMonth()+1;
                                                            int date=datePicker.getDayOfMonth();
                                                            String NewDate=year+","+month+","+date;
                                                            if(NewScheduleName.length()!=0) {
                                                                updateSchedule(GroupName, ScheduleID, NewDate, NewScheduleName);    //일정 업데이트
                                                                inputDialog.cancel();
                                                            } else {    //일정명이 입력되지 않으면
                                                                Toast.makeText(context, "일정명을 입력해주세요", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                    //투명 설정 필요
                                                    dialog.cancel();
                                                    break;
                                            }
                                        }
                                    });
                                    return false;
                                }
                            });
                        }
                    }
                }
                if (exist) no_Schedule.setVisibility(View.GONE);
                else no_Schedule.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //스케줄 삭제 메서드
    private void removeSchedule(String Group, final String EventDate, final String EventName) {
        final DatabaseReference reference=database.getReference().child("Groups").child(Group).child("Schedule");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()) { //모든 일정 불러오기
                    String date=snapshot.child("EventDate").getValue(String.class);    //일정 날짜
                    String name=snapshot.child("EventName").getValue(String.class); //일정 이름
                    if(date.equals(EventDate)&&name.equals(EventName)) {    //이름과 날짜가 일치하는 경우
                        snapshot.getRef().setValue(null);   //삭제
                        Toast.makeText(context, "일정이 삭제되었습니다", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //스케줄 수정
    private void updateSchedule(String Group, String ID, final String NewDate, final String NewName) {
        DatabaseReference reference=database.getReference().child("Groups").child(Group).child("Schedule").child(ID);
        reference.child("EventName").setValue(NewName);
        reference.child("EventDate").setValue(NewDate);
        alert.MsgDialog("일정이 수정되었습니다");
    }

    //일정 추가
    public void AddEvent(final String groupName, int year, int month, int day, final String EventName) {
        final String EventDate=year+","+month+","+day;
        if(year==0||month==0||day==0) { //날짜가 선택되지 않은 경우
            Toast.makeText(context, "날짜를 선택해 주세요", Toast.LENGTH_SHORT).show();
            return; //종료
        }
        final DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count=0;
                for(DataSnapshot snapshot: dataSnapshot.child("Groups").child(groupName).child("Schedule").getChildren())   //일정의 마지막으로
                    count=snapshot.child("id").getValue(Integer.class)+1;
                reference.child("Groups").child(groupName).child("Schedule").child(Integer.toString(count)).child("id").setValue(count);
                reference.child("Groups").child(groupName).child("Schedule").child(Integer.toString(count)).child("EventDate").setValue(EventDate); //날짜
                reference.child("Groups").child(groupName).child("Schedule").child(Integer.toString(count)).child("EventName").setValue(EventName); //이름 추가
                Toast.makeText(context, "일정이 추가되었습니다 ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    //"."을 포함하는 문자를 공백으로 대체
    private String TrimName(String str) {
        if(str.contains("."))
            str=str.replace(".", "");
        return str;
    }

    private long LongDate(Date date) {  //Date를 long으로 변환
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMdd");
        String str=simpleDateFormat.format(date);
        long result=Long.parseLong(str);
        return result;
    }

}
