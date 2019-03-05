package kr.ac.gachon.www.GachonGroup.Board;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseBoard;
import kr.ac.gachon.www.GachonGroup.R;
import kr.ac.gachon.www.GachonGroup.etc.Alert;

public class AccuseActivity extends AppCompatActivity {
    int[] ButtonID=new int[6];
    TextView[] buttons=new TextView[6];
    EditText reasonET;
    private String Reason=null;
    Button accuseBtn;
    private String userID, boardName, boardID, groupName, replyID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accuse);

        //각 버튼 매칭
        ButtonID[0]=R.id.accsue1;
        ButtonID[1]=R.id.accuse2;
        ButtonID[2]=R.id.accuse3;
        ButtonID[3]=R.id.accuse4;
        ButtonID[4]=R.id.accuse5;
        ButtonID[5]=R.id.accuse6;
        reasonET=findViewById(R.id.reasonET);
        accuseBtn=findViewById(R.id.accuseBtn);

        //사유를 누를 경우 표시
        for(int i=0; i<6; i++) {
            buttons[i] = findViewById(ButtonID[i]);
            final int finalI = i;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Reason=buttons[finalI].getText().toString();
                    for(int i=0; i<6; i++) buttons[i].setBackground(getDrawable(R.drawable.btn_clear_list_normal));
                    buttons[finalI].setBackground(getDrawable(R.drawable.btn_clear_list_pressed));
                }
            });
        }

        Intent intent=getIntent();
        userID=intent.getStringExtra("userID");
        boardName=intent.getStringExtra("boardName");
        boardID=Integer.toString(intent.getIntExtra("id", 0));
        groupName=intent.getStringExtra("groupName");
        replyID=intent.getStringExtra("replyID");
        accuseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Accuse();
            }
        });
    }

    //신고
    private void Accuse() {
        //기타 사유가 있으면 기타 사유로 변경
        if(reasonET.getText().toString().length()!=0) Reason=reasonET.getText().toString();
        //사유 체크 여부 확인
        if(Reason.length()==0) Toast.makeText(AccuseActivity.this, "신고 내용을 선택하세요", Toast.LENGTH_SHORT).show();
        else {
            Alert alert=new Alert(AccuseActivity.this);
            alert.MsgDialogChoice("신고하시겠습니까?",  new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseBoard firebaseBoard=new FirebaseBoard(AccuseActivity.this);
                    //동아리가 없고 댓글이 아닐 경우
                    if(groupName==null&&replyID==null)
                    firebaseBoard.Accuse(boardName, boardID, userID, Reason);
                    //동아리가 있고 댓글일 경우
                    else if(groupName!=null&&replyID==null) firebaseBoard.Accuse(groupName, boardName, boardID, userID, Reason);
                    //동아리가 없고 댓글일 경우
                    else if(groupName==null&&replyID!=null) firebaseBoard.AccuseReply(boardName, boardID, userID, Reason, replyID);
                    //동아리가 있고 댓글일 경우
                    else firebaseBoard.AccuseReply(groupName, boardName, boardID, userID, Reason, replyID);
                    Toast.makeText(AccuseActivity.this, "신고가 완료되었습니다", Toast.LENGTH_SHORT).show();
                    Alert.dialog.cancel();
                    finish();
                }
            });
        }
    }
    public void close(View v) {
        finish();
    }
}
