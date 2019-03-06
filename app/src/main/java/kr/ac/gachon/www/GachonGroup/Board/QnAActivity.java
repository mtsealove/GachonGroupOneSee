package kr.ac.gachon.www.GachonGroup.Board;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseList;
import kr.ac.gachon.www.GachonGroup.R;

public class QnAActivity extends AppCompatActivity { //Q&A 액티비티
    ListView boardLV;
    Button searchBtn, postBtn;
    private final String BoardName="QnA";
    public static Activity _QnAActivity;
    private String userID, value;
    FirebaseList firebaseList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qna);
        _QnAActivity=QnAActivity.this;
        Intent intent=getIntent();
        value=intent.getStringExtra("value");
        userID=intent.getStringExtra("userID");
        boardLV= findViewById(R.id.boardLV);
        searchBtn=findViewById(R.id.searchBtn);
        postBtn=findViewById(R.id.postBtn);

        firebaseList=new FirebaseList(QnAActivity.this);
        //검색어가 존재하지 않으면 모든 리스트
        if(value==null)
            firebaseList.setListView(userID, boardLV, BoardName);
        //존재하면 검색어를 포함하는 리스트
        else firebaseList.setListView(userID, boardLV, BoardName,  value);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Search();
            }
        });
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Post();
            }
        });
    }

    private void Search() { //검색
        Intent intent=new Intent(QnAActivity.this, SearchActivity.class);
        intent.putExtra("BoardName", BoardName);
        startActivity(intent);
    }
    private void Post() { //게시글 작성
        Intent intent=new Intent(QnAActivity.this, AddPostActivity.class);
        intent.putExtra("boardName", BoardName);
        intent.putExtra("userID", userID);
        startActivity(intent);
    }

    @Override
    public void onResume() { //글 작성 후 복귀 시 업데이트
        super.onResume();
        if(value==null)
            firebaseList.setListView(userID, boardLV, BoardName);
            //존재하면 검색어를 포함하는 리스트
        else firebaseList.setListView(userID, boardLV, BoardName, value);
    }
    public void close(View v) {
        finish();
    }

    @Override
    public void onBackPressed() {
        if(value!=null) {
            Intent intent=new Intent(QnAActivity.this, QnAActivity.class);
            intent.putExtra("userID", userID);
            startActivity(intent);
            finish();
        } else super.onBackPressed();
    }
}
