package kr.ac.gachon.www.GachonGroup.Group;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseJoinRequest;
import kr.ac.gachon.www.GachonGroup.R;

public class GroupJoinRequestLogActivity extends AppCompatActivity {
    ListView boarLV;
    private String groupName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_join_request_log);
        boarLV=findViewById(R.id.boardLV);
        Intent intent=getIntent();
        groupName=intent.getStringExtra("groupName");

        FirebaseJoinRequest firebaseJoinRequest=new FirebaseJoinRequest(GroupJoinRequestLogActivity.this);
        firebaseJoinRequest.GroupJoinRequestLog(boarLV, groupName);
    }

    public void close(View v) {
        finish();
    }
}
