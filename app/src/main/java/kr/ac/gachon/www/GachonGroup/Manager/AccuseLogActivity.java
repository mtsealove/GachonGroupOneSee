package kr.ac.gachon.www.GachonGroup.Manager;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.acl.Group;
import java.util.ArrayList;

import kr.ac.gachon.www.GachonGroup.Entity.Accuse;
import kr.ac.gachon.www.GachonGroup.R;

public class AccuseLogActivity extends AppCompatActivity {
    Button postBtn;
    ListView boardLV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        postBtn=findViewById(R.id.postBtn);
        boardLV=findViewById(R.id.boardLV);
        postBtn.setVisibility(View.GONE);

        getAccuseList();
    }

    private void getAccuseList() {  //신고 내역 불러오기
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference reference=database.getReference().child("Accuse");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final ArrayList<Accuse> accuseArrayList=new ArrayList<>();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {

                    String boardID=snapshot.child("BoardID").getValue(String.class);
                    String boardName=snapshot.child("BoardName").getValue(String.class);
                    String Reason=snapshot.child("Reason").getValue(String.class);
                    String ReplyId=snapshot.child("ReplyID").getValue(String.class);
                    String GroupName=snapshot.child("GroupName").getValue(String.class);
                    int accuseID=snapshot.child("id").getValue(Integer.class);
                    accuseArrayList.add(new Accuse(boardID, boardName, Reason,ReplyId, accuseID, GroupName));
                }
                ArrayList title=new ArrayList();
                for(Accuse accuses:accuseArrayList) {//타이틀로 추가
                    String result="";
                    if(accuses.getGroupName()!=null) result+=accuses.getGroupName()+" ";
                    if(accuses.getBoardName()!=null) result+=accuses.getBoardName();
                    if(accuses.getReplyID()!=null) result+=" 댓글";
                    title.add(result);
                }
                ArrayAdapter adapter=new ArrayAdapter(AccuseLogActivity.this, R.layout.dropown_item_custom, title);
                boardLV.setAdapter(adapter);
                boardLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent=new Intent(AccuseLogActivity.this, AccuseLogDetailActivity.class);
                        Accuse accuse=accuseArrayList.get(position);
                        intent.putExtra("AccuseID",accuse.getAccuseID());
                        startActivity(intent);

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
