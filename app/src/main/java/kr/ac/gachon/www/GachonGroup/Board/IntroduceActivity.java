package kr.ac.gachon.www.GachonGroup.Board;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseView;
import kr.ac.gachon.www.GachonGroup.R;

public class IntroduceActivity extends AppCompatActivity {
    TextView introduceTV, locationTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce);
        locationTV= findViewById(R.id.locationTV);
        introduceTV= findViewById(R.id.introduceTV);

        Intent intent=getIntent();
        String group=intent.getStringExtra("group");

        FirebaseView firebaseView=new FirebaseView(IntroduceActivity.this);
        firebaseView.setStringTextView(introduceTV, "Groups", group, "introduce", "소개글이 없습니다");
        firebaseView.setStringTextView(locationTV, "Groups", group, "location", "위치 정보가 없습니다");
    }

    public void close(View v) {
        finish();
    }
}
