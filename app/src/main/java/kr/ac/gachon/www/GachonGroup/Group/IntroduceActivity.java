package kr.ac.gachon.www.GachonGroup.Group;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseView;
import kr.ac.gachon.www.GachonGroup.R;

public class IntroduceActivity extends AppCompatActivity {
    private TextView introduceTV, locationTV;
    private Button functionBtn;
    private String group, userGroup;
    private boolean is_manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce);
        locationTV= findViewById(R.id.locationTV);
        introduceTV= findViewById(R.id.introduceTV);
        functionBtn=findViewById(R.id.functionBtn);

        Intent intent=getIntent();
        group=intent.getStringExtra("group");
        is_manager=intent.getBooleanExtra("is_manager", false);
        userGroup=intent.getStringExtra("userGroup");

    }

    private void init() {
        FirebaseView firebaseView=new FirebaseView(IntroduceActivity.this);
        firebaseView.setStringTextView(introduceTV, "Groups", group, "introduce", "소개글이 없습니다");
        firebaseView.setStringTextView(locationTV, "Groups", group, "location", "위치 정보가 없습니다");

        if(is_manager&&group.equals(userGroup)) {
            functionBtn.setVisibility(View.VISIBLE);
            firebaseView.setButtonText(group, functionBtn);
            functionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditIntroduce();
                }
            });
        }
    }

    private void EditIntroduce() {
        String introduce=introduceTV.getText().toString();
        String location=locationTV.getText().toString();
        Intent intent=new Intent(IntroduceActivity.this, EditIntroduceActivity.class);
        if(functionBtn.getText().toString().equals("수정하기")) {
            intent.putExtra("introduce", introduce);
            intent.putExtra("location", location);
        }
        intent.putExtra("groupName", group);
        startActivity(intent);
    }

    public void close(View v) {
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }
}
