package kr.ac.gachon.www.GachonGroup.JoinRequest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseJoinRequest;
import kr.ac.gachon.www.GachonGroup.R;

public class JoinRequestLogActivity extends AppCompatActivity { //일반 사용자의 동아리 신청 내역 조회(리스트)
    private ListView boardLV;
    private String ID;
    private FirebaseJoinRequest firebaseJoinRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_request_log);

        Intent intent=getIntent();
        ID=intent.getStringExtra("userID");
        boardLV=findViewById(R.id.boardLV);

        //리스트에 신청 내역을 불러오기
        firebaseJoinRequest=new FirebaseJoinRequest(JoinRequestLogActivity.this);
        firebaseJoinRequest.JoinRequestLog(boardLV, ID);
    }
    @Override
    public void onResume() {
        super.onResume();
        firebaseJoinRequest.JoinRequestLog(boardLV, ID);
    }

    public void close(View v) {
        finish();
    }
}
