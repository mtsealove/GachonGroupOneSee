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

import kr.ac.gachon.www.GachonGroup.Board.AddPostActivity;
import kr.ac.gachon.www.GachonGroup.Board.PRBoardActivity;
import kr.ac.gachon.www.GachonGroup.etc.Alert;

public class FirebaseBoard extends AppCompatActivity {
    final Context context;
    FirebaseDatabase database;
    public FirebaseBoard(Context context) {
        this.context=context;
        database=FirebaseDatabase.getInstance();
    }

    //게시판 내용 설정
    public void setTextViewBoard(final TextView author, final TextView title, final TextView content, final String boardName , final int id) {
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String authorID=dataSnapshot.child(boardName).child(Integer.toString(id)).child("author").getValue(String.class);
                String authorStr=dataSnapshot.child("Account").child(authorID).child("name").getValue(String.class);
                String titleStr=dataSnapshot.child(boardName).child(Integer.toString(id)).child("title").getValue(String.class);
                String contentStr=dataSnapshot.child(boardName).child(Integer.toString(id)).child("content").getValue(String.class);
                author.setText("작성자: "+authorStr);
                title.setText("제목: "+titleStr);
                content.setText(contentStr);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //동아리 게시판 보드
    public void setTextViewBoard(final String GroupName, final TextView author, final TextView title, final TextView content, final String boardName , final int id) {
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String authorID=dataSnapshot.child("Groups").child(GroupName).child(boardName).child(Integer.toString(id)).child("author").getValue(String.class);
                String authorStr=dataSnapshot.child("Account").child(authorID).child("name").getValue(String.class);
                String titleStr=dataSnapshot.child("Groups").child(GroupName).child(boardName).child(Integer.toString(id)).child("title").getValue(String.class);
                String contentStr=dataSnapshot.child("Groups").child(GroupName).child(boardName).child(Integer.toString(id)).child("content").getValue(String.class);
                author.setText("작성자: "+authorStr);
                title.setText("제목: "+titleStr);
                content.setText(contentStr);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //게시글 신고, 게시판 이름, 게시글 ID, 신고한 유저의 ID, 신고 사유
    public void Accuse(final String boardName, final String boardID, final String userID, final String reason) {
        final DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Accuse에 새로운 데이터 추가
                int countInt=0;
                for(DataSnapshot snapshot: dataSnapshot.child("Accuse").getChildren()) {
                    countInt=snapshot.child("id").getValue(Integer.class)+1;
                }
                String count=Integer.toString(countInt);
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
    //게시글 신고, 게시판 이름, 게시글 ID, 신고한 유저의 ID, 신고 사유
    public void Accuse(final String groupName, final String boardName, final String boardID, final String userID, final String reason) {
        final DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Accuse에 새로운 데이터 추가
                int countInt=0;
                for(DataSnapshot snapshot: dataSnapshot.child("Accuse").getChildren()) {
                    countInt=snapshot.child("id").getValue(Integer.class)+1;
                }
                String count=Integer.toString(countInt);
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
    //게시글 신고, 게시판 이름, 게시글 ID, 신고한 유저의 ID, 신고 사유
    public void AccuseReply(final String boardName, final String boardID, final String userID, final String reason, final String replyID) {
        final DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Accuse에 새로운 데이터 추가
                String count=Integer.toString((int)(dataSnapshot.child("Accuse").getChildrenCount()));
                reference.child("Accuse").child(count).child("BoardName").setValue(boardName);
                reference.child("Accuse").child(count).child("BoardID").setValue(boardID);
                reference.child("Accuse").child(count).child("UserID").setValue(userID);
                reference.child("Accuse").child(count).child("Reason").setValue(reason);
                reference.child("Accuse").child(count).child("ReplyID").setValue(replyID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //게시글 신고, 게시판 이름, 게시글 ID, 신고한 유저의 ID, 신고 사유
    public void AccuseReply(final String groupName, final String boardName, final String boardID, final String userID, final String reason, final String ReplyID) {
        final DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Accuse에 새로운 데이터 추가
                String count=Integer.toString((int)(dataSnapshot.child("Accuse").getChildrenCount()));
                reference.child("Accuse").child(count).child("BoardName").setValue(boardName);
                reference.child("Accuse").child(count).child("BoardID").setValue(boardID);
                reference.child("Accuse").child(count).child("UserID").setValue(userID);
                reference.child("Accuse").child(count).child("Reason").setValue(reason);
                reference.child("Accuse").child(count).child("GroupName").setValue(groupName);
                reference.child("Accuse").child(count).child("ReplyID").setValue(ReplyID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void SearchPRBoard(final String input) {
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> titles=new ArrayList<>();
                ArrayList<Integer> ids=new ArrayList<>();
                for(DataSnapshot snapshot:dataSnapshot.child("PublicRelation").getChildren()) {
                    if(snapshot.child("title").getValue(String.class).contains(input)) {
                        titles.add(snapshot.child("title").getValue(String.class));
                        ids.add(snapshot.child("id").getValue(Integer.class));
                    }
                }
                PRBoardActivity activity=(PRBoardActivity)PRBoardActivity.PRBActivity;
                activity.finish();
                Intent intent=new Intent(context, PRBoardActivity.class);
                intent.putStringArrayListExtra("titles", titles);
                intent.putIntegerArrayListExtra("ids", ids);

                context.startActivity(intent);
                ((Activity)context).finish();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //자신의 글인지 확인하는 메서드
    public void MyContent(final String boardName, final String boardID, final String userID, final Button editBtn, final Button removeBtn){
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //자신의 글이라면
                if(dataSnapshot.child(boardName).child(boardID).child("author").getValue(String.class).equals(userID)) {
                    final ArrayList<String> FilePath=new ArrayList<>();
                    for(DataSnapshot snapshot: dataSnapshot.child(boardName).child(boardID).child("Photos").getChildren()) {
                        String path=snapshot.child("FilePath").getValue(String.class);
                        FilePath.add(path);
                    }
                    final String title=dataSnapshot.child(boardName).child(boardID).child("title").getValue(String.class);
                    final String content=dataSnapshot.child(boardName).child(boardID).child("content").getValue(String.class);
                    editBtn.setText("수정");
                    editBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent(context, AddPostActivity.class);
                            //기본정보
                            intent.putExtra("boardName", boardName);
                            intent.putExtra("userID", userID);
                            //추가 정보
                            intent.putExtra("title", title);
                            intent.putExtra("content", content);
                            intent.putExtra("boardID", boardID);
                            if(FilePath.size()!=0) intent.putExtra("FilePath", FilePath);
                            context.startActivity(intent);
                        }
                    });
                    removeBtn.setVisibility(View.VISIBLE);
                    removeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Alert alert=new Alert(context);
                            alert.MsgDialogChoice("게시글을 삭제하시겠습니까?", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    FirebasePost firebasePost=new FirebasePost(context);
                                    firebasePost.Remove(boardName, boardID);
                                    ((Activity)context).finish();
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

    //자신의 글인지 확인하는 메서드//그룹용
    public void MyContent(final String groupName, final String boardName, final String boardID, final String userID, final Button editBtn, final Button removeBtn){
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //자신의 글이라면
                if(dataSnapshot.child("Groups").child(groupName).child(boardName).child(boardID).child("author").getValue(String.class).equals(userID)) {
                    final ArrayList<String> FilePath=new ArrayList<>();
                    for(DataSnapshot snapshot: dataSnapshot.child("Groups").child(groupName).child(boardName).child(boardID).child("Photos").getChildren()) {
                        String path=snapshot.child("FilePath").getValue(String.class);
                        FilePath.add(path);
                    }
                    final String title=dataSnapshot.child("Groups").child(groupName).child(boardName).child(boardID).child("title").getValue(String.class);
                    final String content=dataSnapshot.child("Groups").child(groupName).child(boardName).child(boardID).child("content").getValue(String.class);

                    editBtn.setText("수정");
                    editBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent(context, AddPostActivity.class);
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
                        }
                    });

                    removeBtn.setVisibility(View.VISIBLE);
                    removeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Alert alert=new Alert(context);
                            alert.MsgDialogChoice("게시글을 삭제하시겠습니까?", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    FirebasePost firebasePost=new FirebasePost(context);
                                    firebasePost.Remove(groupName, boardName, boardID);
                                    ((Activity)context).finish();
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
