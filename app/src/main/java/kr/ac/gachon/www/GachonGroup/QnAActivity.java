package kr.ac.gachon.www.GachonGroup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import kr.ac.gachon.www.GachonGroup.modules.FirebaseHelper;

public class QnAActivity extends AppCompatActivity {
    ListView boardLV;
    Button searchBtn, postBtn;
    private final String BoardName="QnA";
    public static Activity _QnAActivity;
    private String userID, value;
    FirebaseHelper helper;
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

        helper=new FirebaseHelper();
        //검색어가 존재하지 않으면 모든 리스트
        if(value==null)
            helper.setListView(userID, boardLV, BoardName, QnAActivity.this);
        //존재하면 검색어를 포함하는 리스트
        else helper.setListView(userID, boardLV, BoardName, QnAActivity.this, value);

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
    private void Search() {
        Intent intent=new Intent(QnAActivity.this, SearchActivity.class);
        intent.putExtra("BoardName", BoardName);
        startActivity(intent);
    }
    private void Post() {
        Intent intent=new Intent(QnAActivity.this, AddPostActivity.class);
        intent.putExtra("boardName", BoardName);
        intent.putExtra("userID", userID);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(value==null)
            helper.setListView(userID, boardLV, BoardName, QnAActivity.this);
            //존재하면 검색어를 포함하는 리스트
        else helper.setListView(userID, boardLV, BoardName, QnAActivity.this, value);
    }
    public void close(View v) {
        finish();
    }
}
