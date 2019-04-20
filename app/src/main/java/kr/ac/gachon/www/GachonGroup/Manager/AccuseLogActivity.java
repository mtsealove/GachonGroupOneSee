package kr.ac.gachon.www.GachonGroup.Manager;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import kr.ac.gachon.www.GachonGroup.Board.SearchActivity;
import kr.ac.gachon.www.GachonGroup.Entity.Accuse;
import kr.ac.gachon.www.GachonGroup.Entity.AccuseListAdapter;
import kr.ac.gachon.www.GachonGroup.R;

public class AccuseLogActivity extends AppCompatActivity {
    Button postBtn, searchBtn;
    ListView boardLV;
    TextView titleTV;
    String Value;
    public static Activity accuseLogActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        postBtn=findViewById(R.id.postBtn);
        boardLV=findViewById(R.id.boardLV);
        postBtn.setVisibility(View.GONE);
        searchBtn=findViewById(R.id.searchBtn);
        titleTV=findViewById(R.id.titleTV);
        titleTV.setText("신고 내역");
        accuseLogActivity=AccuseLogActivity.this;

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Search();
            }
        });
        Value=getIntent().getStringExtra("value");

        if(Value==null)getAccuseList();
        else getAccuseList(Value);
    }

    private void getAccuseList() {  //신고 내역 불러오기
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference reference=database.getReference().child("Accuse");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AccuseListAdapter accuseListAdapter=new AccuseListAdapter();
                final ArrayList<Accuse> accuseArrayList=new ArrayList<>();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {

                    String boardID=snapshot.child("BoardID").getValue(String.class);
                    String boardName=snapshot.child("BoardName").getValue(String.class);
                    String Reason=snapshot.child("Reason").getValue(String.class);
                    String ReplyId=snapshot.child("ReplyID").getValue(String.class);
                    String GroupName=snapshot.child("GroupName").getValue(String.class);
                    String userID=snapshot.child("UserID").getValue(String.class);
                    boolean read=false;
                    try {
                        read=snapshot.child("Read").getValue(Boolean.class);
                    } catch (Exception e) {
                    }
                    int accuseID=snapshot.child("id").getValue(Integer.class);
                    accuseArrayList.add(new Accuse(boardID, boardName, Reason,ReplyId, accuseID, GroupName));
                    accuseListAdapter.addItem(Reason, userID, read);
                }

                Collections.reverse(accuseArrayList);
                boardLV.setAdapter(accuseListAdapter);
                boardLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent=new Intent(AccuseLogActivity.this, AccuseLogDetailActivity.class);
                        Accuse accuse=accuseArrayList.get(position);
                        intent.putExtra("AccuseID",accuse.getAccuseID());
                        startActivity(intent);
                        SetRead(Integer.toString(accuse.getAccuseID()));

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getAccuseList(final String value) {  //신고 내역 불러오기(검색)
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference reference=database.getReference().child("Accuse");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AccuseListAdapter accuseListAdapter=new AccuseListAdapter();
                final ArrayList<Accuse> accuseArrayList=new ArrayList<>();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {

                    String boardID=snapshot.child("BoardID").getValue(String.class);
                    String boardName=snapshot.child("BoardName").getValue(String.class);
                    String Reason=snapshot.child("Reason").getValue(String.class);
                    String ReplyId=snapshot.child("ReplyID").getValue(String.class);
                    String GroupName=snapshot.child("GroupName").getValue(String.class);
                    String userID=snapshot.child("UserID").getValue(String.class);


                    boolean read=false;
                    try {
                        read=snapshot.child("Read").getValue(Boolean.class);
                    } catch (Exception e) {
                    }
                    int accuseID=snapshot.child("id").getValue(Integer.class);
                    try {
                    if(userID.contains(value)||boardName.contains(value)||Reason.contains(value)||(GroupName!=null&&GroupName.contains(value))) {   //아이디, 내용, 게시판, 이유가 포함되어 있을 때
                        accuseArrayList.add(new Accuse(boardID, boardName, Reason, ReplyId, accuseID, GroupName));
                        accuseListAdapter.addItem(Reason, userID, read);
                    } } catch (Exception e){

                    }
                }

                if(accuseArrayList.size()==0) {
                    ArrayList<String> arrayList=new ArrayList<>();
                    arrayList.add("검색 결과가 없습니다");
                    ArrayAdapter adapter=new ArrayAdapter(AccuseLogActivity.this, R.layout.dropown_item_custom, arrayList);
                    boardLV.setAdapter(adapter);
                }
                else {
                    Collections.reverse(accuseArrayList);
                    boardLV.setAdapter(accuseListAdapter);
                    boardLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(AccuseLogActivity.this, AccuseLogDetailActivity.class);
                            Accuse accuse = accuseArrayList.get(position);
                            intent.putExtra("AccuseID", accuse.getAccuseID());
                            startActivity(intent);
                            SetRead(Integer.toString(accuse.getAccuseID()));

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void Search() {
        Intent intent=new Intent(this, SearchActivity.class);
        intent.putExtra("BoardName", "Accuse");
        startActivity(intent);
    }

    private void SetRead(String accuseID) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Accuse").child(accuseID).child("Read");
        reference.setValue(true);
    }

    public void close(View v){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if(Value==null) finish();
        else {
            Intent intent=new Intent(this, AccuseLogActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
