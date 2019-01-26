package kr.ac.gachon.www.GachonGroup.Board;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseList;
import kr.ac.gachon.www.GachonGroup.R;

public class FederationNoticeActivity extends AppCompatActivity {
    ListView boardLV;
    Button searchBtn;
    private final String BoardName="FederationNotice";
    private String ID, value;
    public static Activity _FederationNoticeActivity;
    FirebaseList firebaseList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_federation_notice);
        _FederationNoticeActivity=FederationNoticeActivity.this;
        Intent intent=getIntent();
        ID=intent.getStringExtra("userID");
        value=intent.getStringExtra("value");
        boardLV= findViewById(R.id.boardLV);
        searchBtn=findViewById(R.id.searchBtn);

        firebaseList=new FirebaseList(FederationNoticeActivity.this);
        //검색어가 존재하지 않으면 모든 리스트

        init();
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Search();
            }
        });
    }
    private void Search() {
        Intent intent=new Intent(FederationNoticeActivity.this, SearchActivity.class);
        intent.putExtra("BoardName", BoardName);
        startActivity(intent);
    }

    private void init() {
        if(value==null)
            firebaseList.setListView(ID, boardLV, BoardName);
            //존재하면 검색어를 포함하는 리스트
        else firebaseList.setListView(ID, boardLV, BoardName, value);
    }

    public void close(View v){
        finish();
    }

    @Override
    public void onResume(){
        super.onResume();
        init();
    }
}
