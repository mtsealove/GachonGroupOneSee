package kr.ac.gachon.www.GachonGroup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseList;
import kr.ac.gachon.www.GachonGroup.modules.Alert;

public class JoinRequsetLogDetailActivity extends AppCompatActivity {
    private String ID;
    private String SelfIntroduce;
    private int StudentNumber;
    private int age;
    private String contact;
    private String group;
    private String major;
    private String name;
    private TextView SelfIntroduceTV, StudentNuberTV, ageTV, contactTV, groupTV, majorTV, nameTV;
    private Button RemoveJoinRequestBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_requset_log_detail);

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

        //모든 뷰 매칭
        SelfIntroduceTV=findViewById(R.id.selfIntroduceTV);
        StudentNuberTV=findViewById(R.id.studentNumberTV);
        ageTV=findViewById(R.id.ageTV);
        contactTV=findViewById(R.id.contactTV);
        groupTV=findViewById(R.id.groupNameTV);
        majorTV=findViewById(R.id.majorTV);
        nameTV=findViewById(R.id.nameTV);
        RemoveJoinRequestBtn=findViewById(R.id.removeBtn);

        SelfIntroduceTV.setText(SelfIntroduce);
        StudentNuberTV.setText(StudentNumberStr);
        ageTV.setText(Integer.toString(age));
        contactTV.setText(contact);
        groupTV.setText(group);
        majorTV.setText(major);
        nameTV.setText(name);

        RemoveJoinRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRemoveJoinRequestBtn();
            }
        });
    }
    //가입 신청 취소
    private void setRemoveJoinRequestBtn() {
        Alert alert=new Alert(JoinRequsetLogDetailActivity.this);
        alert.MsgDialogChoice("가입신청을 취소하시겠습니까?", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseList firebaseList=new FirebaseList(JoinRequsetLogDetailActivity.this);
                firebaseList.RemoveJoinRequest(ID, group);
            }
        });
    }
    public void close(View v) {
        finish();
    }
}
