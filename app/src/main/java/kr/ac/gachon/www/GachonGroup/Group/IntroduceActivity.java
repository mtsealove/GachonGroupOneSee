package kr.ac.gachon.www.GachonGroup.Group;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseImage;
import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseView;
import kr.ac.gachon.www.GachonGroup.R;

public class IntroduceActivity extends AppCompatActivity {  //동아리 소개글 액티비티
    private TextView introduceTV, locationTV;
    private Button functionBtn;
    private String group, userGroup;
    private ImageView GroupIcon;
    private boolean is_manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce);
        locationTV= findViewById(R.id.locationTV);
        introduceTV= findViewById(R.id.introduceTV);
        functionBtn=findViewById(R.id.functionBtn);
        GroupIcon=findViewById(R.id.GroupIcon);

        //데이터 받아오기
        Intent intent=getIntent();
        group=intent.getStringExtra("group");   //동아리 이름
        is_manager=intent.getBooleanExtra("is_manager", false); //관리자 여부
        userGroup=intent.getStringExtra("userGroup");   //사용자 동아리
    }

    private void init() {   //초기화
        FirebaseView firebaseView=new FirebaseView(IntroduceActivity.this);
        firebaseView.setStringTextView(introduceTV, "Groups", group, "introduce", "소개글이 없습니다"); //동아리에 해당하는 소개글 표시
        firebaseView.setStringTextView(locationTV, "Groups", group, "location", "위치 정보가 없습니다"); //동아리에 해당하는 위치 표시

        if(is_manager&&group.equals(userGroup)) {   //관리자이며 사용자의 동아리인경우
            functionBtn.setVisibility(View.VISIBLE);    //수정/등록 활성화
            firebaseView.setButtonText(group, functionBtn);
            functionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditIntroduce();
                }
            });
        }

        FirebaseImage firebaseImage=new FirebaseImage(IntroduceActivity.this);
        String FilePath="Groups/"+group+"/"+group+"Icon.png";   //동아리 로고 경로
        firebaseImage.LoadImageView(FilePath, GroupIcon);   //동아리 로고 표시
    }

    private void EditIntroduce() {  //동아리 소개글 수정
        String introduce=introduceTV.getText().toString();
        String location=locationTV.getText().toString();
        Intent intent=new Intent(IntroduceActivity.this, EditIntroduceActivity.class);
        if(functionBtn.getText().toString().equals("수정하기")) {//수정할 수 있다면
            intent.putExtra("introduce", introduce);    //소개와
            intent.putExtra("location", location);  //위치 전송
        }
        intent.putExtra("groupName", group);
        startActivity(intent);
    }

    public void close(View v) {
        finish();
    }

    @Override
    public void onResume() {
        //작성 후 돌아왔을 때 바뀐 정보 표시
        super.onResume();
        init();
    }
}
