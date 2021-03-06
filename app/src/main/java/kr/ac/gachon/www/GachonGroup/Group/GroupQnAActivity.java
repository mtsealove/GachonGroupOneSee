package kr.ac.gachon.www.GachonGroup.Group;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import kr.ac.gachon.www.GachonGroup.Board.PostActivity;
import kr.ac.gachon.www.GachonGroup.Entity.Account;
import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseAccount;
import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseList;
import kr.ac.gachon.www.GachonGroup.R;
import kr.ac.gachon.www.GachonGroup.Board.SearchActivity;

public class GroupQnAActivity extends AppCompatActivity {   //동아리 Q&A 액티비티
    ListView boardLV;
    Button searchBtn, postBtn;
    TextView titleTV, groupNameTV;
    private final String BoardName="GroupQnA";
    private String userID, value, groupName;
    public static Activity _GroupQnAActivity;
    FirebaseAccount firebaseAccount;
    FirebaseList firebaseList;
    private Account account;

    @Override
    protected void onCreate(Bundle si) {
        super.onCreate(si);
        setContentView(R.layout.activity_list);
        _GroupQnAActivity= GroupQnAActivity.this;
        account=new Account();
        firebaseAccount=new FirebaseAccount(GroupQnAActivity.this);
        firebaseList=new FirebaseList(GroupQnAActivity.this);
        Intent intent=getIntent();
        groupName=intent.getStringExtra("groupName");
        value=intent.getStringExtra("value");
        userID=intent.getStringExtra("ID");
        firebaseAccount.GetAccount(userID, account);
        boardLV= findViewById(R.id.boardLV);
        searchBtn=findViewById(R.id.searchBtn);
        postBtn=findViewById(R.id.postBtn);
        titleTV=findViewById(R.id.titleTV);
        titleTV.setText("Q&A");
        groupNameTV=findViewById(R.id.groupNameTV);
        groupNameTV.setText(groupName);
        groupNameTV.setVisibility(View.VISIBLE);


        //검색어가 존재하지 않으면 모든 리스트
        if(value==null)
            firebaseList.setGroupListView(groupName, userID, boardLV, BoardName);
            //존재하면 검색어를 포함하는 리스트
        else
        firebaseList.setGroupListView(groupName, userID, boardLV, BoardName, value);

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

    //검색
    private void Search() {
        Intent intent=new Intent(GroupQnAActivity.this, SearchActivity.class);
        intent.putExtra("BoardName", BoardName);    //게시판 이름
        intent.putExtra("groupName", groupName);    //동아리 이름
        intent.putExtra("userID", userID);  //사용자 아이디 전송 및
        startActivity(intent);  //검색 액티비티로 이동
    }

    //글 작성
    private void Post() {
            Intent intent = new Intent(GroupQnAActivity.this, PostActivity.class);
            intent.putExtra("boardName", BoardName);
            intent.putExtra("userID", userID);
            intent.putExtra("groupName", groupName);
            startActivity(intent);  //작성 액티비티
    }
    public void close(View v) {
        finish();
    }

    @Override
    public void onResume() {    //게시글 작성 후 돌아오면 그 리스트 다시 표시
        super.onResume();
        if(value==null)
            firebaseList.setGroupListView(groupName, userID, boardLV, BoardName);
        else firebaseList.setGroupListView(groupName, userID, boardLV, BoardName, value);
    }

    @Override
    public void onBackPressed() {
        if(value!=null) { //만약 검색을 실시했을 경우 검색 없는 액티비티로 이동
            Intent intent=new Intent(GroupQnAActivity.this, GroupQnAActivity.class);
            intent.putExtra("groupName", groupName);
            intent.putExtra("ID", userID);
            startActivity(intent);
            finish();
        } else super.onBackPressed();  //아니면 그냥 뒤로가기
    }
}
