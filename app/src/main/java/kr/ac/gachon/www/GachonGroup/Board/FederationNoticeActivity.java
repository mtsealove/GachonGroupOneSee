package kr.ac.gachon.www.GachonGroup.Board;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseAccount;
import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseList;
import kr.ac.gachon.www.GachonGroup.R;

public class FederationNoticeActivity extends AppCompatActivity { //연합회 공시사항 액티비티
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
        //데이터 수신
        Intent intent=getIntent();
        ID=intent.getStringExtra("userID");
        value=intent.getStringExtra("value"); //검색 값
        group=intent.getStringExtra("group"); //사용자 동아리(연합회인지 확인)
        boardLV= findViewById(R.id.boardLV);
        searchBtn=findViewById(R.id.searchBtn);
        postBtn=findViewById(R.id.postBtn);

        firebaseList=new FirebaseList(FederationNoticeActivity.this);

        init(); //초기화
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Search();
            }
        });
    }
    private void Search() { //검색버튼
        Intent intent=new Intent(FederationNoticeActivity.this, SearchActivity.class);
        intent.putExtra("BoardName", BoardName); //게시판 이름 전송
        intent.putExtra("userGroup", group); //사용자 동아리 전송
        startActivity(intent);
    }

    private void init() { //시작
        //검색어가 없으면 모든 리스트 표시
        if(value==null)
            firebaseList.setListView(ID, boardLV, BoardName);
            //존재하면 검색어를 포함하는 리스트 표시
        else firebaseList.setListView(ID, boardLV, BoardName, value);

        //연합회일 경우 모든 작성 버튼 활성화
        if(group.contains("연합회")||group.contains("관리자")) {
            postBtn.setVisibility(View.VISIBLE);
            postBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Post();
                }
            });
        }

    }

    private void Post() { //게시글 작성
        Intent intent=new Intent(FederationNoticeActivity.this, PostActivity.class);
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
    public void onBackPressed() { //검색을 했던 경우 검색 없는 액티비티 생성
        if(value!=null) {
            Intent intent=new Intent(FederationNoticeActivity.this, FederationNoticeActivity.class);
            intent.putExtra("userID", ID);
            intent.putExtra("group", group);
            startActivity(intent);
            finish();
        } else super.onBackPressed();
    }
}
