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


import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.Timer;
import java.util.TimerTask;

import kr.ac.gachon.www.GachonGroup.Account.AES256Util;
import kr.ac.gachon.www.GachonGroup.Board.HomeActivity;
import kr.ac.gachon.www.GachonGroup.Entity.Account;
import kr.ac.gachon.www.GachonGroup.Account.FindIdActivity;
import kr.ac.gachon.www.GachonGroup.Account.LoginActivity;
import kr.ac.gachon.www.GachonGroup.R;
import kr.ac.gachon.www.GachonGroup.Account.SignUpActivity;
import kr.ac.gachon.www.GachonGroup.etc.Alert;
import kr.ac.gachon.www.GachonGroup.Gmail.GmailSender;

public class FirebaseAccount extends AppCompatActivity {    //Firebase를 이용한 계정 접근 클래스
    FirebaseDatabase database;
    Alert alert;
    final Context context;

    public FirebaseAccount(Context context) {
        this.context=context;
        database=FirebaseDatabase.getInstance();
        alert=new Alert(context);
    }

    public void Check_ID_Reuse(final String ID, final boolean[] reuse) { //ID 중복 확인
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Account").child(ID).exists()) { //해당 ID의 데이터가 존재하면
                    Alert alert=new Alert(context);
                    alert.MsgDialog("중복된 아이디가 있습니다.\n다른 아이디를 입력해주세요");
                    reuse[0]=false;
                } else { //사용 가능
                    reuse[0]=true;
                    Toast.makeText(context, "아이디를 사용할 수 있습니다", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void Check_Email_Reuse(final String email, final boolean[] reuse) { //이메일 사용 가능 여부 체크및 이메일 발송
        final GmailSender gmailSender=new GmailSender("werqtt18@gmail.com", "kffdmoebguyivmyh");
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean reuseb=true;
                for(DataSnapshot snapshot: dataSnapshot.child("Account").getChildren()) { //모든 계정의 이메일을 검색
                    if(snapshot.child("email").getValue(String.class).equals(email)) { //같은 이메일이 존재하면
                        reuseb=false; //사용 불가
                        break;
                    }
                }
                if(!reuseb) { //사용불가하면
                    reuse[0]=false;
                    Alert alert=new Alert(context);
                    alert.MsgDialog("이미 사용중인 이메일입니다");
                } else {
                    SignUpActivity.verify_code=gmailSender.CreateVerifyCode(); //인증번호 생성
                    String msg = "가천대학교 동아리 한눈에 보자 회원가입 인증번호는 " + SignUpActivity.verify_code + " 입니다";
                    //GMailSender.sendMail(제목, 본문내용, 보내는사람, 받는사람);
                    try {
                        //메일로 인증번호 전송
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


    int time;   //제한시간
    private void VerifyCode() { //인증번호 확인 메서드
        time=300; //제한시간 5분
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
                        if(time<0) Toast.makeText(context, "시간이 만료되었습니다", Toast.LENGTH_SHORT).show(); //시간 만료
                        else {
                            time--;
                            int min = time / 60;
                            int sec = time % 60;
                            //화면에 남은 시간 표시
                            timeTV.setText(min + "분 " + sec + "초 남음");
                        }
                    }
                });
            }
        };
        Timer timer=new Timer();
        timer.schedule(timerTask, 0, 1000); //1초에 한 번 시간 변경

        //인증번호 입력 다이얼로그 생성
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setView(layout);
        final AlertDialog dialog=builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        dialog.show();

        //확인 버튼리스너 설정
        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newCode=verifyET.getText().toString();
                if(time<0) Toast.makeText(context,"시간이 만료되었습니다", Toast.LENGTH_SHORT).show(); //시간 초과 시
                else if(newCode.equals(SignUpActivity.verify_code)) { //입력받은 코드와 생성된 코드가 일치할 셩우
                    Toast.makeText(context, "인증이 완료되었습니다", Toast.LENGTH_SHORT).show();
                    SignUpActivity.Verified=true;
                    dialog.cancel();
                    SignUpActivity.emailET.setEnabled(false);
                    //인증 완료
                } else Toast.makeText(context, "인증번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show(); //인증번호 불일치
            }
        });

    }

    public void CreateAccount(final Account account) {  //계정 생성 메서드
        final String ID=account.ID;
        final DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //관리자 child에 해당 전화번호가 존재하면 관리자 여부를 true로 설정
                for(DataSnapshot snapshot:dataSnapshot.child("Manager").getChildren()) {
                    if (snapshot.child("phone").getValue(String.class).equals(account.phone))
                        account.is_manager = true;
                    //비밀번호 암호화
                    try {
                        AES256Util aes256Util = new AES256Util();
                        account.password=aes256Util.encrypt(account.password);
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                }

                //DB에 모든 정보 저장
                reference.child("Account").child(ID).child("ID").setValue(account.getID());
                reference.child("Account").child(ID).child("StudentNumber").setValue(account.getStudentNumber());
                reference.child("Account").child(ID).child("email").setValue(account.getEmail());
                reference.child("Account").child(ID).child("is_manager").setValue(account.isIs_manager());
                reference.child("Account").child(ID).child("major").setValue(account.getMajor());
                reference.child("Account").child(ID).child("group").setValue(account.getGroup());
                reference.child("Account").child(ID).child("name").setValue(account.getName());
                reference.child("Account").child(ID).child("password").setValue(account.getPassword());
                reference.child("Account").child(ID).child("phone").setValue(account.getPhone());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //아이디 찾기 인증번호 발송
    public void Find_ID_mail(final String email) {
        final GmailSender gmailSender=new GmailSender("werqtt18@gmail.com", "kffdmoebguyivmyh");
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String ID=null;
                for(DataSnapshot snapshot:dataSnapshot.child("Account").getChildren()) {    //Account에서 모든 계정의 email검색
                    if (snapshot.child("email").getValue(String.class).equals(email)) { //일치하는 계정 발견
                        ID=snapshot.child("ID").getValue(String.class); //ID를 저장
                        break;
                    }
                }
                if(ID==null) Toast.makeText(context, "존재하지 않는 이메일입니다", Toast.LENGTH_SHORT).show();  //ID가 없으면
                else {  //인증코드 생성
                    String verifyCode=gmailSender.CreateVerifyCode();
                    try {
                        //메일로 인증번호 발송
                        gmailSender.sendMail("가천대학교 동아리 한눈에보자 ID 찾기 인증번호",
                                "가천대학교 동아리 ID 찾기 인증번호는 "+verifyCode+"입니다.",
                                "werqtt18@gmail.com",
                                email);
                        Toast.makeText(context, "인증번호가 발송되었습니다", Toast.LENGTH_SHORT).show();
                        FindIdActivity.VerifyCode=verifyCode;   //ID 찾기 액티비티의 인증번호 변경
                        FindIdActivity.ID=ID;   //ID 찾기 액티비티의 ID 변경
                        FindIdActivity.check_4_ID_btn.setVisibility(View.VISIBLE);  //인증번호 입력 버튼 활성화
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

    public void Find_PW_mail(final String email, final String ID) { //비밀번호 찾기
        final GmailSender gmailSender=new GmailSender("werqtt18@gmail.com", "kffdmoebguyivmyh");
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String PW=null;
                for(DataSnapshot snapshot: dataSnapshot.child("Account").getChildren()) {
                    if(snapshot.child("ID").getValue(String.class).equals(ID)) {    //입력한 ID 일치하는 ID찾기
                        if(snapshot.child("email").getValue(String.class).equals(email)) {  //입력한 ID의 이메일과 사용자가 입력한 이메일이 일치하는지 확인
                            PW=snapshot.child("password").getValue(String.class);   //비밀번호 저장
                            AES256Util aes256Util=new AES256Util();
                            break;
                        } else Toast.makeText(context, "메일 주소가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                    }
                }
                if(PW==null) Toast.makeText(context, "ID가 존재하지 않습니다", Toast.LENGTH_SHORT).show();
                else {
                    String verifyCode=gmailSender.CreateVerifyCode();   //인증번호 생성
                    try {   //인증번호 메일로 발송
                        gmailSender.sendMail("가천대학교 동아리 한눈에보자 비밀번호 찾기 인증번호",
                                "가천대학교 동아리 비밀번호 찾기 인증번호는 "+verifyCode+" 입니다.",
                                "werqtt18@gmail.com",
                                email);
                        Toast.makeText(context, "인증번호가 발송되었습니다" ,Toast.LENGTH_SHORT).show();
                        FindIdActivity.VerifyCode=verifyCode;   //비밀번호 찾기 액티비티의 인증번호 변경
                        AES256Util aes256Util=new AES256Util();
                        PW=aes256Util.decrypt(aes256Util.decrypt(PW));  //비밀번호 복호화
                        FindIdActivity.password=PW;
                        FindIdActivity.check_4_PW_btn.setVisibility(View.VISIBLE);  //인증번호 입력 버튼 활성화
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

    //ID를 기반으로 다른 정보를 Account클래스에 저장(계정 복사)
    public void GetAccount(final String ID, final Account account) {
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {  //ID를 기반으로 나머지 정보 가져오기
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

    public void Login(final String ID, final String PW) {   //로그인
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean find=false;
                for(DataSnapshot snapshot: dataSnapshot.child("Account").getChildren()) {
                    if(snapshot.child("ID").getValue(String.class).equals(ID)) {    //존재하는 ID 인지 확인
                        find=true;
                        if(snapshot.child("password").getValue(String.class).equals(PW)) {  //비밀번호가 일치하는지 확인
                            String name=snapshot.child("name").getValue(String.class);
                            String email=snapshot.child("email").getValue(String.class);
                            String major=snapshot.child("major").getValue(String.class);
                            int StudentNumber=snapshot.child("StudentNumber").getValue(Integer.class);
                            String group=snapshot.child("group").getValue(String.class);
                            String phone=snapshot.child("phone").getValue(String.class);
                            boolean isManager=snapshot.child("is_manager").getValue(Boolean.class);
                            LoginActivity.account=new Account(name, ID, email, major, StudentNumber, group, PW, phone, isManager);  //계정 생성
                            moveHome(ID);   //홈 액티비티로 이동
                        } else {
                            LoginActivity.pendingLayout.setVisibility(View.GONE);
                            Toast.makeText(context, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                if(!find) {
                    LoginActivity.pendingLayout.setVisibility(View.GONE);
                    Toast.makeText(context, "일치하는 ID를 찾지 못했습니다", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //LoadActivityty에서 자동 로그인
    public void AutoLogin(final String ID, final String PW) {
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean done=false;
                for(DataSnapshot snapshot:dataSnapshot.child("Account").getChildren()) {
                    if(snapshot.child("ID").getValue(String.class).equals(ID)) {    //ID와 비밀번호가 일치하면
                        if(snapshot.child("password").getValue(String.class).equals(PW)) {
                            String name=snapshot.child("name").getValue(String.class);
                            String email=snapshot.child("email").getValue(String.class);
                            String major=snapshot.child("major").getValue(String.class);
                            int StudentNumber=snapshot.child("StudentNumber").getValue(Integer.class);
                            String group=snapshot.child("group").getValue(String.class);
                            String phone=snapshot.child("phone").getValue(String.class);
                            boolean isManager=snapshot.child("is_manager").getValue(Boolean.class);
                            LoginActivity.account=new Account(name, ID, email, major, StudentNumber, group, PW, phone, isManager);
                            done=true;
                            moveHome(ID);   //홈 화면으로 이동
                        } else moveLogin();
                    }
                }
                if(!done) moveLogin();  //일치하는 계정을 찾지 못하면 로그인 화면으로 이동(비밀번호가 변경된 사유)
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //로그인 화면 이동
    private void moveLogin() {
        Intent intent=new Intent(context, LoginActivity.class);
        context.startActivity(intent);
        ((Activity)context).finish();   //현재 실행되는 액티비티 종료
    }

    //홈화면 이동
    private void moveHome(String ID) {
        Intent home=new Intent();
        home.putExtra("ID", ID);    //ID전달
        home.setClass(context, HomeActivity.class);
        home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(home);
        ((Activity)context).finish();   //현재 실행되는 액티비티 종료
    }

    //계정 정보 수정
    public void UpdateAccountData(final String ID, final String child, String value) {
        DatabaseReference reference=database.getReference();
        if(child.equals("password")) {  //비밀번호일 경우 입력받은 값을 암호화
            try {
                AES256Util aes256Util=new AES256Util();
                value=aes256Util.encrypt(value);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
        }
        reference.child("Account").child(ID).child(child).setValue(value);  //DB 업데이트
    }
    //계정 정보 수정(int 값)
    public void UpdateAccountData(final String ID, final String child, final int value) {
        DatabaseReference reference=database.getReference();
        reference.child("Account").child(ID).child(child).setValue(value);  //DB 업데이트
    }

    //계정 삭제
    public void RemoveAccount(String ID) {
        DatabaseReference reference=database.getReference();
        reference.child("Account").child(ID).setValue(null);    //데이터 삭제
    }
}
