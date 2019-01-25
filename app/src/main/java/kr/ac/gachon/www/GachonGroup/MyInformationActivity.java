package kr.ac.gachon.www.GachonGroup;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseAccount;
import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseView;
import kr.ac.gachon.www.GachonGroup.modules.Alert;
import kr.ac.gachon.www.GachonGroup.modules.FirebaseHelper;

public class MyInformationActivity extends AppCompatActivity {
    TextView nameTV, groupTV;
    Button EditInfoBtn, logoutBtn, removeAccountBtn, myGroupBtn, myGroupScheduleBtn, requirementsBtn, joinRequestLogBtn;
    ImageView profileIcon;
    private String ID;

    Account account;
    FirebaseAccount firebaseAccount;
    FirebaseView firebaseView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_information);
        nameTV= findViewById(R.id.nameTV);
        groupTV= findViewById(R.id.groupTV);
        EditInfoBtn= findViewById(R.id.EditInfoBtn);
        logoutBtn= findViewById(R.id.logoutBtn);
        profileIcon= findViewById(R.id.userIcon);
        removeAccountBtn= findViewById(R.id.removeAccountBtn);
        myGroupBtn= findViewById(R.id.myGroupBtn);
        myGroupScheduleBtn= findViewById(R.id.MyGroupSchduleBtn);
        removeAccountBtn= findViewById(R.id.removeAccountBtn);
        requirementsBtn= findViewById(R.id.requirementsBtn);
        joinRequestLogBtn=findViewById(R.id.joinRequestLogBtn);

        account=new Account();
        Intent intent=getIntent();
        ID=intent.getStringExtra("ID");
        firebaseAccount=new FirebaseAccount(MyInformationActivity.this);
        firebaseAccount.GetAccount(ID, account);

        firebaseView=new FirebaseView(MyInformationActivity.this);
        firebaseView.setTextView("name", ID, nameTV);
        firebaseView.setTextView("group", ID, groupTV);

        EditInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Edit_information();
            }
        });
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogOut();
            }
        });
        removeAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Remove_Account();
            }
        });
        myGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyGroup();
            }
        });
        myGroupScheduleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyGroupSchedule();
            }
        });
        requirementsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Requirements();
            }
        });
        joinRequestLogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JoinRequestLog();
            }
        });
    }

    //정보 수정 활동
    private void Edit_information() {
        Intent intent=new Intent(MyInformationActivity.this, EditMyInformationActivity.class);
        intent.putExtra("ID", ID);
        startActivity(intent);
    }
    //로그아웃
    private void LogOut() {
        try {
            BufferedWriter bw=new BufferedWriter(new FileWriter(new File(getFilesDir()+"login.dat"), false));
            bw.write("");
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        HomeActivity HA=(HomeActivity)HomeActivity._Home_Activity;
        HA.finish();
        Intent intent=new Intent(MyInformationActivity.this, LoginActivity.class);
        intent.putExtra("logout", true);
        startActivity(intent);
        finish();
    }

    //회원 탈퇴 메서드
    private void Remove_Account() {
        final Alert alert=new Alert(MyInformationActivity.this);
        alert.MsgDialogChoice("회원을 탙퇴하십니까?", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAccount.RemoveAccount(ID);
                alert.MsgDialogEnd("회원 탈퇴가 완료되었습니다");
            }
        });
    }

    //내 동아리 바로가기
    private void MyGroup() {
        if (account.group.equals("동아리 없음"))
            Toast.makeText(MyInformationActivity.this, "가입된 동아리가 없습니다", Toast.LENGTH_SHORT).show();
        else {
            Intent intent = new Intent(MyInformationActivity.this, GroupMenuActivity.class);
            intent.putExtra("ID", ID);
            intent.putExtra("groupName", account.group);
            startActivity(intent);
        }
    }
    //내 동아리 일정 바로가기
    private void MyGroupSchedule() {
        Intent intent=new Intent(MyInformationActivity.this, GroupScheduleActivity.class);
        intent.putExtra("groupName", account.group);
        startActivity(intent);
    }
    //문의사항
    private void Requirements() {
        Intent intent=new Intent(MyInformationActivity.this, RequirementsActivity.class);
        intent.putExtra("ID", ID);
        startActivity(intent);
    }
    //동아리 신청 내역 조회
    private void JoinRequestLog() {
        Intent intent=new Intent(MyInformationActivity.this, JoinRequestLogActivity.class);
        intent.putExtra("userID", ID);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        firebaseView.setTextView("name", ID, nameTV);
        firebaseView.setTextView("group", ID, groupTV);
        super.onResume();
    }
}
