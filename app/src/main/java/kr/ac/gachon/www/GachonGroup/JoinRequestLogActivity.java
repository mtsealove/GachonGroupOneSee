package kr.ac.gachon.www.GachonGroup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseList;
import kr.ac.gachon.www.GachonGroup.modules.JoinRequest;

public class JoinRequestLogActivity extends AppCompatActivity {
    private ListView boardLV;
    private String ID;
    FirebaseList firebaseList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_request_log);

        Intent intent=getIntent();
        ID=intent.getStringExtra("userID");
        boardLV=findViewById(R.id.boardLV);

        //리스트에 신청 내역을 불러오기
        firebaseList=new FirebaseList(JoinRequestLogActivity.this);
        firebaseList.JoinRequestLog(boardLV, ID);
    }
    @Override
    public void onResume() {
        super.onResume();
        firebaseList.JoinRequestLog(boardLV, ID);
    }
}
