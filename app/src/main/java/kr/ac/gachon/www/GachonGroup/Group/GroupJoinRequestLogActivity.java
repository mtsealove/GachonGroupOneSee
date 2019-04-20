package kr.ac.gachon.www.GachonGroup.Group;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import kr.ac.gachon.www.GachonGroup.Board.PublicNoticeActivity;
import kr.ac.gachon.www.GachonGroup.Entity.RequestListViewItem;
import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseJoinRequest;
import kr.ac.gachon.www.GachonGroup.Note.SendNoteActivity;
import kr.ac.gachon.www.GachonGroup.R;
import kr.ac.gachon.www.GachonGroup.etc.Alert;

public class GroupJoinRequestLogActivity extends AppCompatActivity {    //특정 동아리에 가입신청된 기록을 조회하는 액티비티, 관리자만 접근 가능
    ListView boarLV;
    private String groupName, userID;
    Button noteBtn, removeBtn;
    ArrayList<Integer> requestIds;
    Alert alert;
    FirebaseJoinRequest firebaseJoinRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_join_request_log);
        boarLV=findViewById(R.id.boardLV);  //신청된 리스트를 표시할 버튼
        noteBtn=findViewById(R.id.noteBtn); //쪽지 보내기 버튼
        removeBtn=findViewById(R.id.removeBtn); //신청내역 삭제 버튼

        alert=new Alert(this);
        Intent intent=getIntent();
        groupName=intent.getStringExtra("groupName");
        userID=intent.getStringExtra("userID");

        init();

        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean exist=false;
                for(int i=0; i<boarLV.getAdapter().getCount(); i++) {
                    if(((RequestListViewItem)boarLV.getItemAtPosition(i)).isChecked()) {
                        exist=true;
                    }
                }
                if(!exist) Toast.makeText(GroupJoinRequestLogActivity.this, "신청 내역을\n선택해주세요", Toast.LENGTH_SHORT).show();
                else RemoveJoinRequest();
            }
        });

        noteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendNote();
            }
        });
    }

    private void init() {
        requestIds=new ArrayList<>();
        firebaseJoinRequest=new FirebaseJoinRequest(GroupJoinRequestLogActivity.this);
        firebaseJoinRequest.GroupJoinRequestLog(boarLV, groupName, requestIds); //동아리 이름으로 리스트 설정
    }

    private void RemoveJoinRequest() {  //신청내역 삭제
        alert.MsgDialogChoice("삭제하시겠습니까?", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Integer> removeIds=new ArrayList<>();
                for(int i=0; i<boarLV.getAdapter().getCount(); i++) {
                    if(((RequestListViewItem)boarLV.getItemAtPosition(i)).isChecked())  //체크가되면
                        removeIds.add(requestIds.get(i));   //해당 아이디를 삭제할 아이디에 추가
                }
                for(int removeID: removeIds)
                    firebaseJoinRequest.RemoveJoinRequest(removeID);
                Toast.makeText(GroupJoinRequestLogActivity.this, "신청 내역이 삭제되었습니다", Toast.LENGTH_SHORT).show();
                Alert.dialog.cancel();
                init(); //삭제 후 리로드
            }
        });
    }

    private void SendNote() {
        final ArrayList<String> ids=new ArrayList<>();
        for(int i=0; i<boarLV.getAdapter().getCount(); i++) {
            RequestListViewItem item= (RequestListViewItem) boarLV.getAdapter().getItem(i);
            if(item.isChecked()) {  //체크되어 있다면
                ids.add(item.getUserID());  //사용자의 ID추가
            }
        }
        if(ids.size()==0) alert.MsgDialog("신청 내역을\n선택해주세요");
        else {
            alert.MsgDialogChoice("쪽지를 보내시겠습니까?", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(GroupJoinRequestLogActivity.this, SendNoteActivity.class);
                    intent.putExtra("Sender", userID);
                    StringBuffer Receivers=new StringBuffer();
                    for(String id:ids){
                        Receivers.append(id);
                        Receivers.append(",");
                    }
                    Receivers.deleteCharAt(Receivers.length()-1);
                    intent.putExtra("Receiver", Receivers.toString());
                    startActivity(intent);
                    Alert.dialog.cancel();
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
