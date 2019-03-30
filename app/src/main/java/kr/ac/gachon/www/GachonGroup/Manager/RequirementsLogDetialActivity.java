package kr.ac.gachon.www.GachonGroup.Manager;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import kr.ac.gachon.www.GachonGroup.R;

public class RequirementsLogDetialActivity extends AppCompatActivity {
    TextView titleTV, authorTV, emailTV, contentTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requirements_log_detial);

        titleTV=findViewById(R.id.titleTV);
        emailTV=findViewById(R.id.emailTV);
        authorTV=findViewById(R.id.authorTV);
        contentTV=findViewById(R.id.contentTV);
        Intent getIntent=getIntent();
        String ID=getIntent.getStringExtra("ID");

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference reference=database.getReference().child("Requirements").child(ID);
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
    }
}
