package kr.ac.gachon.www.GachonGroup.Note;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import kr.ac.gachon.www.GachonGroup.Entity.NoteItem;
import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseNote;
import kr.ac.gachon.www.GachonGroup.R;
import kr.ac.gachon.www.GachonGroup.etc.Alert;

public class NoteListActivity extends AppCompatActivity {
    Button receiveBtn, sendBtn; //상단의 탭
    Button SendNoteBtn, RemoveBtn; //하단의 삭제, 보내기 버튼
    ListView receiveList, sendList;
    private String userID;
    final int RECEIVE=1, SEND=2;
    int current=RECEIVE;    //삭제를 위해 가지는 변수
    FirebaseNote firebaseNote;
    ArrayList<Integer> receiveIDS, sendIDS;
    Alert alert;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);

        receiveBtn=findViewById(R.id.receiveBtn);
        sendBtn=findViewById(R.id.sendBtn);
        receiveList=findViewById(R.id.receiveNoteList); //받은 리스트
        sendList=findViewById(R.id.sendNoteList);   //보낸 리스트
        SendNoteBtn=findViewById(R.id.sendNoteBtn);
        RemoveBtn=findViewById(R.id.removeBtn);
        RemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteNote();
            }
        });

        sendBtn.setOnClickListener(tabBtnListener);
        receiveBtn.setOnClickListener(tabBtnListener);
        SendNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendNote();
            }
        });

        firebaseNote=new FirebaseNote(NoteListActivity.this);

        init();
        alert=new Alert(this);
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

    private void SendNote() {   //쪽지 보내기 액티비티로 이동
        Intent intent=new Intent(NoteListActivity.this, SendNoteActivity.class);
        intent.putExtra("Sender", userID);
        startActivity(intent);
    }

    private void init() {
        receiveIDS=new ArrayList<>();
        sendIDS=new ArrayList<>();
        Intent intent=getIntent();
        userID=intent.getStringExtra("userID");
        firebaseNote.GetReceiveNote(userID, receiveList, receiveIDS);
        firebaseNote.GetSendNote(userID, sendList, sendIDS);
    }

    private void DeleteNote() { //쪽지 삭제
        ListView listView=new ListView(this);
        ArrayList<Integer> list=new ArrayList<>();
        switch (current) {  //현재
            case RECEIVE:   //받은
                listView=receiveList;
                list=receiveIDS;
                break;
            case SEND:  //보낸
                listView=sendList;
                list=sendIDS;
                break;

        }

        final ArrayList<Integer> removeList=new ArrayList<>();
        for(int i=0; i<listView.getAdapter().getCount(); i++) {
            if(((NoteItem)listView.getItemAtPosition(i)).isChecked()) {  //체크된 것들을 파악
                removeList.add(list.get(i));  //삭제할 ID추가
            }
        }
        if(removeList.size()==0) alert.MsgDialog("삭제할 ID를 선택해주세요");
        else {  //삭제할 모든 것들을 삭제
            alert.MsgDialogChoice("쪽지를 삭제하시겠습니까?", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for(int noteID: removeList)
                        firebaseNote.DeleteReceiveNote(noteID);
                    alert.MsgDialog("쪽지가 삭제되었습니다");
                    Alert.dialog.cancel();
                    init();
                }
            });
        }

    }
    @Override
    public void onResume() {
        super.onResume();
        init();
    }
    
    public void close(View v) {
        finish();
    }
}
