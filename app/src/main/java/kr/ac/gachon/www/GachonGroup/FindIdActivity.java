package kr.ac.gachon.www.GachonGroup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import kr.ac.gachon.www.GachonGroup.modules.Alert;
import kr.ac.gachon.www.GachonGroup.modules.FirebaseHelper;
import kr.ac.gachon.www.GachonGroup.modules.GmailSender;

public class FindIdActivity extends AppCompatActivity {
    EditText email_4_ID_et;
    EditText verify_4_ID_et;
    Button verify_4_ID_btn;
    public static Button check_4_ID_btn;

    EditText email_4_PW_et;
    EditText ID_4_PW_et;
    EditText verify_4_PW_et;
    Button verify_4_PW_btn;
    public static Button check_4_PW_btn;
    Alert alert;

    public static String ID; //찾을 ID
    public static String VerifyCode; //인증번호
    public static String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_id);
        alert=new Alert();

        //각 객체 매칭
        email_4_ID_et=(EditText)findViewById(R.id.email4IDET);
        verify_4_ID_et=(EditText)findViewById(R.id.verify4IDET);
        verify_4_ID_btn=(Button)findViewById(R.id.verify4IDBTN);
        check_4_ID_btn=(Button)findViewById(R.id.check4IDBTN);

        email_4_PW_et=(EditText)findViewById(R.id.email4PWET);
        ID_4_PW_et=(EditText)findViewById(R.id.ID4PWET);
        verify_4_PW_et=(EditText)findViewById(R.id.verfy4PWET);
        verify_4_PW_btn=(Button)findViewById(R.id.verify4PWBTN);
        check_4_PW_btn=(Button)findViewById(R.id.check4PWBTN);

        verify_4_ID_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Send_ID_mail();
            }
        });
        check_4_ID_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Check_VerifyCode_4_ID();
            }
        });
        verify_4_PW_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Send_PW_email();
            }
        });
        check_4_PW_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Check_VerifyCode_4_PW();
            }
        });
    }


    //아이디 메일 메서드
    private void Send_ID_mail() {
        String email=email_4_ID_et.getText().toString();
        if(email.length()==0) Toast.makeText(FindIdActivity.this, "이메일을 입력해 주세요", Toast.LENGTH_SHORT).show();
        else if((!email.contains("@gachon.ac.kr"))&&(!email.contains("@gc.gachon.ac.kr"))&&(!email.contains("@mc.gachon.ac.kr")))
            Toast.makeText(FindIdActivity.this, "올바른 이메일을 입력해 주세요", Toast.LENGTH_SHORT).show();
        else {
            FirebaseHelper helper = new FirebaseHelper();
            helper.Find_ID_mail(email, FindIdActivity.this);
        }
    }

    //아이디 인증번호 대조 메서드
    private void Check_VerifyCode_4_ID() {
        String newCode=verify_4_ID_et.getText().toString();
        if(newCode.length()==0) Toast.makeText(FindIdActivity.this, "인증번호를 입력해 주세요", Toast.LENGTH_SHORT).show();
        else if(!newCode.equals(VerifyCode)) Toast.makeText(FindIdActivity.this, "인증번호가 틀렸습니다", Toast.LENGTH_SHORT).show();
        else {
            alert.MsgDialog("회원님의 아이디는\n"+ID+" 입니다", FindIdActivity.this);
       }
    }

    //비밀번호 메일 메서드
    private void Send_PW_email() {
        String ID=ID_4_PW_et.getText().toString();
        String email=email_4_PW_et.getText().toString();
        if(ID.length()==0) Toast.makeText(FindIdActivity.this, "ID를 입력해 주세요", Toast.LENGTH_SHORT).show();
        else if(email.length()==0) Toast.makeText(FindIdActivity.this, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show();
        else if((!email.contains("@gachon.ac.kr"))&&(!email.contains("@gc.gachon.ac.kr"))&&(!email.contains("@mc.gachon.ac.kr")))
            Toast.makeText(FindIdActivity.this, "올바른 이메일을 입력해 주세요", Toast.LENGTH_SHORT).show();
        else {
            FirebaseHelper helper=new FirebaseHelper();
            helper.Find_PW_mail(email, ID, FindIdActivity.this);
        }
    }

    //비밀번호 인증번호 대조 메서드
    private void Check_VerifyCode_4_PW() {
        String newCode=verify_4_PW_et.getText().toString();
        if(newCode.length()==0) Toast.makeText(FindIdActivity.this, "인증번호를 입력해 주세요", Toast.LENGTH_SHORT).show();
        else if(!newCode.equals(VerifyCode)) Toast.makeText(FindIdActivity.this, "인증번호가 틀렸습니다", Toast.LENGTH_SHORT).show();
        else {
            alert.MsgDialog("회원님의 비밀번호는\n"+password+" 입니다", FindIdActivity.this);
        }
    }
    public void close(View v) {
        finish();
    }
}
