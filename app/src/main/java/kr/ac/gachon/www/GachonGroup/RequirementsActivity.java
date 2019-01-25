package kr.ac.gachon.www.GachonGroup;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import kr.ac.gachon.www.GachonGroup.modules.Alert;
import kr.ac.gachon.www.GachonGroup.modules.GmailSender;

public class RequirementsActivity extends AppCompatActivity {
    EditText titleET, emailET, contentET;
    Button sendBtn, closeBtn;
    private String title, email, content, ID;
    Alert alert;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requirements);
        Intent intent=getIntent();
        ID=intent.getStringExtra("ID");
        alert=new Alert(RequirementsActivity.this);
        //뷰 매칭
        titleET= findViewById(R.id.titleET);
        emailET= findViewById(R.id.emailET);
        contentET= findViewById(R.id.contentET);

        sendBtn= findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSendBtn();
            }
        });

        closeBtn= findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCloseBtn();
            }
        });
    }
    //버튼 클릭시 미입력 체크
    private void setSendBtn() {
        title=titleET.getText().toString();
        email=emailET.getText().toString();
        content=contentET.getText().toString();

        if(title.length()==0) Toast.makeText(RequirementsActivity.this, "제목을 입력해 주세요", Toast.LENGTH_SHORT).show();
        else if(email.length()==0) Toast.makeText(RequirementsActivity.this, "이메일을 입력해 주세요", Toast.LENGTH_SHORT).show();
        else if(content.length()==0) Toast.makeText(RequirementsActivity.this, "내용을 입력해 주세요", Toast.LENGTH_SHORT).show();
        else if((!email.contains("@"))||((!email.contains(".com"))&&(!email.contains(".kr"))&&(!email.contains(".net"))))
            Toast.makeText(RequirementsActivity.this, "이메일 형식을 확인해 주세요", Toast.LENGTH_SHORT).show();
        else {
            SendRequirement();
        }
    }
    //보내기 버튼
    private void SendRequirement() {
        alert.MsgDialogChoice("문의하신 내용을 보내시겠습니까?",  new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SendMail()) alert.MsgDialogEnd("문의사항이 전달되었습니다");
                else alert.MsgDialogEnd("메일 발송에 실패하였습니다\n잠시 후 다시 시도해 주세요");
            }
        });
    }

    private void setCloseBtn() {
        title=titleET.getText().toString();
        email=emailET.getText().toString();
        content=contentET.getText().toString();

        if(title.length()==0&&email.length()==0&&content.length()==0) {
            finish();
        } else {
            setCloseDialog();
        }
    }
    private void setCloseDialog() {
        alert.MsgDialogChoice("작성을 취소하시겠습니까?",  new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //메일 보내기
    private boolean SendMail() {
        GmailSender sender=new GmailSender("werqtt18@gmail.com", "kffdmoebguyivmyh");
        try {
            String MailContent="보낸 사람: "+email;
            MailContent+="\n내용:\n"+content;
            sender.sendMail(title, MailContent,"werqtt18@gmail.com", "werqtt18@gmail.com");
            return true;
        }catch (Exception e) {
            Log.d("RequirementActiviy", "메일 발송 실패");
            return false;
        }
    }
    @Override
    public void onBackPressed() {
        setCloseBtn();
    }
}
