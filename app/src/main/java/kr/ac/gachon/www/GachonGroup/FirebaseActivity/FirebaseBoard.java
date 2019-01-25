package kr.ac.gachon.www.GachonGroup.FirebaseActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import kr.ac.gachon.www.GachonGroup.PRBoardActivity;

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
                String count=Integer.toString((int)(dataSnapshot.child("Accuse").getChildrenCount()));
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
                String count=Integer.toString((int)(dataSnapshot.child("Accuse").getChildrenCount()));
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
}
