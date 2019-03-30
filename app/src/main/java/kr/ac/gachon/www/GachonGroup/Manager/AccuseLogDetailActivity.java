package kr.ac.gachon.www.GachonGroup.Manager;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import kr.ac.gachon.www.GachonGroup.Entity.Accuse;
import kr.ac.gachon.www.GachonGroup.R;

public class AccuseLogDetailActivity extends AppCompatActivity {
    TextView authorTV, boardNameTV, groupTV,  reasonTV, replyTV, contentTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accuse_log_detail);

        authorTV=findViewById(R.id.authorTV);
        boardNameTV=findViewById(R.id.boardTV);
        groupTV=findViewById(R.id.groupTV);
        reasonTV=findViewById(R.id.reasonTV);
        replyTV=findViewById(R.id.replyTV);
        contentTV=findViewById(R.id.contentTV);

        Intent getIntent=getIntent();
        final int AccuseID=getIntent.getIntExtra("AccuseID", 0);

        final FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference reference=database.getReference().child("Accuse").child(Integer.toString(AccuseID));
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String BoardID=dataSnapshot.child("BoardID").getValue(String.class);
                String BoardName=dataSnapshot.child("BoardName").getValue(String.class);
                String Reason=dataSnapshot.child("Reason").getValue(String.class);
                String userID=dataSnapshot.child("UserID").getValue(String.class);
                String ReplyID=dataSnapshot.child("ReplyID").getValue(String.class);
                String GroupName=dataSnapshot.child("GroupName").getValue(String.class);
                authorTV.setText("신고자: "+userID);
                boardNameTV.setText("게시판: "+BoardName);
                if(GroupName==null) groupTV.setVisibility(View.GONE);
                else groupTV.setText("동아리: "+GroupName);
                reasonTV.setText("게시글 ID: "+BoardID);
                contentTV.setText(Reason);
                if(ReplyID!=null) {
                    replyTV.setVisibility(View.VISIBLE);
                    replyTV.setText("댓글 ID: "+ReplyID);
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
