package kr.ac.gachon.www.GachonGroup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GroupMenuActivity extends AppCompatActivity {
    TextView groupNameTV;
    Button groupScheduleBtn;
    String groupName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_menu);

        groupNameTV=(TextView)findViewById(R.id.Group_name);
        groupScheduleBtn=(Button)findViewById(R.id.groupScheduleBtn);

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
    }

    private void GroupSchedule() {
        Intent intent=new Intent(GroupMenuActivity.this, GroupScheduleActivity.class);
        intent.putExtra("groupName", groupName);
        startActivity(intent);
    }

    public void back(View v) {
        onBackPressed();
    }
}
