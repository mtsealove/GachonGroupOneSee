package kr.ac.gachon.www.GachonGroup.Board;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseList;
import kr.ac.gachon.www.GachonGroup.R;

public class PublicNoticeActivity extends AppCompatActivity {
    ListView boardLV;
    Button searchBtn, postBtn;
    TextView titleTV;
    private final String BoardName="PublicNotice";
    private String ID, value, group;
    public static Activity _PublicNoticeActivity;
    FirebaseList firebaseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Intent intent=getIntent();
        ID=intent.getStringExtra("userID"); //사용자 ID
        value=intent.getStringExtra("value"); //검색 값
        group=intent.getStringExtra("group");
        _PublicNoticeActivity= PublicNoticeActivity.this;

        boardLV= findViewById(R.id.boardLV);
        searchBtn=findViewById(R.id.searchBtn);
        postBtn=findViewById(R.id.postBtn);
        titleTV=findViewById(R.id.titleTV);
        titleTV.setText("공지사항");

        firebaseList=new FirebaseList(PublicNoticeActivity.this);

        init(); //초기화
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Search();
            }
        });
    }
    private void Search() { //검색버튼
        Intent intent=new Intent(PublicNoticeActivity.this, SearchActivity.class);
        intent.putExtra("BoardName", BoardName); //게시판 이름 전송
        intent.putExtra("userGroup", group); //사용자 동아리 전송
        intent.putExtra("userID", ID);
        startActivity(intent);
    }
    private void init() { //시작
        //검색어가 없으면 모든 리스트 표시
        if(value==null)
            firebaseList.setListView(ID, boardLV, BoardName);
            //존재하면 검색어를 포함하는 리스트 표시
        else firebaseList.setListView(ID, boardLV, BoardName, value);

        //관리자일 경우 모든 작성 버튼 활성화
        if(group.contains("관리자")) {
            postBtn.setVisibility(View.VISIBLE);
            postBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Post();
                }
            });
        } else {    //관리자가 아니면 등록 버튼 소멸
            postBtn.setVisibility(View.GONE);
        }
    }

    private void Post() { //게시글 작성
        Intent intent=new Intent(PublicNoticeActivity.this, PostActivity.class);
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
            Intent intent=new Intent(PublicNoticeActivity.this,PublicNoticeActivity.class);
            intent.putExtra("userID", ID);
            intent.putExtra("group", group);
            startActivity(intent);
            finish();
        } else super.onBackPressed();
    }
}
