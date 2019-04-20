package kr.ac.gachon.www.GachonGroup.Manager;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebasePost;
import kr.ac.gachon.www.GachonGroup.R;
import kr.ac.gachon.www.GachonGroup.etc.Alert;

public class RequirementsLogDetialActivity extends AppCompatActivity {
    TextView titleTV, authorTV, emailTV, contentTV;
    Button removeBtn;
    String RequirementID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requirements_log_detial);

        titleTV=findViewById(R.id.titleTV);
        emailTV=findViewById(R.id.emailTV);
        authorTV=findViewById(R.id.authorTV);
        contentTV=findViewById(R.id.contentTV);
        removeBtn=findViewById(R.id.removeBtn);

        Intent getIntent=getIntent();
        RequirementID=getIntent.getStringExtra("ID");


        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference reference=database.getReference().child("Requirements").child(RequirementID);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String content=dataSnapshot.child("content").getValue(String.class);
                String email=dataSnapshot.child("email").getValue(String.class);
                String title=dataSnapshot.child("title").getValue(String.class);
                String userID=dataSnapshot.child("userID").getValue(String.class);
                titleTV.setText("제목: "+title);
                emailTV.setText("이메일: "+email);
                authorTV.setText("글쓴이: " +userID);
                contentTV.setText(content);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteRequirement();
            }
        });
    }

    private void DeleteRequirement() {
        final FirebasePost firebasePost=new FirebasePost(this);
        Alert alert=new Alert(this);
        alert.MsgDialogChoice("삭제하시겠습니까?", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebasePost.RemoveRequire(RequirementID);
                Toast.makeText(RequirementsLogDetialActivity.this, "문의사항이 삭제되었습니다", Toast.LENGTH_SHORT).show();
                Alert.dialog.cancel();
                finish();
            }
        });
    }

    public void close(View v) {
        finish();
    }
}
