package kr.ac.gachon.www.GachonGroup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import kr.ac.gachon.www.GachonGroup.modules.BackPressCloseHandler;
import kr.ac.gachon.www.GachonGroup.modules.FirebaseHelper;

public class HomeActivity extends AppCompatActivity {
    //동아리 리스트 버튼
    Button GroupBtn[]=new Button[8];
    int buttonID[]=new int[8];
    Account account;
    Button myInfo;

    private BackPressCloseHandler backPressCloseHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent intent=getIntent();
        String ID=intent.getStringExtra("ID");
        FirebaseHelper helper=new FirebaseHelper();
        account=new Account();
        helper.GetAccount(ID, account);

        backPressCloseHandler=new BackPressCloseHandler(this);

        buttonID[0]=R.id.academicBtn;
        buttonID[1]=R.id.musicBtn;
        buttonID[2]=R.id.religionBtn;
        buttonID[3]=R.id.athleticBtn;
        buttonID[4]=R.id.performanceBtn;
        buttonID[5]=R.id.social_scienceBtn;
        buttonID[6]=R.id.exhibitionBtn;
        buttonID[7]=R.id.volunteerBtn;
        myInfo=(Button)findViewById(R.id.myInfoBtn);

        //버튼 매칭
        for(int i=0; i<8; i++)
            GroupBtn[i] = (Button) findViewById(buttonID[i]);
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
                    groupList.putExtra("category", category);
                    groupList.putExtra("categorykr", categoryKR);
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
    }
    private void MyInformation() {
        Intent intent=new Intent(HomeActivity.this, MyInformationActivity.class);
        intent.putExtra("ID", account.ID);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }
}
