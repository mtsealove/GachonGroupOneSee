package kr.ac.gachon.www.GachonGroup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GroupMenuActivity extends AppCompatActivity {
    TextView groupNameTV;
    Button groupScheduleBtn, groupIntroduceBtn;
    String groupName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_menu);

        groupNameTV= findViewById(R.id.federationNoticeTV);
        groupScheduleBtn= findViewById(R.id.groupScheduleBtn);
        groupIntroduceBtn= findViewById(R.id.introduceBtn);

        //동아리 이름을 받아와 설정
        Intent intent=getIntent();
        groupName=intent.getStringExtra("groupName");
        groupNameTV.setText(groupName);
        groupScheduleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupSchedule();
            }
        });
        groupIntroduceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Introduce();
            }
        });
    }

    private void GroupSchedule() {
        Intent intent=new Intent(GroupMenuActivity.this, GroupScheduleActivity.class);
        intent.putExtra("groupName", groupName);
        startActivity(intent);
    }

    private void Introduce() {
        Intent intent=new Intent(GroupMenuActivity.this, IntroduceActivity.class);
        intent.putExtra("group", groupName);
        startActivity(intent);
    }

    public void back(View v) {
        onBackPressed();
    }
}
