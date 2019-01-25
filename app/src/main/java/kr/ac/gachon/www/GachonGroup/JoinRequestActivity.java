package kr.ac.gachon.www.GachonGroup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseAccount;
import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseList;
import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseView;
import kr.ac.gachon.www.GachonGroup.modules.FirebaseHelper;
import kr.ac.gachon.www.GachonGroup.modules.JoinRequest;

public class JoinRequestActivity extends AppCompatActivity {
    private String ID, groupName;
    EditText nameET, contactET, student_numberET, majorET, ageET, able_timeET, self_introduceET;
    TextView groupNameTV;
    Button requestBtn;
    FirebaseView firebaseView;
    private Account account;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_request);
        Intent intent=getIntent();
        ID=intent.getStringExtra("ID");
        groupName=intent.getStringExtra("groupName");

        firebaseView=new FirebaseView(JoinRequestActivity.this);
        account=new Account();
        FirebaseAccount firebaseAccount=new FirebaseAccount(JoinRequestActivity.this);
        firebaseAccount.GetAccount(ID, account);
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

        firebaseView.setEditText("name", ID, nameET);
        firebaseView.setEditText("StudentNumber", ID, student_numberET);
        firebaseView.setEditText("major", ID, majorET);

        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JoinRequest();
            }
        });
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

        if(account.group.equals(groupName)) Toast.makeText(JoinRequestActivity.this, "이미 가입된 동아리입니다", Toast.LENGTH_SHORT).show();
        else if(name.length()==0) Toast.makeText(JoinRequestActivity.this, "이름을 입력해 주세요", Toast.LENGTH_SHORT).show();
        else if(contact.length()==0) Toast.makeText(JoinRequestActivity.this, "연락처를 입력해 주세요", Toast.LENGTH_SHORT).show();
        else if(student_number==0) Toast.makeText(JoinRequestActivity.this, "학번을 입력해 주세요", Toast.LENGTH_SHORT).show();
        else if(major.length()==0) Toast.makeText(JoinRequestActivity.this, "학과를 입력해 주세요", Toast.LENGTH_SHORT).show();
        else if(age==0) Toast.makeText(JoinRequestActivity.this, "나이를 입력해 주세요", Toast.LENGTH_SHORT).show();
        else if(able_time.length()==0) Toast.makeText(JoinRequestActivity.this, "가능한 시간을 입력해 주세요", Toast.LENGTH_SHORT).show();
        else if(self_introduce.length()==0) Toast.makeText(JoinRequestActivity.this, "자기소개를 입력해 주세요", Toast.LENGTH_SHORT).show();
        else {
            JoinRequest joinRequest=new JoinRequest(name, contact, student_number, major, age, self_introduce, ID, groupName);
            FirebaseList firebaseList=new FirebaseList(JoinRequestActivity.this);
            firebaseList.MakeJoinRequest(joinRequest);
        }
    }
}
