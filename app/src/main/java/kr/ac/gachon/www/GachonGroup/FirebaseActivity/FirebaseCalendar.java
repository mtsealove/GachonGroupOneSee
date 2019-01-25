package kr.ac.gachon.www.GachonGroup.FirebaseActivity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.HashSet;

import kr.ac.gachon.www.GachonGroup.Calendar.EventdayDecorator;
import kr.ac.gachon.www.GachonGroup.R;

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
                for(DataSnapshot snapshot: dataSnapshot.child("Groups").child(GroupName).child("Schedule").getChildren()) {
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
    //달력에 이벤트 리스너 추가
    public void Add_EventDayEvent(final String GroupName, final String Day, final LinearLayout layout, final TextView no_Schedule) {
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean exist=false;
                layout.removeAllViews();
                layout.addView(no_Schedule);
                for(DataSnapshot snapshot:dataSnapshot.child("Groups").child(GroupName).child("Schedule").getChildren()) {
                    if(snapshot.child("EventDate").getValue(String.class).equals(Day)) {
                        String date=(snapshot.child("EventDate").getValue(String.class).split(","))[2];
                        String name=snapshot.child("EventName").getValue(String.class);
                        LayoutInflater inflater=LayoutInflater.from(context);
                        View sub=inflater.inflate(R.layout.sub_schedule, null);
                        TextView SchDate= sub.findViewById(R.id.schdateTV);
                        TextView SchName= sub.findViewById(R.id.schNameTV);
                        SchDate.setText(date+"일");
                        SchName.setText(name);
                        layout.addView(sub);
                        exist=true;
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

}
