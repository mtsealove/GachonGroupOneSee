package kr.ac.gachon.www.GachonGroup.Account;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import kr.ac.gachon.www.GachonGroup.Board.HomeActivity;
import kr.ac.gachon.www.GachonGroup.Board.PublicNoticeActivity;
import kr.ac.gachon.www.GachonGroup.Board.RequirementsActivity;
import kr.ac.gachon.www.GachonGroup.Entity.Account;
import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseAccount;
import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseImage;
import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseNote;
import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseView;
import kr.ac.gachon.www.GachonGroup.Group.GroupJoinRequestLogActivity;
import kr.ac.gachon.www.GachonGroup.Group.GroupMenuActivity;
import kr.ac.gachon.www.GachonGroup.JoinRequest.JoinRequestLogActivity;
import kr.ac.gachon.www.GachonGroup.Manager.AccuseLogActivity;
import kr.ac.gachon.www.GachonGroup.Manager.RequirementLogActivity;
import kr.ac.gachon.www.GachonGroup.Note.NoteListActivity;
import kr.ac.gachon.www.GachonGroup.R;
import kr.ac.gachon.www.GachonGroup.etc.Alert;
import kr.ac.gachon.www.GachonGroup.etc.ServicePolicyActivity;
import kr.ac.gachon.www.GachonGroup.etc.VersionActivity;

public class MyInformationActivity extends AppCompatActivity { //내 정보 액티비티
    TextView nameTV, groupTV, UnreadNoteTV;
    Button EditInfoBtn, logoutBtn, removeAccountBtn, myGroupBtn, requirementsBtn, joinRequestLogBtn, versionBtn, publicNoticeBtn, requirementLogBtn, accuseLogBtn, servicePlicyBtn;
    ImageView profileIcon;
    private String ID, group;
    LinearLayout userLayout, managerLayout;
    RelativeLayout noteLayout;

    Account account;
    FirebaseAccount firebaseAccount;
    FirebaseView firebaseView;
    FirebaseImage firebaseImage;
    FirebaseNote firebaseNote;
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
        removeAccountBtn= findViewById(R.id.removeAccountBtn);
        requirementsBtn= findViewById(R.id.requirementsBtn);
        joinRequestLogBtn=findViewById(R.id.joinRequestLogBtn);
        versionBtn=findViewById(R.id.versionBtn);
        publicNoticeBtn=findViewById(R.id.publicNoticeBtn);
        userLayout=findViewById(R.id.userLayout);
        managerLayout=findViewById(R.id.ManagerLayout);
        accuseLogBtn=findViewById(R.id.accuseLogBtn);
        noteLayout=findViewById(R.id.noteLayout);
        UnreadNoteTV=findViewById(R.id.unReadNoteTV);
        servicePlicyBtn=findViewById(R.id.servicePolicyBtn);

        account=new Account();
        final Intent intent=getIntent();
        ID=intent.getStringExtra("ID");
        group=intent.getStringExtra("group");   //관리자 인지를 판단


        firebaseAccount=new FirebaseAccount(MyInformationActivity.this);
        firebaseAccount.GetAccount(ID, account);

        firebaseView=new FirebaseView(MyInformationActivity.this);
        firebaseView.setTextView("name", ID, nameTV);
        firebaseView.setTextView("group", ID, groupTV);

        firebaseImage=new FirebaseImage(MyInformationActivity.this);
        firebaseImage.ShowProfileIcon(ID, profileIcon);

        firebaseNote=new FirebaseNote(this);
        firebaseNote.SetUnReadNotes(ID, UnreadNoteTV);

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
        publicNoticeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PublicNotice();
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
        noteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Note();
            }
        });
        servicePlicyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServicePolicy();
            }
        });

        versionBtn.setText("현재 버전: "+GetVersion());

        //관리자 여부에 따라 다른 레이아웃 출력
        if(group.equals("관리자")){
            //userLayout.setVisibility(View.GONE);
            //managerLayout.setVisibility(View.VISIBLE);

            joinRequestLogBtn.setVisibility(View.GONE);
            myGroupBtn.setVisibility(View.GONE);
            requirementsBtn.setText("문의사항 조회");
            requirementsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1=new Intent(MyInformationActivity.this, RequirementLogActivity.class);
                    startActivity(intent1);
                }
            });
            accuseLogBtn.setVisibility(View.VISIBLE);
            accuseLogBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1=new Intent(MyInformationActivity.this, AccuseLogActivity.class);
                    startActivity(intent1);
                }
            });
            removeAccountBtn.setVisibility(View.GONE);
        }
    }

    //정보 수정
    private void Edit_information() {
        Intent intent=new Intent(MyInformationActivity.this, EditMyInformationActivity.class);
        intent.putExtra("ID", ID); //사용자 ID 전송
        intent.putExtra("isManager", account.is_manager);   //관리자 여부
        startActivity(intent);
    }
    //로그아웃
    private void LogOut() {
        try {
            //단말에 저장되어 있는 ID와 비밀번호 삭제
            BufferedWriter bw=new BufferedWriter(new FileWriter(new File(getFilesDir()+"login.dat"), false));
            bw.write("");
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        HomeActivity HA=(HomeActivity) HomeActivity._Home_Activity;
        HA.finish(); //홈 액티비티 종료
        Intent intent=new Intent(MyInformationActivity.this, LoginActivity.class);
        intent.putExtra("logout", true); //로그인 액티비티에 로그아웃 됬음 알림
        startActivity(intent);
        finish();
    }

    //회원 탈퇴 메서드
    private void Remove_Account() {
        final Alert alert=new Alert(MyInformationActivity.this);
        alert.MsgDialogChoice("회원을 탈퇴하십니까?", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAccount.RemoveAccount(ID);
                alert.MsgDialogEnd("회원 탈퇴가 완료되었습니다");
                Alert.dialog.cancel();
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

    private void Note() {   //쪽지함
        Intent intent=new Intent(MyInformationActivity.this, NoteListActivity.class);
        intent.putExtra("userID", ID);
        startActivity(intent);
    }

    private void PublicNotice() { //공지사항 조회
        Intent intent=new Intent(MyInformationActivity.this, PublicNoticeActivity.class);
        intent.putExtra("userID", ID);
        intent.putExtra("group", account.group);
        startActivity(intent);
    }

    //동아리 신청 내역 조회
    private void JoinRequestLog() {
        //관리자이면 자신의 동아리에 신청된 내역 조회
        if(account.is_manager) {
            Intent intent=new Intent(MyInformationActivity.this, GroupJoinRequestLogActivity.class);
            intent.putExtra("groupName", account.group);
            intent.putExtra("userID", ID);
            startActivity(intent);
        } else { //일반회원이면 자신이 신청한 동아리 내역 조회
            Intent intent=new Intent(MyInformationActivity.this, JoinRequestLogActivity.class);
            intent.putExtra("userID", ID);
            startActivity(intent); }
    }




    @Override
    protected void onResume() {
        super.onResume();
        firebaseView.setTextView("name", ID, nameTV);
        firebaseView.setTextView("group", ID, groupTV);
        firebaseImage.ShowProfileIcon(ID, profileIcon);
        firebaseAccount.GetAccount(ID, account);
        firebaseNote.SetUnReadNotes(ID, UnreadNoteTV);
    }

    private String GetVersion() { //버전 확인
        PackageInfo packageInfo=null;
        try {
            packageInfo=getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionName=packageInfo.versionName;
            versionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(MyInformationActivity.this, VersionActivity.class);
                    startActivity(intent);
                }
            });
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    //문의사항
    private void Requirements() {
        Intent intent=new Intent(MyInformationActivity.this, RequirementsActivity.class);
        intent.putExtra("ID", ID);
        startActivity(intent);
    }

    private void ServicePolicy() {  //서비스 약관
        Intent intent=new Intent(this, ServicePolicyActivity.class);
        startActivity(intent);
    }

    public void close(View v) {
        finish();
    }
}
