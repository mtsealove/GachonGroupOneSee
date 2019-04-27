package kr.ac.gachon.www.GachonGroup.Board;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import kr.ac.gachon.www.GachonGroup.Account.MyInformationActivity;
import kr.ac.gachon.www.GachonGroup.Entity.Account;
import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseAccount;
import kr.ac.gachon.www.GachonGroup.Group.GroupListActivity;
import kr.ac.gachon.www.GachonGroup.R;
import kr.ac.gachon.www.GachonGroup.etc.BackPressCloseHandler;

public class HomeActivity extends AppCompatActivity { //로그인 후 나타나는 홈 액티비티
    //동아리 리스트 버튼
    Button GroupBtn[]=new Button[8];
    int buttonID[]=new int[8];
    private Account account;
    private String ID;
    Button myInfo, PRbaordBtn, FederationNoticeBtn, QnABtn;
    int curent_page=0;

    public static Activity _Home_Activity;

    private BackPressCloseHandler backPressCloseHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        _Home_Activity=HomeActivity.this;
        Intent intent=getIntent();
        ID=intent.getStringExtra("ID");
        FirebaseAccount firebaseAccount=new FirebaseAccount(HomeActivity.this);
        account=new Account();
        firebaseAccount.GetAccount(ID, account);

        backPressCloseHandler=new BackPressCloseHandler(this);
        //동아리 분과 버튼들
        buttonID[0]=R.id.academicBtn;
        buttonID[1]=R.id.musicBtn;
        buttonID[2]=R.id.religionBtn;
        buttonID[3]=R.id.athleticBtn;
        buttonID[4]=R.id.performanceBtn;
        buttonID[5]=R.id.social_scienceBtn;
        buttonID[6]=R.id.exhibitionBtn;
        buttonID[7]=R.id.volunteerBtn;
        myInfo= findViewById(R.id.myInfoBtn);
        PRbaordBtn= findViewById(R.id.PRboardBtn);
        FederationNoticeBtn= findViewById(R.id.federation_noticeBtn);
        QnABtn= findViewById(R.id.QnABtn);

        //버튼 매칭
        for(int i=0; i<8; i++)
            GroupBtn[i] = findViewById(buttonID[i]);
        //각 버튼 리스너 지정
        for(int i=0; i<8; i++) {
            final int finalI = i;
            GroupBtn[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //각 포지션에 맞는 인텐트로 데이터 전송
                    Intent groupList=new Intent(HomeActivity.this, GroupListActivity.class);
                    String category=null;
                    String categoryKR=null;
                    switch (finalI) {
                        case 0: category="academic";
                        categoryKR="학술";
                        break;
                        case 1: category="music";
                        categoryKR="음악";
                        break;
                        case 2: category="religion";
                        categoryKR="종교";
                         break;
                        case 3: category="athletic";
                        categoryKR="체육";
                        break;
                        case 4: category="performance";
                        categoryKR="공연";
                        break;
                        case 5: category="social_science";
                        categoryKR="사회과학";
                        break;
                        case 6: category="exhibition";
                        categoryKR="전시창작";
                        break;
                        case 7: category="volunteer";
                        categoryKR="취미봉사";
                        break;
                    }
                    groupList.putExtra("category", category); //동아리 분과 전송
                    groupList.putExtra("categorykr", categoryKR);
                    groupList.putExtra("ID", ID); //사용자 ID 전송
                    startActivity(groupList);
                }
            });
        }

        myInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyInformation();
            }
        });
        PRbaordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PRBoard();
            }
        });
        FederationNoticeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FederationNotice();
            }
        });
        QnABtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QnA();
            }
        });
    }
    private void MyInformation() { //내 정보 바로가기
            Intent intent = new Intent(HomeActivity.this, MyInformationActivity.class);
            intent.putExtra("group", account.group);
            intent.putExtra("ID", account.ID);
            startActivity(intent);
    }
    private void PRBoard() { //홍보게시판 바로가기
        Intent intent=new Intent(HomeActivity.this, PRBoardActivity.class);
        intent.putExtra("userID", ID);
        intent.putExtra("is_manager", account.is_manager);
        intent.putExtra("group", account.group);
        startActivity(intent);
    }
    private void FederationNotice() { //연합회 공지사항 바로가기
        Intent intent=new Intent(HomeActivity.this, FederationNoticeActivity.class);
        intent.putExtra("userID", ID);
        intent.putExtra("group", account.group);
        startActivity(intent);
    }
    private void QnA() { //QnA바로가기
        Intent intent=new Intent(HomeActivity.this, QnAActivity.class);
        intent.putExtra("userID", ID);
        startActivity(intent);
    }
    //뒤로가기 2번 눌러서 종료
    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }
}
