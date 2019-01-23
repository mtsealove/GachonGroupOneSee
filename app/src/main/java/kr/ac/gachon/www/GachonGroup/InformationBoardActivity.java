package kr.ac.gachon.www.GachonGroup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import kr.ac.gachon.www.GachonGroup.modules.FirebaseHelper;

public class InformationBoardActivity extends AppCompatActivity {
    ListView boardLV;
    Button searchBtn, postBtn;
    TextView titleTV, groupNameTV;
    private final String BoardName="Information";
    private String userID, value, groupName;
    public static Activity _InformationActivity;
    FirebaseHelper helper;
    private Account account;

    @Override
    protected void onCreate(Bundle si) {
        super.onCreate(si);
        setContentView(R.layout.activity_qna);

        account=new Account();
        helper=new FirebaseHelper();
        Intent intent=getIntent();
        groupName=intent.getStringExtra("groupName");
        value=intent.getStringExtra("value");
        userID=intent.getStringExtra("ID");
        helper.GetAccount(userID, account);
        boardLV= findViewById(R.id.boardLV);
        searchBtn=findViewById(R.id.searchBtn);
        postBtn=findViewById(R.id.postBtn);
        titleTV=findViewById(R.id.titleTV);
        titleTV.setText("정보게시판");
        groupNameTV=findViewById(R.id.groupNameTV);
        groupNameTV.setText(groupName);
        groupNameTV.setVisibility(View.VISIBLE);
        _InformationActivity=InformationBoardActivity.this;

        //검색어가 존재하지 않으면 모든 리스트
        if(value==null)
            helper.setListView(groupName, userID, boardLV, BoardName, InformationBoardActivity.this);
            //존재하면 검색어를 포함하는 리스트
        else helper.setListView(groupName, userID, boardLV, BoardName, InformationBoardActivity.this, value);

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

    //검색 메서드
    private void Search() {
        Intent intent=new Intent(InformationBoardActivity.this, SearchActivity.class);
        intent.putExtra("BoardName", BoardName);
        intent.putExtra("groupName", groupName);
        startActivity(intent);
    }

    //글 작성 메서드
    private void Post() {
        if(account.group.equals(groupName)) {
            Intent intent = new Intent(InformationBoardActivity.this, AddPostActivity.class);
            intent.putExtra("boardName", BoardName);
            intent.putExtra("userID", userID);
            intent.putExtra("groupName", groupName);
            startActivity(intent);
        } else Toast.makeText(InformationBoardActivity.this, "작성 권한이 없습니다", Toast.LENGTH_SHORT).show();
    }
    public void close(View v) {
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(value==null)
            helper.setListView(groupName, userID, boardLV, BoardName, InformationBoardActivity.this);
        else helper.setListView(groupName, userID, boardLV, BoardName, InformationBoardActivity.this, value);
    }
}
