package kr.ac.gachon.www.GachonGroup.JoinRequest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import kr.ac.gachon.www.GachonGroup.Entity.Account;
import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseAccount;
import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseJoinRequest;
import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseView;
import kr.ac.gachon.www.GachonGroup.Entity.JoinRequest;
import kr.ac.gachon.www.GachonGroup.R;

public class JoinRequestActivity extends AppCompatActivity {    //동아리 가입신청 액티비티
    private String ID, groupName, name, contact, major, ableTime, SelfIntroduce;
    private int age, StudentNumber;
    private EditText nameET, contactET, student_numberET, majorET, ageET, able_timeET, self_introduceET;
    private TextView groupNameTV;
    private Button requestBtn;
    private FirebaseView firebaseView;
    private Account account;
    private FirebaseJoinRequest firebaseJoinRequest;
    private boolean update;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_request);
        //데이터 받아오기
        Intent intent=getIntent();
        ID=intent.getStringExtra("ID");
        groupName=intent.getStringExtra("groupName");
        name=intent.getStringExtra("name");
        contact=intent.getStringExtra("contact");
        StudentNumber=intent.getIntExtra("StudentNumber", 0);
        age=intent.getIntExtra("age", 0);
        major=intent.getStringExtra("major");
        ableTime=intent.getStringExtra("AbleTime");
        SelfIntroduce=intent.getStringExtra("SelfIntroduce");


        firebaseView=new FirebaseView(JoinRequestActivity.this);
        account=new Account();
        FirebaseAccount firebaseAccount=new FirebaseAccount(JoinRequestActivity.this);
        firebaseAccount.GetAccount(ID, account);

        //텍스트 설정
        nameET=findViewById(R.id.nameET);
        contactET=findViewById(R.id.contactET);
        contactET.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        student_numberET=findViewById(R.id.studentNumberET);
        majorET=findViewById(R.id.majorET);
        ageET=findViewById(R.id.ageET);
        able_timeET=findViewById(R.id.ableTimeET);
        self_introduceET=findViewById(R.id.selfIntoduceET);
        groupNameTV=findViewById(R.id.groupNameTV);
        groupNameTV.setText(groupName);
        requestBtn=findViewById(R.id.requestBtn);

        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JoinRequest();
            }
        });

        firebaseJoinRequest=new FirebaseJoinRequest(JoinRequestActivity.this);

        update=intent.getBooleanExtra("update", false);
        //이미 신청했으면 종료
        if(!update) {
        firebaseJoinRequest.CheckAlreadyRequested(ID, groupName);
            firebaseView.setEditText("name", ID, nameET);
            firebaseView.setEditText("StudentNumber", ID, student_numberET);
            firebaseView.setEditText("major", ID, majorET);
        } else {
            nameET.setText(name);
            contactET.setText(contact);
            student_numberET.setText(Integer.toString(StudentNumber));
            ageET.setText(Integer.toString(age));
            majorET.setText(major);
            able_timeET.setText(ableTime);
            self_introduceET.setText(SelfIntroduce);
        }
    }

    //입력란을 확인하고 요청을 발송함
    private void JoinRequest() {
        String name=nameET.getText().toString();
        String contact=contactET.getText().toString();
        int student_number=0;
        try {
            student_number=Integer.parseInt(student_numberET.getText().toString());
        } catch (Exception e) {
        }
        String major=majorET.getText().toString();
        int age=0;
        try {
            age=Integer.parseInt(ageET.getText().toString());
        } catch (Exception e) {
        }
        String able_time=able_timeET.getText().toString();
        String self_introduce=self_introduceET.getText().toString();

        //모두 입력했는지 확인
        if(name.length()==0) Toast.makeText(JoinRequestActivity.this, "이름을 입력해 주세요", Toast.LENGTH_SHORT).show();
        else if(contact.length()==0) Toast.makeText(JoinRequestActivity.this, "연락처를 입력해 주세요", Toast.LENGTH_SHORT).show();
        else if(student_number==0) Toast.makeText(JoinRequestActivity.this, "학번을 입력해 주세요", Toast.LENGTH_SHORT).show();
        else if(major.length()==0) Toast.makeText(JoinRequestActivity.this, "학과를 입력해 주세요", Toast.LENGTH_SHORT).show();
        else if(age==0) Toast.makeText(JoinRequestActivity.this, "나이를 입력해 주세요", Toast.LENGTH_SHORT).show();
        else if(able_time.length()==0) Toast.makeText(JoinRequestActivity.this, "가능한 시간을 입력해 주세요", Toast.LENGTH_SHORT).show();
        else if(self_introduce.length()==0) Toast.makeText(JoinRequestActivity.this, "자기소개를 입력해 주세요", Toast.LENGTH_SHORT).show();
        else {  //그렇다면 새 가입신청 객체 생성
            JoinRequest joinRequest=new JoinRequest(name, contact, student_number, major, age, self_introduce, ID, groupName, able_time);
            if(!update) //수정이 아니면
                firebaseJoinRequest.MakeJoinRequest(joinRequest);   //DB에 추가
            else firebaseJoinRequest.UpdateJoinRequest(ID, groupName, joinRequest); //DB정보 수정

        }
    }

    public void close(View v) {
        finish();
    }
}
