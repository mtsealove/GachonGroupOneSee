package kr.ac.gachon.www.GachonGroup.Group;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseJoinRequest;
import kr.ac.gachon.www.GachonGroup.R;

public class GroupJoinRequestLogActivity extends AppCompatActivity {    //특정 동아리에 가입신청된 기록을 조회하는 액티비티, 관리자만 접근 가능
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
        firebaseJoinRequest.GroupJoinRequestLog(boarLV, groupName); //동아리 이름으로 리스트 설정
    }

    public void close(View v) {
        finish();
    }
}
