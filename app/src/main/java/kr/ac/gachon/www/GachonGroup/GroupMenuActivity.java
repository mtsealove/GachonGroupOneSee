package kr.ac.gachon.www.GachonGroup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class GroupMenuActivity extends AppCompatActivity {
    TextView groupNameTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_menu);

        groupNameTV=(TextView)findViewById(R.id.Group_name);
        //동아리 이름을 받아와 설정
        Intent intent=getIntent();
        String groupName=intent.getStringExtra("groupName");
        groupNameTV.setText(groupName);

    }

    public void back(View v) {
        onBackPressed();
    }
}
