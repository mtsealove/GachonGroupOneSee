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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import kr.ac.gachon.www.GachonGroup.modules.Alert;

public class RequirementsActivity extends AppCompatActivity {
    EditText titleET, emailET, contentET;
    Button sendBtn, closeBtn;
    private String title, email, content, ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requirements);
        Intent intent=getIntent();
        ID=intent.getStringExtra("ID");
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
        else {
            SendRequirement();
        }
    }
    //보내기 버튼
    private void SendRequirement() {
        AlertDialog.Builder builder=new AlertDialog.Builder(RequirementsActivity.this);
        LayoutInflater inflater=getLayoutInflater();
        View layout=inflater.inflate(R.layout.dialog_msg_choice, null);
        TextView msg= layout.findViewById(R.id.dialog_msgTV);
        msg.setText("문의하신 내용을\n보내시겠습니까?");
        Button negative= layout.findViewById(R.id.negative);
        Button positive= layout.findViewById(R.id.positive);
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
                //내용을 전달할 메서드 추가
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
        AlertDialog.Builder builder=new AlertDialog.Builder(RequirementsActivity.this);
        LayoutInflater inflater=getLayoutInflater();
        View layout=inflater.inflate(R.layout.dialog_msg_choice, null);
        builder.setView(layout);
        TextView msg= layout.findViewById(R.id.dialog_msgTV);
        msg.setText("작성을 취소하시겠습니까?");
        Button positive= layout.findViewById(R.id.positive);
        Button negative= layout.findViewById(R.id.negative);
        final AlertDialog dialog=builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                finish();
            }
        });
        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }
    @Override
    public void onBackPressed() {
        setCloseBtn();
    }
}
