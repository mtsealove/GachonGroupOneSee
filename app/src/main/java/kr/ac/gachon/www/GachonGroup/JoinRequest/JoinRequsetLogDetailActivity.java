package kr.ac.gachon.www.GachonGroup.JoinRequest;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseJoinRequest;
import kr.ac.gachon.www.GachonGroup.R;
import kr.ac.gachon.www.GachonGroup.etc.Alert;

public class JoinRequsetLogDetailActivity extends AppCompatActivity {   //동아리 신청 내역 조회(상세)
    private String ID;
    private String SelfIntroduce;
    private int StudentNumber;
    private int age;
    private String contact;
    private String group;
    private String major;
    private String name;
    private String AbleTime;
    private TextView SelfIntroduceTV, StudentNuberTV, ageTV, contactTV, groupTV, majorTV, nameTV, AbleTimeTV;
    private Button RemoveJoinRequestBtn, updateJoinRequestBtn;
    public static Activity _JoinRequestLogDetailActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_requset_log_detail);
        _JoinRequestLogDetailActivity=JoinRequsetLogDetailActivity.this;
        //표시할 내용 받아오기
        Intent intent=getIntent();
        ID=intent.getStringExtra("ID");
        SelfIntroduce=intent.getStringExtra("SelfIntroduce");
        StudentNumber=intent.getIntExtra("StudentNumber", 0);
        String StudentNumberStr=Integer.toString(StudentNumber).substring(2,4);
        age=intent.getIntExtra("age", 0);
        contact=intent.getStringExtra("contact");
        group=intent.getStringExtra("group");
        major=intent.getStringExtra("major");
        name=intent.getStringExtra("name");
        AbleTime=intent.getStringExtra("AbleTime");
        boolean viewOnly=intent.getBooleanExtra("viewOnly", false); //관리자일 경우 볼 수만 있게

        //모든 뷰 매칭
        SelfIntroduceTV=findViewById(R.id.selfIntroduceTV);
        StudentNuberTV=findViewById(R.id.studentNumberTV);
        ageTV=findViewById(R.id.ageTV);
        contactTV=findViewById(R.id.contactTV);
        groupTV=findViewById(R.id.groupNameTV);
        majorTV=findViewById(R.id.majorTV);
        nameTV=findViewById(R.id.nameTV);
        AbleTimeTV=findViewById(R.id.ableTimeTV);
        RemoveJoinRequestBtn=findViewById(R.id.removeBtn);
        updateJoinRequestBtn=findViewById(R.id.updateBtn);

        //화면에 표시
        SelfIntroduceTV.setText(SelfIntroduce);
        StudentNuberTV.setText(StudentNumberStr);
        ageTV.setText(Integer.toString(age));
        contactTV.setText(contact);
        groupTV.setText(group);
        majorTV.setText(major);
        nameTV.setText(name);
        AbleTimeTV.setText(AbleTime);

        if(viewOnly) {  //관리자면 삭제/수정 불가
            RemoveJoinRequestBtn.setVisibility(View.GONE);
            updateJoinRequestBtn.setVisibility(View.GONE);
        }

        //삭제 및 수정 버튼 매칭
        RemoveJoinRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRemoveJoinRequestBtn();
            }
        });
        updateJoinRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpdateJoinRequestBtn();
            }
        });
    }
    //가입 신청 취소
    private void setRemoveJoinRequestBtn() {
        Alert alert=new Alert(JoinRequsetLogDetailActivity.this);
        alert.MsgDialogChoice("가입신청을 취소하시겠습니까?", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseJoinRequest firebaseJoinRequest=new FirebaseJoinRequest(JoinRequsetLogDetailActivity.this);
                firebaseJoinRequest.RemoveJoinRequest(ID, group);   //DB에서 삭제
            }
        });
    }

    private void setUpdateJoinRequestBtn() {    //가입 신청 수정
        Alert alert=new Alert(JoinRequsetLogDetailActivity.this);
        alert.MsgDialogChoice("신청 내역을 수정하시겠습니까?", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(JoinRequsetLogDetailActivity.this, JoinRequestActivity.class);
                //화면에 표시될 내용과 ID전송
                intent.putExtra("name", name);
                intent.putExtra("contact",contact);
                intent.putExtra("StudentNumber", StudentNumber);
                intent.putExtra("age", age);
                intent.putExtra("major", major);
                intent.putExtra("SelfIntroduce", SelfIntroduce);
                intent.putExtra("AbleTime", AbleTime);
                intent.putExtra("ID", ID);
                intent.putExtra("groupName", group);
                intent.putExtra("update", true);
                startActivity(intent);  //수정하러 가기
                Alert.dialog.cancel();
            }
        });
    }
    public void close(View v) {
        finish();
    }
}
