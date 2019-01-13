package kr.ac.gachon.www.GachonGroup.modules;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

import kr.ac.gachon.www.GachonGroup.Account;
import kr.ac.gachon.www.GachonGroup.R;
import kr.ac.gachon.www.GachonGroup.SignUpActivity;

public class FirebaseHelper extends AppCompatActivity {
    FirebaseDatabase database;

    public FirebaseHelper() {
        database=FirebaseDatabase.getInstance();
    }
    //ID 찾기
    public void Check_ID_Reuse(final String ID, final boolean[] reuse, final Context context) {
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Account").child(ID).exists()) {
                    Alert alert=new Alert();
                    alert.MsgDialog("중복된 아이디가 있습니다.\n다른 아이디를 입력해주세요", context);
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

    public void Check_Email_Reuse(final String email, final boolean[] reuse, final Context context) {
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
                    Alert alert=new Alert();
                    alert.MsgDialog("이미 사용중인 이메일입니다", context);
                } else {
                    GmailSender gMailSender=new GmailSender("werqtt18@gmail.com", "kffdmoebguyivmyh");
                    SignUpActivity.verify_code=gMailSender.CreateVerifyCode();
                    String msg = "가천대학교 동아리 한눈에 보자 회원가입 인증번호는 " + SignUpActivity.verify_code + " 입니다";
                    //GMailSender.sendMail(제목, 본문내용, 보내는사람, 받는사람);
                    try {
                        gMailSender.sendMail("가천대학교 동아리 한눈에 보자 인증메일입니다", msg, "mtsealove0927@gmail.com", email);
                            VerifyCode(context);
                     } catch (SendFailedException e) {
                    Toast.makeText(getApplicationContext(), "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                } catch (MessagingException e) {
                    Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주십시오", Toast.LENGTH_SHORT).show();
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

    private void VerifyCode(final Context context) {
        time=300;
        //레이아웃 inflate
        LayoutInflater inflater=LayoutInflater.from(context);
        View layout=inflater.inflate(R.layout.dialog_verify_code, null);
        //각 뷰 매칭
        final TextView timeTV=(TextView)layout.findViewById(R.id.timeTV);
        final EditText verifyET=(EditText)layout.findViewById(R.id.verify_code_ET);
        Button okay=(Button)layout.findViewById(R.id.okay);

        //남은 시간 표시
        TimerTask timerTask=new TimerTask() {
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
    public void CreateAccount(Account account) {
        String ID=account.ID;
        DatabaseReference reference=database.getReference();
        reference.child("Account").child(ID).setValue(account);
    }

    //메일을 매개변수로 찾아 ID찾기
    String ID;
    String email;
    public String Find_ID(final String email) {
        ID="NotFound";
        this.email=email;
        GetID getID=new GetID();
        getID.start();
        synchronized (getID) {
            try {
                getID.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return ID;
    }
    class GetID extends Thread{
        @Override
        public void run() {
            synchronized (this) {
                DatabaseReference reference = database.getReference();
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean done = false;
                        for (DataSnapshot snapshot : dataSnapshot.child("Account").getChildren()) {
                            if (snapshot.child("email").getValue(String.class).equals(email)) {
                                ID = snapshot.child("ID").getValue(String.class);
                                Log.d("ID", ID);
                                done = true;
                                break;
                            }
                        }
                        if (!done) ID = "NotFound";
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                notify();
            }
        }
    }
}
