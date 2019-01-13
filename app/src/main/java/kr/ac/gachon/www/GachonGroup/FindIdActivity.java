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
    Button check_4_ID_btn;

    EditText email_4_PW_et;
    EditText ID_4_PW_et;
    EditText verify_4_PW_et;
    Button verify_4_PW_btn;
    Button check_4_PW_btn;
    Alert alert;

    private String ID;
    private String VerifyCode;

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
                Send_Code_4_ID();
            }
        });
    }

    //아이디 찾기 메서드
    private void Send_Code_4_ID() {
        String email=email_4_ID_et.getText().toString();
        FirebaseHelper helper=new FirebaseHelper();
        ID=helper.Find_ID(email);
        if(ID.equals("NotFound")) {
            alert.MsgDialog("일치하는 계정이 없습니다", FindIdActivity.this);
        } else {
            GmailSender gmailSender=new GmailSender("mtsealove0927@gmail.com","fzdgbxjnfozylztv");
            VerifyCode=gmailSender.CreateVerifyCode();
            try {
                gmailSender.sendMail("가천대학교 동아리 한번에 보자 ID 인증메일입니다",
                        "인증번호는 "+VerifyCode+" 입니다",
                        "mtsealove0927@gmail.com",
                        email);
                Toast.makeText(FindIdActivity.this, "인증번호가 발송되었습니다", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void close(View v) {
        finish();
    }
}
