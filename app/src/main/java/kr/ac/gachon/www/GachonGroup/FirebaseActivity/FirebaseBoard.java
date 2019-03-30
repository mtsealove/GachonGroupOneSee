package kr.ac.gachon.www.GachonGroup.FirebaseActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import kr.ac.gachon.www.GachonGroup.Board.PostActivity;
import kr.ac.gachon.www.GachonGroup.etc.Alert;

public class FirebaseBoard extends AppCompatActivity {  //firebase를 이용한 게시글 확인 메서드 및 신고
    final Context context;
    FirebaseDatabase database;
    public FirebaseBoard(Context context) {
        this.context=context;
        database=FirebaseDatabase.getInstance();
    }

    //게시판 내용 설정
    public void setTextViewBoard(final TextView author, final TextView title, final TextView content, final String boardName , final int id, final TextView timeTV) {  //작성자 TV, 제목 TV, 내용 TV, 게시판 이름, 게시글 번호
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {  //데이터 읽어오기
                String authorID=dataSnapshot.child(boardName).child(Integer.toString(id)).child("author").getValue(String.class);
                //작성자 ID를 기반으로 작성자의 이름 출력
                String authorStr=dataSnapshot.child("Account").child(authorID).child("name").getValue(String.class);
                String titleStr=dataSnapshot.child(boardName).child(Integer.toString(id)).child("title").getValue(String.class);
                String contentStr=dataSnapshot.child(boardName).child(Integer.toString(id)).child("content").getValue(String.class);
                String time=dataSnapshot.child(boardName).child(Integer.toString(id)).child("time").getValue(String.class);
                //화면에 표시
                author.setText("작성자: "+authorStr);
                title.setText("제목: "+titleStr);
                content.setText(contentStr);
                timeTV.setText(time);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //동아리 게시판 보드
    public void setTextViewBoard(final String GroupName, final TextView author, final TextView title, final TextView content, final String boardName , final int id, final TextView timeTV) {  //동아리 이름, 작성자 TV, 제목 TV, 내용 TV, 게시판 이름, 게시글 번호
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {  //데이터 읽어오기
                String authorID=dataSnapshot.child("Groups").child(GroupName).child(boardName).child(Integer.toString(id)).child("author").getValue(String.class);
                //작성자 ID를 기반으로 작성자의 이름 출력
                String authorStr=dataSnapshot.child("Account").child(authorID).child("name").getValue(String.class);
                String titleStr=dataSnapshot.child("Groups").child(GroupName).child(boardName).child(Integer.toString(id)).child("title").getValue(String.class);
                String contentStr=dataSnapshot.child("Groups").child(GroupName).child(boardName).child(Integer.toString(id)).child("content").getValue(String.class);
                String time=dataSnapshot.child("Groups").child(GroupName).child(boardName).child(Integer.toString(id)).child("time").getValue(String.class);
                //화면에 표시
                author.setText("작성자: "+authorStr);
                title.setText("제목: "+titleStr);
                content.setText(contentStr);
                timeTV.setText(time);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //일반 게시판 게시글 신고
    public void Accuse(final String boardName, final String boardID, final String userID, final String reason) {    //게시판 이름, 게시글 ID, 신고한 유저의 ID, 신고 사유
        final DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Accuse에 새로운 데이터 추가
                int countInt=0;
                for(DataSnapshot snapshot: dataSnapshot.child("Accuse").getChildren()) {    //마지막 신고의 번호 확인
                    countInt=snapshot.child("id").getValue(Integer.class)+1;
                }
                String count=Integer.toString(countInt);
                //데이터 삽입
                reference.child("Accuse").child(count).child("id").setValue(countInt);
                reference.child("Accuse").child(count).child("BoardName").setValue(boardName);
                reference.child("Accuse").child(count).child("BoardID").setValue(boardID);
                reference.child("Accuse").child(count).child("UserID").setValue(userID);
                reference.child("Accuse").child(count).child("Reason").setValue(reason);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //동아리 게시판 게시글 신고
    public void Accuse(final String groupName, final String boardName, final String boardID, final String userID, final String reason) {    //동아리 이름, 게시판 이름, 게시글 ID, 신고한 유저의 ID, 신고 사유
        final DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Accuse에 새로운 데이터 추가
                int countInt=0;
                for(DataSnapshot snapshot: dataSnapshot.child("Accuse").getChildren()) {    //마지막 신고 번호 확인
                    countInt=snapshot.child("id").getValue(Integer.class)+1;
                }
                String count=Integer.toString(countInt);
                //데이터 삽입
                reference.child("Accuse").child(count).child("id").setValue(countInt);
                reference.child("Accuse").child(count).child("BoardName").setValue(boardName);
                reference.child("Accuse").child(count).child("BoardID").setValue(boardID);
                reference.child("Accuse").child(count).child("UserID").setValue(userID);
                reference.child("Accuse").child(count).child("Reason").setValue(reason);
                reference.child("Accuse").child(count).child("GroupName").setValue(groupName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //일반 게시판 댓글 신고
    public void AccuseReply(final String boardName, final String boardID, final String userID, final String reason, final String replyID) { // 게시판 이름, 게시글 ID, 신고한 유저의 ID, 신고 사유, 댓글 ID
        final DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Accuse에 새로운 데이터 추가
                int countInt=0;
                for(DataSnapshot snapshot:dataSnapshot.child("Accuse").getChildren()) { //마지막 번호 확인
                    countInt=snapshot.child("id").getValue(Integer.class)+1;
                }
                String count=Integer.toString(countInt);
                reference.child("Accuse").child(count).child("BoardName").setValue(boardName);
                reference.child("Accuse").child(count).child("BoardID").setValue(boardID);
                reference.child("Accuse").child(count).child("UserID").setValue(userID);
                reference.child("Accuse").child(count).child("Reason").setValue(reason);
                reference.child("Accuse").child(count).child("ReplyID").setValue(replyID);
                reference.child("Accuse").child(count).child("id").setValue(countInt);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //동아리 게시판 게시글 신고
    public void AccuseReply(final String groupName, final String boardName, final String boardID, final String userID, final String reason, final String ReplyID) { //동아리 이름, 게시판 이름, 게시글 ID, 신고한 유저의 ID, 신고 사유, 댓글 ID
        final DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Accuse에 새로운 데이터 추가
                int countInt=0;
                for(DataSnapshot snapshot:dataSnapshot.child("Accuse").getChildren()) { //마지막 번호 확인
                    countInt=snapshot.child("id").getValue(Integer.class)+1;
                }
                String count=Integer.toString(countInt);
                reference.child("Accuse").child(count).child("BoardName").setValue(boardName);
                reference.child("Accuse").child(count).child("BoardID").setValue(boardID);
                reference.child("Accuse").child(count).child("UserID").setValue(userID);
                reference.child("Accuse").child(count).child("Reason").setValue(reason);
                reference.child("Accuse").child(count).child("GroupName").setValue(groupName);
                reference.child("Accuse").child(count).child("ReplyID").setValue(ReplyID);
                reference.child("Accuse").child(count).child("id").setValue(countInt);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //자신의 글인지 확인(일반 게시판)
    public void MyContent(final String boardName, final String boardID, final String userID, final Button editBtn, final Button removeBtn){
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(boardName).child(boardID).child("author").getValue(String.class).equals(userID)) {    //자신의 글이라면
                    final ArrayList<String> FilePath=new ArrayList<>(); //사진의 경로
                    for(DataSnapshot snapshot: dataSnapshot.child(boardName).child(boardID).child("Photos").getChildren()) {
                        String path=snapshot.child("FilePath").getValue(String.class);
                        FilePath.add(path);
                    }
                    final String title=dataSnapshot.child(boardName).child(boardID).child("title").getValue(String.class);
                    final String content=dataSnapshot.child(boardName).child(boardID).child("content").getValue(String.class);
                    //수정 활성화
                    editBtn.setVisibility(View.VISIBLE);
                    editBtn.setText("수정");
                    editBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent(context, PostActivity.class);
                            //기본정보
                            intent.putExtra("boardName", boardName);
                            intent.putExtra("userID", userID);
                            //추가 정보//자신의 글이라면
                            intent.putExtra("title", title);
                            intent.putExtra("content", content);
                            intent.putExtra("boardID", boardID);
                            if(FilePath.size()!=0) intent.putExtra("FilePath", FilePath);
                            context.startActivity(intent);
                            //게시글 작성 액티비티로 이동
                        }
                    });
                    removeBtn.setVisibility(View.VISIBLE);  //삭제 활성
                    removeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Alert alert=new Alert(context);
                            alert.MsgDialogChoice("게시글을 삭제하시겠습니까?", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    FirebasePost firebasePost=new FirebasePost(context);
                                    firebasePost.Remove(boardName, boardID);    //게시글 삭제
                                    ((Activity)context).finish();   //현재 액티비티 종료
                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //자신의 글인지 확인(동아리 게시판)
    public void MyContent(final String groupName, final String boardName, final String boardID, final String userID, final Button editBtn, final Button removeBtn){
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Groups").child(groupName).child(boardName).child(boardID).child("author").getValue(String.class).equals(userID)) {   //자신의 글이라면
                    final ArrayList<String> FilePath=new ArrayList<>(); //사진 경로
                    for(DataSnapshot snapshot: dataSnapshot.child("Groups").child(groupName).child(boardName).child(boardID).child("Photos").getChildren()) {
                        String path=snapshot.child("FilePath").getValue(String.class);
                        FilePath.add(path);
                    }
                    final String title=dataSnapshot.child("Groups").child(groupName).child(boardName).child(boardID).child("title").getValue(String.class);
                    final String content=dataSnapshot.child("Groups").child(groupName).child(boardName).child(boardID).child("content").getValue(String.class);
                    //수정 활성화
                    editBtn.setVisibility(View.VISIBLE);
                    editBtn.setText("수정");
                    editBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent(context, PostActivity.class);
                            //기본정보
                            intent.putExtra("boardName", boardName);
                            intent.putExtra("userID", userID);
                            intent.putExtra("groupName", groupName);
                            //추가 정보
                            intent.putExtra("title", title);
                            intent.putExtra("content", content);
                            intent.putExtra("boardID", boardID);
                            if(FilePath.size()!=0) intent.putExtra("FilePath", FilePath);
                            context.startActivity(intent);
                            //게시글 작성 액티비티로 이동
                        }
                    });
                    //삭제 활성화
                    removeBtn.setVisibility(View.VISIBLE);
                    removeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Alert alert=new Alert(context);
                            alert.MsgDialogChoice("게시글을 삭제하시겠습니까?", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    FirebasePost firebasePost=new FirebasePost(context);
                                    firebasePost.Remove(groupName, boardName, boardID); //게시글 삭제
                                    ((Activity)context).finish();   //현재 액티비티 종료
                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
