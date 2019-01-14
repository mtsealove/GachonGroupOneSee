package kr.ac.gachon.www.GachonGroup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import kr.ac.gachon.www.GachonGroup.modules.BackPressCloseHandler;

public class HomeActivity extends AppCompatActivity {
    //동아리 리스트 버튼
    Button GroupBtn[]=new Button[7];
    int buttonID[]=new int[7];

    private BackPressCloseHandler backPressCloseHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        backPressCloseHandler=new BackPressCloseHandler(this);

        buttonID[0]=R.id.academicBtn;
        buttonID[1]=R.id.musicBtn;
        buttonID[2]=R.id.religionBtn;
        buttonID[3]=R.id.athleticBtn;
        buttonID[4]=R.id.performanceBtn;
        buttonID[5]=R.id.social_scienceBtn;
        buttonID[6]=R.id.exhibitionBtn;

        //버튼 매칭
        for(int i=0; i<7; i++)
            GroupBtn[i] = (Button) findViewById(buttonID[i]);
        //각 버튼 리스너 지정
        for(int i=0; i<7; i++) {
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
                    }
                    groupList.putExtra("category", category);
                    groupList.putExtra("categorykr", categoryKR);
                    startActivity(groupList);
                }
            });
        }
    }
    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }
}
