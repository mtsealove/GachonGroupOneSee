package kr.ac.gachon.www.GachonGroup.Account;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseNote;
import kr.ac.gachon.www.GachonGroup.R;

public class NoteListActivity extends AppCompatActivity {
    Button receiveBtn, sendBtn;
    ListView receiveList, sendList;
    private String userID;
    final int RECEIVE=1, SEND=2;
    int current=RECEIVE;    //삭제를 위해 가지는 변수
    FirebaseNote firebaseNote;
    ArrayList<Integer> receiveIDS, sendIDS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);

        receiveBtn=findViewById(R.id.receiveBtn);
        sendBtn=findViewById(R.id.sendBtn);
        receiveList=findViewById(R.id.receiveNoteList); //받은 리스트
        sendList=findViewById(R.id.sendNoteList);   //보낸 리스트

        sendBtn.setOnClickListener(tabBtnListener);
        receiveBtn.setOnClickListener(tabBtnListener);

        firebaseNote=new FirebaseNote(NoteListActivity.this);

        init();
    }

    View.OnClickListener tabBtnListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {   //양 버튼 클릭 리스너
            switch (v.getId()) {
                case R.id.receiveBtn:   //받은 쪽지함 눌렀을 떄
                    receiveBtn.setBackground(getDrawable(R.drawable.note_selected));
                    sendBtn.setBackground(getDrawable(R.drawable.note_normal));
                    receiveList.setVisibility(View.VISIBLE);
                    sendList.setVisibility(View.GONE);
                    current=RECEIVE;
                    break;
                case R.id.sendBtn:  //보낸 쪽지함 눌렀을 때
                    sendBtn.setBackground(getDrawable(R.drawable.note_selected));
                    receiveBtn.setBackground(getDrawable(R.drawable.note_normal));
                    sendList.setVisibility(View.VISIBLE);
                    receiveList.setVisibility(View.GONE);
                    current=SEND;
                    break;
            }
        }
    };

    private void init() {
        receiveIDS=new ArrayList<>();
        sendIDS=new ArrayList<>();
        Intent intent=getIntent();
        userID=intent.getStringExtra("userID");
        firebaseNote.GetReceiveNote(userID, receiveList, receiveIDS);
        firebaseNote.GetSendNote(userID, sendList, sendIDS);
    }

    public void close(View v) {
        finish();
    }
}
