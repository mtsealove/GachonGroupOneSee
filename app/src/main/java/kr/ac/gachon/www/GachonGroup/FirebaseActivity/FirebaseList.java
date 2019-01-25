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

import kr.ac.gachon.www.GachonGroup.BoardActivity;
import kr.ac.gachon.www.GachonGroup.JoinRequsetLogDetailActivity;
import kr.ac.gachon.www.GachonGroup.R;
import kr.ac.gachon.www.GachonGroup.modules.Alert;
import kr.ac.gachon.www.GachonGroup.modules.JoinRequest;

public class FirebaseList extends AppCompatActivity {
    final Context context;
    FirebaseDatabase database;
    public FirebaseList(Context context) {
        this.context=context;
        database=FirebaseDatabase.getInstance();
    }

    //리스트뷰에 게시판의 타이틀 설정
    public void setListView(final String userID, final ListView listView, final String board_name) {
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> titles=new ArrayList<>();
                ArrayList<Integer> ids=new ArrayList<>();
                for(DataSnapshot snapshot: dataSnapshot.child(board_name).getChildren()) {
                    String title=snapshot.child("title").getValue(String.class);
                    int id=snapshot.child("id").getValue(Integer.class);
                    titles.add(title);
                    ids.add(id);
                }
                ArrayAdapter adapter=new ArrayAdapter(context, R.layout.dropown_item_custom, titles);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent=new Intent(context, BoardActivity.class);
                        intent.putExtra("boardName", board_name);
                        intent.putExtra("id", position);
                        intent.putExtra("userID", userID);
                        context.startActivity(intent);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //특정 그룹의 게시판 설정
    public void setGroupListView(final String groupName, final String userID, final ListView listView, final String board_name) {
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> titles=new ArrayList<>();
                ArrayList<Integer> ids=new ArrayList<>();
                String newName=TrimName(groupName);
                for(DataSnapshot snapshot: dataSnapshot.child("Groups").child(newName).child(board_name).getChildren()) {
                    String title=snapshot.child("title").getValue(String.class);
                    int id=snapshot.child("id").getValue(Integer.class);
                    titles.add(title);
                    ids.add(id);
                }
                ArrayAdapter adapter=new ArrayAdapter(context, R.layout.dropown_item_custom, titles);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent=new Intent(context, BoardActivity.class);
                        intent.putExtra("groupName", groupName);
                        intent.putExtra("boardName", board_name);
                        intent.putExtra("id", position);
                        intent.putExtra("userID", userID);
                        context.startActivity(intent);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //리스트뷰에 게시판의 타이틀 설정, 검색 값 설정
    public void setListView(final String userID, final ListView listView, final String board_name, final String value) {
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> titles=new ArrayList<>();
                final ArrayList<Integer> ids=new ArrayList<>();
                for(DataSnapshot snapshot: dataSnapshot.child(board_name).getChildren()) {
                    String title=snapshot.child("title").getValue(String.class);
                    if(title.contains(value)) {
                        int id = snapshot.child("id").getValue(Integer.class);
                        titles.add(title);
                        ids.add(id);
                    }
                }
                ArrayAdapter adapter=new ArrayAdapter(context, R.layout.dropown_item_custom, titles);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent=new Intent(context, BoardActivity.class);
                        intent.putExtra("boardName", board_name);
                        intent.putExtra("id", ids.get(position));
                        intent.putExtra("userID", userID);
                        context.startActivity(intent);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //특정 그룹의 리스트뷰에 게시판의 타이틀 설정, 검색 값 설정
    public void setGroupListView(final String groupName, final String userID, final ListView listView, final String board_name, final String value) {
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> titles=new ArrayList<>();
                final ArrayList<Integer> ids=new ArrayList<>();
                for(DataSnapshot snapshot: dataSnapshot.child("Groups").child(groupName).child(board_name).getChildren()) {
                    String title=snapshot.child("title").getValue(String.class);
                    if(title.contains(value)) {
                        int id = snapshot.child("id").getValue(Integer.class);
                        titles.add(title);
                        ids.add(id);
                    }
                }
                ArrayAdapter adapter=new ArrayAdapter(context, R.layout.dropown_item_custom, titles);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent=new Intent(context, BoardActivity.class);
                        intent.putExtra("groupName", groupName);
                        intent.putExtra("boardName", board_name);
                        intent.putExtra("id", ids.get(position));
                        intent.putExtra("userID", userID);
                        context.startActivity(intent);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    //동아리 가입 요청
    public void MakeJoinRequest(final JoinRequest joinRequest) {
        final DatabaseReference reference=database.getReference();
        //동아리 안의 JoinRequest에 데이터 삽입
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean already=false;
                for(DataSnapshot snapshot: dataSnapshot.child("JoinRequest").getChildren()) {
                    String dataID=snapshot.child("ID").getValue(String.class);
                    String dataGroup=snapshot.child("group").getValue(String.class);
                    //데이터에서 검색하여 ID와 동아리가 일치하는 신청내역 조회
                    if(dataGroup.equals(joinRequest.group)&&dataID.equals(joinRequest.ID)) already=true;
                }
                //만약 신청 내역이 있으면 토스트 출력
                if(already) Toast.makeText(context, "이미 신청한 동아리입니다", Toast.LENGTH_SHORT).show();
                else {
                    //데이터 삽입
                    reference.child("JoinRequest").push().setValue(joinRequest);
                    Toast.makeText(context, "신청이 완료되었습니다", Toast.LENGTH_SHORT).show();
                    ((Activity)context).finish();
                }
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
                        JoinRequest joinRequest=new JoinRequest(name, contact, StudentNumber, major, age, SelfIntroduce, ID, group);
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

    private String TrimName(String name) {
        name.replace(".", "");
        return name;
    }
}
