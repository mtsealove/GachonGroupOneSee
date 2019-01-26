package kr.ac.gachon.www.GachonGroup.FirebaseActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import kr.ac.gachon.www.GachonGroup.JoinRequest.JoinRequsetLogDetailActivity;
import kr.ac.gachon.www.GachonGroup.R;
import kr.ac.gachon.www.GachonGroup.modules.Alert;
import kr.ac.gachon.www.GachonGroup.Entity.JoinRequest;

public class FirebaseJoinRequest extends AppCompatActivity {
    FirebaseDatabase database;
    final Context context;
    public FirebaseJoinRequest(Context context) {
        this.context=context;
        database=FirebaseDatabase.getInstance();
    }
    //동아리 가입 요청
    public void MakeJoinRequest(final JoinRequest joinRequest) {
        final DatabaseReference reference=database.getReference();
        //동아리 안의 JoinRequest에 데이터 삽입
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //데이터 삽입
                    reference.child("JoinRequest").push().setValue(joinRequest);
                    Toast.makeText(context, "신청이 완료되었습니다", Toast.LENGTH_SHORT).show();
                    ((Activity)context).finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //동아리 가입 신청 내역을 ListView에 표시
    public void JoinRequestLog(final ListView listView, final String ID) {
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //제목 표시용
                ArrayList<String> title=new ArrayList<>();
                //수정 및 삭제 액티비티로 보낼 내용
                final ArrayList<JoinRequest> joinRequests=new ArrayList<>();
                for(DataSnapshot snapshot:dataSnapshot.child("JoinRequest").getChildren()) {
                    if(snapshot.child("ID").getValue(String.class).equals(ID)) {
                        //타이틀에 추가
                        title.add(snapshot.child("group").getValue(String.class));
                        //JoinRequests에 추가
                        String SelfIntroduce=snapshot.child("SelfIntroduce").getValue(String.class);
                        int StudentNumber=snapshot.child("StudentNumber").getValue(Integer.class);
                        int age=snapshot.child("age").getValue(Integer.class);
                        String contact=snapshot.child("contact").getValue(String.class);
                        String group=snapshot.child("group").getValue(String.class);
                        String major=snapshot.child("major").getValue(String.class);
                        String name=snapshot.child("name").getValue(String.class);
                        String AbleTime=snapshot.child("AbleTime").getValue(String.class);
                        JoinRequest joinRequest=new JoinRequest(name, contact, StudentNumber, major, age, SelfIntroduce, ID, group, AbleTime);
                        joinRequests.add(joinRequest);
                    }
                }
                //어댑터 설정
                ArrayAdapter adapter=new ArrayAdapter(context, R.layout.dropown_item_custom, title);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //수정 및 삭제 액티비티로 데이터 전달
                        Intent intent=new Intent(context, JoinRequsetLogDetailActivity.class);
                        intent.putExtra("ID", joinRequests.get(position).ID);
                        intent.putExtra("SelfIntroduce", joinRequests.get(position).SelfIntroduce);
                        intent.putExtra("StudentNumber", joinRequests.get(position).StudentNumber);
                        intent.putExtra("age", joinRequests.get(position).age);
                        intent.putExtra("contact", joinRequests.get(position).contact);
                        intent.putExtra("group", joinRequests.get(position).group);
                        intent.putExtra("major", joinRequests.get(position).major);
                        intent.putExtra("name", joinRequests.get(position).name);
                        intent.putExtra("AbleTime", joinRequests.get(position).AbleTime);
                        context.startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //동아리 가입 신청 취소
    public void RemoveJoinRequest(final String ID, final String Group) {
        DatabaseReference reference=database.getReference().child("JoinRequest");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    DatabaseReference reference1=snapshot.getRef();
                    String dataID=snapshot.child("ID").getValue(String.class);
                    String dataGroup=snapshot.child("group").getValue(String.class);
                    //ID와 동아리가 일치하는 데이터를 삭제
                    if(ID.equals(dataID)&&Group.equals(dataGroup)) {
                        reference1.setValue(null);
                        Alert alert=new Alert(context);
                        alert.MsgDialogEnd("가입신청이 최소되었습니다");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //이미 가입되어 있거나 신청한 동아리인지 판단
    public void CheckAlreadyRequested(final String ID, final String Group) {
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Account").child(ID).child("group").getValue(String.class).equals(Group)) {
                    Toast.makeText(context, "이미 가입된 동아리입니다", Toast.LENGTH_SHORT).show();
                    ((Activity)context).finish();
                } else {
                    for (DataSnapshot snapshot : dataSnapshot.child("JoinRequest").getChildren()) {
                        String dataID = snapshot.child("ID").getValue(String.class);
                        String dataGroup = snapshot.child("group").getValue(String.class);
                        if (ID.equals(dataID) && dataGroup.equals(Group)) {
                            Toast.makeText(context, "이미 가입 신청한 동아리 입니다\n동아리 신청 내역을 확인해 보세요", Toast.LENGTH_SHORT).show();
                            ((Activity) context).finish();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //가입 신청 업데이트
    public void UpdateJoinRequest(final String ID, final String Group, final JoinRequest joinRequest) {
        final DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.child("JoinRequest").getChildren()) {
                    String dataID=snapshot.child("ID").getValue(String.class);
                    String dataGroup=snapshot.child("group").getValue(String.class);
                    if(dataID.equals(ID)&&dataGroup.equals(Group)) {
                        DatabaseReference databaseReference=snapshot.getRef();
                        databaseReference.setValue(null);
                        MakeJoinRequest(joinRequest);
                        JoinRequsetLogDetailActivity._JoinRequestLogDetailActivity.finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
