package kr.ac.gachon.www.GachonGroup.FirebaseActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

import kr.ac.gachon.www.GachonGroup.Account;
import kr.ac.gachon.www.GachonGroup.FindIdActivity;
import kr.ac.gachon.www.GachonGroup.LoginActivity;
import kr.ac.gachon.www.GachonGroup.R;
import kr.ac.gachon.www.GachonGroup.SignUpActivity;
import kr.ac.gachon.www.GachonGroup.modules.Alert;
import kr.ac.gachon.www.GachonGroup.modules.GmailSender;

//Firebase를 이용한 계정 접근 메서드
public class FirebaseAccount extends AppCompatActivity {
    FirebaseDatabase database;
    final Context context;

    public FirebaseAccount(Context context) {
        this.context=context;
        database=FirebaseDatabase.getInstance();
    }

    //ID 찾기
    public void Check_ID_Reuse(final String ID, final boolean[] reuse) {
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Account").child(ID).exists()) {
                    Alert alert=new Alert(context);
                    alert.MsgDialog("중복된 아이디가 있습니다.\n다른 아이디를 입력해주세요");
                    reuse[0]=false;
                } else {
                    reuse[0]=true;
                    Toast.makeText(context, "아이디를 사용할 수 있습니다", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void Check_Email_Reuse(final String email, final boolean[] reuse) {
        final GmailSender gmailSender=new GmailSender("werqtt18@gmail.com", "kffdmoebguyivmyh");
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean reuseb=true;
                for(DataSnapshot snapshot: dataSnapshot.child("Account").getChildren()) {
                    if(snapshot.child("email").getValue(String.class).equals(email)) {
                        reuseb=false;
                    }
                }
                if(!reuseb) {
                    reuse[0]=false;
                    Alert alert=new Alert(context);
                    alert.MsgDialog("이미 사용중인 이메일입니다");
                } else {

                    SignUpActivity.verify_code=gmailSender.CreateVerifyCode();
                    String msg = "가천대학교 동아리 한눈에 보자 회원가입 인증번호는 " + SignUpActivity.verify_code + " 입니다";
                    //GMailSender.sendMail(제목, 본문내용, 보내는사람, 받는사람);
                    try {
                        gmailSender.sendMail("가천대학교 동아리 한눈에 보자 회원가입 인증메일입니다", msg, "werqtt18@gmail.com", email);
                        VerifyCode();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //인증번호 확인 메서드
    int time;

    private void VerifyCode() {
        time=300;
        //레이아웃 inflate
        LayoutInflater inflater=LayoutInflater.from(context);
        View layout=inflater.inflate(R.layout.dialog_verify_code, null);
        //각 뷰 매칭
        final TextView timeTV=layout.findViewById(R.id.timeTV);
        final EditText verifyET=layout.findViewById(R.id.verify_code_ET);
        Button okay= layout.findViewById(R.id.okay);

        //남은 시간 표시
        TimerTask timerTask=new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(time<0) Toast.makeText(context, "시간이 만료되었습니다", Toast.LENGTH_SHORT).show();
                        else {
                            time--;
                            int min = time / 60;
                            int sec = time % 60;
                            timeTV.setText(min + "분 " + sec + "초 남음");
                        }
                    }
                });
            }
        };
        Timer timer=new Timer();
        timer.schedule(timerTask, 0, 1000);



        //다이얼로그 생성
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setView(layout);
        final AlertDialog dialog=builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        dialog.show();

        //확인 버튼
        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newCode=verifyET.getText().toString();
                if(time<0) Toast.makeText(context,"시간이 만료되었습니다", Toast.LENGTH_SHORT).show();
                else if(newCode.equals(SignUpActivity.verify_code)) {
                    Toast.makeText(context, "인증이 완료되었습니다", Toast.LENGTH_SHORT).show();
                    SignUpActivity.Verified=true;
                    dialog.cancel();
                    SignUpActivity.emailET.setEnabled(false);
                } else Toast.makeText(context, "인증번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
            }
        });

    }

    //계정 생성 메서드
    public void CreateAccount(final Account account) {
        final String ID=account.ID;
        final DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //관리자 child에 해당 전화번호가 존재하면 관리자 여부를 true로 설정
                for(DataSnapshot snapshot:dataSnapshot.child("Manager").getChildren()) {
                    if(snapshot.child("phone").getValue(String.class).equals(account.phone))
                        account.is_manager=true;
                }
                reference.child("Account").child(ID).setValue(account);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //아이디 찾기 인증번호 발송 메서드
    public void Find_ID_mail(final String email) {
        final GmailSender gmailSender=new GmailSender("werqtt18@gmail.com", "kffdmoebguyivmyh");
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String ID=null;
                for(DataSnapshot snapshot:dataSnapshot.child("Account").getChildren()) {
                    if (snapshot.child("email").getValue(String.class).equals(email)) {
                        ID=snapshot.child("ID").getValue(String.class);
                        break;
                    }
                }
                if(ID==null) Toast.makeText(context, "존재하지 않는 이메일입니다", Toast.LENGTH_SHORT).show();
                else {
                    String verifyCode=gmailSender.CreateVerifyCode();
                    try {
                        gmailSender.sendMail("가천대학교 동아리 한눈에보자 ID 찾기 인증번호",
                                "가천대학교 동아리 ID 찾기 인증번호는 "+verifyCode+"입니다.",
                                "werqtt18@gmail.com",
                                email);
                        Toast.makeText(context, "인증번호가 발송되었습니다", Toast.LENGTH_SHORT).show();
                        FindIdActivity.VerifyCode=verifyCode;
                        FindIdActivity.ID=ID;
                        FindIdActivity.check_4_ID_btn.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //비밀번호 찾기 메서드
    public void Find_PW_mail(final String email, final String ID) {
        final GmailSender gmailSender=new GmailSender("werqtt18@gmail.com", "kffdmoebguyivmyh");
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String PW=null;
                for(DataSnapshot snapshot: dataSnapshot.child("Account").getChildren()) {
                    if(snapshot.child("ID").getValue(String.class).equals(ID)) {
                        if(snapshot.child("email").getValue(String.class).equals(email)) {
                            PW=snapshot.child("password").getValue(String.class);
                            break;
                        } else Toast.makeText(context, "메일 주소가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                    }
                }
                if(PW==null) Toast.makeText(context, "ID가 존재하지 않습니다", Toast.LENGTH_SHORT).show();
                else {
                    String verifyCode=gmailSender.CreateVerifyCode();
                    try {
                        gmailSender.sendMail("가천대학교 동아리 한눈에보자 비밀번호 찾기 인증번호",
                                "가천대학교 동아리 비밀번호 찾기 인증번호는 "+verifyCode+" 입니다.",
                                "werqtt18@gmail.com",
                                email);
                        Toast.makeText(context, "인증번호가 발송되었습니다" ,Toast.LENGTH_SHORT).show();
                        FindIdActivity.VerifyCode=verifyCode;
                        FindIdActivity.password=PW;
                        FindIdActivity.check_4_PW_btn.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //ID를 기반으로 다른 정보를 Account클래스에 저장
    public void GetAccount(final String ID, final Account account) {
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name=dataSnapshot.child("Account").child(ID).child("name").getValue(String.class);
                String email=dataSnapshot.child("Account").child(ID).child("email").getValue(String.class);
                String major=dataSnapshot.child("Account").child(ID).child("major").getValue(String.class);
                int StudentNumber=dataSnapshot.child("Account").child(ID).child("StudentNumber").getValue(Integer.class);
                String group=dataSnapshot.child("Account").child(ID).child("group").getValue(String.class);
                String password=dataSnapshot.child("Account").child(ID).child("password").getValue(String.class);
                String phone=dataSnapshot.child("Account").child(ID).child("phone").getValue(String.class);
                boolean is_manager=dataSnapshot.child("Account").child(ID).child("is_manager").getValue(Boolean.class);
                account.CopyAccount(new Account(name, ID, email, major, StudentNumber, group, password, phone, is_manager));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //로그인 메서드
    public void Login(final String ID, final String PW, final boolean isManager) {
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean find=false;
                for(DataSnapshot snapshot: dataSnapshot.child("Account").getChildren()) {
                    if(snapshot.child("ID").getValue(String.class).equals(ID)) {
                        find=true;
                        if(snapshot.child("password").getValue(String.class).equals(PW)) {
                            if(isManager) {
                                if(snapshot.child("is_manager").getValue(Boolean.class)==isManager) {
                                    String name=snapshot.child("name").getValue(String.class);
                                    String email=snapshot.child("email").getValue(String.class);
                                    String major=snapshot.child("major").getValue(String.class);
                                    int StudentNumber=snapshot.child("StudentNumber").getValue(Integer.class);
                                    String group=snapshot.child("group").getValue(String.class);
                                    String phone=snapshot.child("phone").getValue(String.class);
                                    LoginActivity.account=new Account(name, ID, email, major, StudentNumber, group, PW, phone, isManager);
                                    moveHome(ID);
                                } else Toast.makeText(context, "관리자 계정이 아닙니다", Toast.LENGTH_SHORT).show();
                            } else {
                                String name=snapshot.child("name").getValue(String.class);
                                String email=snapshot.child("email").getValue(String.class);
                                String major=snapshot.child("major").getValue(String.class);
                                int StudentNumber=snapshot.child("StudentNumber").getValue(Integer.class);
                                String group=snapshot.child("group").getValue(String.class);
                                String phone=snapshot.child("phone").getValue(String.class);
                                LoginActivity.account=new Account(name, ID, email, major, StudentNumber, group, PW, phone, isManager);
                                moveHome(ID);
                            }
                        } else Toast.makeText(context, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                    }
                }
                if(!find) Toast.makeText(context, "일치하는 ID를 찾지 못했습니다", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //홈화면 이동
    private void moveHome(String ID) {
        Intent home=new Intent();
        home.putExtra("ID", ID);
        home.setClass(context, kr.ac.gachon.www.GachonGroup.HomeActivity.class);
        home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(home);

        ((Activity)context).finish();
    }

    //계정 정보 수정
    public void UpdateAccountData(final String ID, final String child, final String value) {
        DatabaseReference reference=database.getReference();
        reference.child("Account").child(ID).child(child).setValue(value);
    }
    //계정 정보 수정(int값)
    public void UpdateAccountData(final String ID, final String child, final int value) {
        DatabaseReference reference=database.getReference();
        reference.child("Account").child(ID).child(child).setValue(value);
    }

    //계정 삭제
    public void RemoveAccount(String ID) {
        DatabaseReference reference=database.getReference();
        reference.child("Account").child(ID).setValue(null);
    }
}
