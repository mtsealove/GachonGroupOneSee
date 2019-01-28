package kr.ac.gachon.www.GachonGroup.Group;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import kr.ac.gachon.www.GachonGroup.Entity.Account;
import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseAccount;
import kr.ac.gachon.www.GachonGroup.Board.InformationBoardActivity;
import kr.ac.gachon.www.GachonGroup.JoinRequest.JoinRequestActivity;
import kr.ac.gachon.www.GachonGroup.R;

public class GroupMenuActivity extends AppCompatActivity {
    TextView groupNameTV;
    Button groupScheduleBtn, groupIntroduceBtn, groupJoinRequestBtn, informationBtn, groupQnABtn;
    private String groupName, ID;
    private Account account;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_menu);

        groupNameTV= findViewById(R.id.titleTV);
        groupScheduleBtn= findViewById(R.id.groupScheduleBtn);
        groupIntroduceBtn= findViewById(R.id.introduceBtn);
        groupJoinRequestBtn=findViewById(R.id.groupJoinBtn);
        informationBtn=findViewById(R.id.informationBtn);
        groupQnABtn=findViewById(R.id.groupQnABtn);

        //동아리 이름을 받아와 설정
        Intent intent=getIntent();
        groupName=intent.getStringExtra("groupName");
        ID=intent.getStringExtra("ID");
        //계정 복사
        account=new Account();
        FirebaseAccount firebaseAccount=new FirebaseAccount(GroupMenuActivity.this);
        firebaseAccount.GetAccount(ID, account);

        groupNameTV.setText(groupName);
        groupScheduleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupSchedule();
            }
        });
        groupIntroduceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Introduce();
            }
        });
        groupJoinRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JoinRequest();
            }
        });
        informationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InformationBoard();
            }
        });
        groupQnABtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupQnA();
            }
        });
    }

    private void GroupSchedule() {
        Intent intent=new Intent(GroupMenuActivity.this, GroupScheduleActivity.class);
        groupName=TrimName(groupName);
        intent.putExtra("groupName", groupName);
        startActivity(intent);
    }

    private void Introduce() {
        Intent intent=new Intent(GroupMenuActivity.this, IntroduceActivity.class);
        groupName=TrimName(groupName);
        intent.putExtra("group", groupName);
        intent.putExtra("is_manager", account.is_manager);
        intent.putExtra("userGroup", account.group);
        startActivity(intent);
    }

    private void JoinRequest() {
        if(groupName.equals(account.group))
            Toast.makeText(GroupMenuActivity.this, "자신이 속한 동아리는 신청할 수 없습니다", Toast.LENGTH_SHORT).show();
        else if(account.is_manager) Toast.makeText(GroupMenuActivity.this, "관리자는 가입신청을 할 수 없습니다", Toast.LENGTH_SHORT).show();
        else {
            Intent intent = new Intent(GroupMenuActivity.this, JoinRequestActivity.class);
            groupName=TrimName(groupName);
            intent.putExtra("groupName", groupName);
            intent.putExtra("ID", ID);
            startActivity(intent);
        }
    }
    private void InformationBoard() {
        Intent intent=new Intent(GroupMenuActivity.this, InformationBoardActivity.class);
        groupName=TrimName(groupName);
        intent.putExtra("groupName", groupName);
        intent.putExtra("ID", ID);
        startActivity(intent);
    }
    private void GroupQnA() {
        Intent intent=new Intent(GroupMenuActivity.this, GroupQnAActivity.class);
        groupName=TrimName(groupName);
        intent.putExtra("groupName", groupName);
        intent.putExtra("ID", ID);
        startActivity(intent);
    }

    private String TrimName(String name) {
        name.replace(".", "");
        return name;
    }
    public void back(View v) {
        onBackPressed();
    }
}
