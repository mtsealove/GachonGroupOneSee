package kr.ac.gachon.www.GachonGroup.Board;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import kr.ac.gachon.www.GachonGroup.Entity.Account;
import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseAccount;
import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseList;
import kr.ac.gachon.www.GachonGroup.R;

public class FederationNoticeActivity extends AppCompatActivity {
    ListView boardLV;
    Button searchBtn, postBtn;
    private final String BoardName="FederationNotice";
    private String ID, value, group;
    public static Activity _FederationNoticeActivity;
    FirebaseList firebaseList;
    FirebaseAccount firebaseAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_federation_notice);
        _FederationNoticeActivity=FederationNoticeActivity.this;
        Intent intent=getIntent();
        ID=intent.getStringExtra("userID");
        value=intent.getStringExtra("value");
        group=intent.getStringExtra("group");
        boardLV= findViewById(R.id.boardLV);
        searchBtn=findViewById(R.id.searchBtn);
        postBtn=findViewById(R.id.postBtn);

        firebaseList=new FirebaseList(FederationNoticeActivity.this);

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
        intent.putExtra("userGroup", group);
        startActivity(intent);
    }

    private void init() {
        //검색어가 없으면 모든 리스트 표시
        if(value==null)
            firebaseList.setListView(ID, boardLV, BoardName);
            //존재하면 검색어를 포함하는 리스트 표시
        else firebaseList.setListView(ID, boardLV, BoardName, value);

        //연합회일 경우 모든 작성 버튼 활성화
        if(group.equals("동아리 연합회")) {
            postBtn.setVisibility(View.VISIBLE);
            postBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Post();
                }
            });
        }

    }

    private void Post() {
        Intent intent=new Intent(FederationNoticeActivity.this, AddPostActivity.class);
        intent.putExtra("boardName", BoardName);
        intent.putExtra("userID", ID);

        startActivity(intent);
    }
    public void close(View v){
        finish();
    }

    @Override
    public void onResume(){
        super.onResume();
        init();
    }

    @Override
    public void onBackPressed() {
        if(value!=null) {
            Intent intent=new Intent(FederationNoticeActivity.this, FederationNoticeActivity.class);
            intent.putExtra("userID", ID);
            intent.putExtra("group", group);
            startActivity(intent);
            finish();
        } else super.onBackPressed();
    }
}
