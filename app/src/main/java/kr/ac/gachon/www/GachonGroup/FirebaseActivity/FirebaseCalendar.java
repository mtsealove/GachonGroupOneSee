package kr.ac.gachon.www.GachonGroup.FirebaseActivity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import java.util.ArrayList;
import java.util.HashSet;

import kr.ac.gachon.www.GachonGroup.Calendar.EventdayDecorator;
import kr.ac.gachon.www.GachonGroup.R;
import kr.ac.gachon.www.GachonGroup.etc.Alert;

public class FirebaseCalendar extends AppCompatActivity {
    final Context context;
    FirebaseDatabase database;
    public FirebaseCalendar(Context context) {
        this.context=context;
        database=FirebaseDatabase.getInstance();
    }

    //달력에 이벤트 닷 추가
    public void Add_EventDay(final String GroupName, final MaterialCalendarView calendarView) {
        final HashSet<CalendarDay> days=new HashSet<>();
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.child("Groups").child(TrimName(GroupName)).child("Schedule").getChildren()) {
                    String eventDate=snapshot.child("EventDate").getValue(String.class);
                    String dates[]=eventDate.split(",");
                    int year=Integer.parseInt(dates[0]);
                    int month=Integer.parseInt(dates[1])-1;
                    int day=Integer.parseInt(dates[2]);
                    days.add(CalendarDay.from(year, month, day));
                }
                calendarView.addDecorators(new EventdayDecorator(days));
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
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean exist=false;
                layout.removeAllViews();
                layout.addView(no_Schedule);
                for(DataSnapshot snapshot:dataSnapshot.child("Groups").child(TrimName(GroupName)).child("Schedule").getChildren()) {
                    if(snapshot.child("EventDate").getValue(String.class).equals(Day)) {
                        String date=(snapshot.child("EventDate").getValue(String.class).split(","))[2];
                        final String fullDate=snapshot.child("EventDate").getValue(String.class);
                        final String name=snapshot.child("EventName").getValue(String.class);
                        LayoutInflater inflater=LayoutInflater.from(context);
                        View sub=inflater.inflate(R.layout.sub_schedule, null);
                        TextView SchDate= sub.findViewById(R.id.schdateTV);
                        TextView SchName= sub.findViewById(R.id.schNameTV);
                        SchDate.setText(date+"일");
                        SchName.setText(name);
                        layout.addView(sub);
                        exist=true;
                        //관리자이며 자신의 동아리일 경우
                        if(isManager&&userGroup.equals(GroupName)) {
                            sub.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    ListView listView=new ListView(context);
                                    listView.setDivider(null);
                                    ArrayList<String> arrayList=new ArrayList<>();
                                    arrayList.add("삭제");
                                    ArrayAdapter adapter=new ArrayAdapter(context, R.layout.dropown_item_custom, arrayList);
                                    listView.setAdapter(adapter);
                                    AlertDialog.Builder builder=new AlertDialog.Builder(context);
                                    builder.setView(listView);
                                    final AlertDialog dialog=builder.create();
                                    dialog.show();
                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            Alert alert=new Alert(context);
                                            switch (position) {
                                                case 0:
                                                    alert.MsgDialogChoice("일정을 삭제하시겠습니까?", new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            removeSchedule(GroupName,  fullDate, name);
                                                            Alert.dialog.cancel();
                                                        }
                                                    });
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
                for(DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    String date=snapshot.child("EventDate").getValue(String.class);
                    String name=snapshot.child("EventName").getValue(String.class);
                    if(date.equals(EventDate)&&name.equals(EventName)) {
                        snapshot.getRef().setValue(null);
                        Toast.makeText(context, "일정이 삭제되었습니다", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //관리자용 스케줄 추가
    public void AddEvent(final String groupName, int year, int month, int day, final String EventName) {
        final String EventDate=year+","+month+","+day;
        if(year==0||month==0||day==0) {
            Toast.makeText(context, "날짜를 선택해 주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        final DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count=0;
                for(DataSnapshot snapshot: dataSnapshot.child("Groups").child(groupName).child("Schedule").getChildren())
                    count=snapshot.child("id").getValue(Integer.class)+1;
                reference.child("Groups").child(groupName).child("Schedule").child(Integer.toString(count)).child("id").setValue(count);
                reference.child("Groups").child(groupName).child("Schedule").child(Integer.toString(count)).child("EventDate").setValue(EventDate);
                reference.child("Groups").child(groupName).child("Schedule").child(Integer.toString(count)).child("EventName").setValue(EventName);
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

}
