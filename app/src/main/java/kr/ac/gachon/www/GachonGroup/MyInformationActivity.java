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

import kr.ac.gachon.www.GachonGroup.modules.Alert;
import kr.ac.gachon.www.GachonGroup.modules.FirebaseHelper;

public class MyInformationActivity extends AppCompatActivity {
    TextView nameTV, groupTV;
    Button EditInfoBtn, logoutBtn, removeAccountBtn, myGroupBtn, myGroupScheduleBtn;
    ImageView profileIcon;
    private String ID;

    Account account;
    FirebaseHelper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_information);
        nameTV=(TextView)findViewById(R.id.nameTV);
        groupTV=(TextView)findViewById(R.id.groupTV);
        EditInfoBtn=(Button)findViewById(R.id.EditInfoBtn);
        logoutBtn=(Button)findViewById(R.id.logoutBtn);
        profileIcon=(ImageView)findViewById(R.id.userIcon);
        removeAccountBtn=(Button)findViewById(R.id.removeAccountBtn);
        myGroupBtn=(Button)findViewById(R.id.myGroupBtn);
        myGroupScheduleBtn=(Button)findViewById(R.id.MyGroupSchduleBtn);

        account=new Account();
        Intent intent=getIntent();
        ID=intent.getStringExtra("ID");
        helper=new FirebaseHelper();
        helper.GetAccount(ID, account);
        helper.setTextView("name", ID, nameTV);
        helper.setTextView("group", ID, groupTV);

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
    }

    //정보 수정 활동
    private void Edit_information() {
        Intent intent=new Intent(MyInformationActivity.this, EditMyInformationActivity.class);
        intent.putExtra("ID", ID);
        startActivity(intent);
    }
    //로그아웃
    private void LogOut() {
        HomeActivity HA=(HomeActivity)HomeActivity._Home_Activity;
        HA.finish();
        Intent intent=new Intent(MyInformationActivity.this, LoginActivity.class);
        intent.putExtra("logout", true);
        startActivity(intent);
        finish();
    }

    //회원 탈퇴 메서드
    private void Remove_Account() {
        AlertDialog.Builder builder=new AlertDialog.Builder(MyInformationActivity.this);
        LayoutInflater inflater=getLayoutInflater();
        View layout=inflater.inflate(R.layout.dialog_msg_choice, null);
        TextView msg=(TextView)layout.findViewById(R.id.dialog_msgTV);
        msg.setText("회원을 탈퇴하십니까?");
        Button negative=(Button)layout.findViewById(R.id.negative);
        Button positive=(Button)layout.findViewById(R.id.positive);
        builder.setView(layout);
        final AlertDialog dialog=builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        dialog.show();
        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.RemoveAccount(ID);
                Alert alert=new Alert();
                alert.MsgDialogEnd("회원 탈퇴가 완료되었습니다", MyInformationActivity.this);
                dialog.cancel();
            }
        });
    }

    //내 동아리 바로가기
    private void MyGroup() {
        Intent intent=new Intent(MyInformationActivity.this, GroupMenuActivity.class);
        intent.putExtra("groupName", account.group);
        startActivity(intent);
    }
    private void MyGroupSchedule() {
        Intent intent=new Intent(MyInformationActivity.this, GroupScheduleActivity.class);
        intent.putExtra("groupName", account.group);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        helper.setTextView("name", ID, nameTV);
        helper.setTextView("group", ID, groupTV);
        super.onResume();
    }
}
