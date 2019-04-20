package kr.ac.gachon.www.GachonGroup.Note;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseNote;
import kr.ac.gachon.www.GachonGroup.R;
import kr.ac.gachon.www.GachonGroup.etc.Alert;

public class SendNoteActivity extends AppCompatActivity {
    EditText receiverET, contentET;
    Button sendBtn;
    Alert alert;
    private String Sender, Receiver, content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_note);
        receiverET=findViewById(R.id.receiverET);
        contentET=findViewById(R.id.contentET);
        sendBtn=findViewById(R.id.sendBtn);

        alert=new Alert(this);

        getParameter();
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Send();
            }
        });
    }

    private void getParameter() {   //정보 받아오기
        Intent intent=getIntent();
        Sender=intent.getStringExtra("Sender");
        Receiver=intent.getStringExtra("Receiver");
        if(Receiver!=null&&Receiver.length()!=0) {  //미리 입력받았다면
            receiverET.setText(Receiver);
        }
    }

    private boolean CheckInput() {  //정확히 입력했는지 확인
        boolean result=false;
        Receiver=receiverET.getText().toString();
        content=contentET.getText().toString();

        if(Receiver.length()==0) Toast.makeText(SendNoteActivity.this, "받는 사람을 입력하세요", Toast.LENGTH_SHORT).show();
        else if(content.length()==0) Toast.makeText(SendNoteActivity.this, "내용을 입력하세요", Toast.LENGTH_SHORT).show();
        else result=true;

        return result;
    }

    private void Send() {   //보내기
        final FirebaseNote firebaseNote=new FirebaseNote(this);
        if(CheckInput()) {  //입력이 됬으면
            Receiver=Receiver.trim();   //띄어쓰기 제거
            final String[] Receivers=Receiver.split(",");
            alert.MsgDialogChoice("전송하시겠습니까?", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    firebaseNote.SendNote(Sender, content, Receivers);
                    Alert.dialog.cancel();  //닫기
                }
            });
        }
    }

    public void close(View v) {
        finish();
    }
}
