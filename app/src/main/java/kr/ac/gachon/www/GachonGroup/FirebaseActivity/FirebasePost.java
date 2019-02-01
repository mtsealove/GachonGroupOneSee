package kr.ac.gachon.www.GachonGroup.FirebaseActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import kr.ac.gachon.www.GachonGroup.Board.AccuseActivity;
import kr.ac.gachon.www.GachonGroup.R;

public class FirebasePost extends AppCompatActivity {
    FirebaseDatabase database;
    final Context context;
    public FirebasePost(Context context){
        this.context=context;
        database=FirebaseDatabase.getInstance();
    }

    //화면에 댓글 표시
    public void AddReply(final LinearLayout layout, final String boardName, final int BoardID, final String userID) {
        final DatabaseReference reference=database.getReference();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                LayoutInflater inflater=LayoutInflater.from(context);
                layout.removeAllViews();
                ArrayList<View> replys=new ArrayList<>();
                final ArrayList<String> userIDs=new ArrayList<>();
                final ArrayList<Integer> replyIDS=new ArrayList<>();
                for(DataSnapshot snapshot: dataSnapshot.child(boardName).child(Integer.toString(BoardID)).child("reply").getChildren()) {
                    View reply=inflater.inflate(R.layout.sub_reply, null);
                    TextView authorTV=reply.findViewById(R.id.authorTV);
                    TextView timeTV=reply.findViewById(R.id.timeTV);
                    TextView contentTV=reply.findViewById(R.id.contentTV);

                    String authorID=snapshot.child("author").getValue(String.class);
                    String authorName=dataSnapshot.child("Account").child(authorID).child("name").getValue(String.class);
                    authorTV.setText(authorName);
                    timeTV.setText(snapshot.child("time").getValue(String.class));
                    contentTV.setText(snapshot.child("content").getValue(String.class));
                    replys.add(reply);
                    userIDs.add(authorID);
                    replyIDS.add(snapshot.child("id").getValue(Integer.class));
                }
                for(int i=0; i<replys.size(); i++) {
                    final int finalI = i;
                    replys.get(i).setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            AlertDialog.Builder builder=new AlertDialog.Builder(context);
                            ArrayList<String> arrayList=new ArrayList<>();
                            arrayList.add("신고하기");
                            if (userID.equals(userIDs.get(finalI))) arrayList.add("삭제하기");
                            ArrayAdapter adapter=new ArrayAdapter(context, R.layout.dropown_item_custom, arrayList);
                            ListView listView=new ListView(context);
                            listView.setAdapter(adapter);
                            builder.setView(listView);
                            final AlertDialog dialog=builder.create();
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    switch (position) {
                                        case 0:
                                        dialog.cancel();
                                        Intent intent = new Intent(context, AccuseActivity.class);
                                        intent.putExtra("userID", id);
                                        intent.putExtra("boardName", boardName);
                                        intent.putExtra("id", BoardID);
                                        intent.putExtra("replyID", Integer.toString(finalI));
                                        context.startActivity(intent);
                                        break;
                                        case 1:
                                            reference.child(boardName).child(Integer.toString(BoardID)).child("reply").child(Integer.toString(replyIDS.get(finalI))).setValue(null);
                                            Toast.makeText(context, "댓글이 삭제되었습니다", Toast.LENGTH_SHORT).show();
                                            dialog.cancel();
                                            break;
                                    }
                                }
                            });
                            dialog.show();
                            return false;
                        }
                    });
                    layout.addView(replys.get(i));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //동아리 게시판 화면에 댓글 표시
    //동아리 이름, 댓글이 추가될 LinearLayout, 게시판 이름, 게시글 ID, Context
    public void AddReply(final String groupName, final LinearLayout layout,final String boardName, final int BoardID, final String userID) {
        final DatabaseReference reference=database.getReference();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final LayoutInflater inflater=LayoutInflater.from(context);
                //데이터가 업데이트 되면 모든 뷰를 추가하기 때문에 모든 뷰 삭제
                layout.removeAllViews();
                ArrayList<View> replys=new ArrayList<>();
                final ArrayList<String> userIDs=new ArrayList<>();
                final ArrayList<Integer> replyIDs=new ArrayList<>();
                for(DataSnapshot snapshot: dataSnapshot.child("Groups").child(groupName).child(boardName).child(Integer.toString(BoardID)).child("reply").getChildren()) {
                    View reply=inflater.inflate(R.layout.sub_reply, null);
                    TextView authorTV=reply.findViewById(R.id.authorTV);
                    TextView timeTV=reply.findViewById(R.id.timeTV);
                    TextView contentTV=reply.findViewById(R.id.contentTV);
                    //댓글을 작성한 유저의 ID를 기반으로 유저의 이름 파악
                    String authorID=snapshot.child("author").getValue(String.class);
                    String authorName=dataSnapshot.child("Account").child(authorID).child("name").getValue(String.class);
                    //작성자, 작성 시간, 댓글 내용 출력
                    authorTV.setText(authorName);
                    timeTV.setText(snapshot.child("time").getValue(String.class));
                    contentTV.setText(snapshot.child("content").getValue(String.class));

                    replys.add(reply);
                    userIDs.add(authorID);
                    replyIDs.add(snapshot.child("id").getValue(Integer.class));
                }
                for(int i=0; i<replys.size(); i++) {
                    final int finalI = i;
                    replys.get(i).setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            AlertDialog.Builder builder=new AlertDialog.Builder(context);
                            ArrayList<String> arrayList=new ArrayList<>();
                            arrayList.add("신고하기");
                            if (userID.equals(userIDs.get(finalI))) arrayList.add("삭제하기");

                            ArrayAdapter adapter=new ArrayAdapter(context, R.layout.dropown_item_custom, arrayList);
                            ListView listView=new ListView(context);
                            listView.setAdapter(adapter);
                            builder.setView(listView);
                            final AlertDialog dialog=builder.create();
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    switch (position) {
                                        case 0:
                                        dialog.cancel();
                                        Intent intent = new Intent(context, AccuseActivity.class);
                                        intent.putExtra("userID", id);
                                        intent.putExtra("boardName", boardName);
                                        intent.putExtra("id", BoardID);
                                        intent.putExtra("groupName", groupName);
                                        intent.putExtra("replyID", Integer.toString(replyIDs.get(finalI)));
                                        context.startActivity(intent);
                                        break;
                                        case 1:
                                            reference.child("Groups").child(groupName).child(boardName).child(Integer.toString(BoardID)).child("reply").child(Integer.toString(replyIDs.get(finalI))).setValue(null);
                                            Toast.makeText(context, "댓글이 삭제되었습니다", Toast.LENGTH_SHORT).show();
                                            dialog.cancel();
                                            break;
                                    }
                                }
                            });
                            dialog.show();
                            return false;
                        }
                    });
                    layout.addView(replys.get(i));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //일반 게시판 댓글 작성
    //게시판 이름, 게시글 ID, 유저 ID, 내용
    public void CommitReply(final String boardName, final String boardID, final String userID, final String Content) {
        final DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //마지막 글로
                int count=0;
                for(DataSnapshot snapshot:dataSnapshot.child(boardName).child(boardID).child("reply").getChildren()) {
                    count=snapshot.child("id").getValue(Integer.class)+1;
                }
                reference.child(boardName).child(boardID).child("reply").child(Integer.toString(count)).child("author").setValue(userID);
                reference.child(boardName).child(boardID).child("reply").child(Integer.toString(count)).child("content").setValue(Content);
                reference.child(boardName).child(boardID).child("reply").child(Integer.toString(count)).child("id").setValue(count);
                //현재 시간을 불러와 간단한 형식으로 추가
                Date date=new Date();
                SimpleDateFormat dateFormat=new SimpleDateFormat("yy/MM/dd HH:mm");
                String dateStr=dateFormat.format(date);
                reference.child(boardName).child(boardID).child("reply").child(Integer.toString(count)).child("time").setValue(dateStr);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //동아리 게시판에 댓글 작성
    //동아리 이름, 게시판 이름, 게시글 ID, 유저 ID, 내용
    //동아리 회원 이외 접근 불가
    public void CommitReply(final String groupName, final String boardName, final String boardID, final String userID, final String Content) {
        final DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //해당 게시글의 마지막 댓글로
                int count=0;
                for(DataSnapshot snapshot: dataSnapshot.child("Groups").child(groupName).child(boardName).child(boardID).child("reply").getChildren()) {
                    count=snapshot.child("id").getValue(Integer.class)+1;
                }
                reference.child("Groups").child(groupName).child(boardName).child(boardID).child("reply").child(Integer.toString(count)).child("author").setValue(userID);
                reference.child("Groups").child(groupName).child(boardName).child(boardID).child("reply").child(Integer.toString(count)).child("content").setValue(Content);
                reference.child("Groups").child(groupName).child(boardName).child(boardID).child("reply").child(Integer.toString(count)).child("id").setValue(count);
                //현재 시간을 불러와 간단한 형식으로 데이터 추가
                Date date=new Date();
                SimpleDateFormat dateFormat=new SimpleDateFormat("yy/MM/dd HH:mm");
                String dateStr=dateFormat.format(date);
                reference.child("Groups").child(groupName).child(boardName).child(boardID).child("reply").child(Integer.toString(count)).child("time").setValue(dateStr);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //전체 게시판(연합회 공지사항, QnA)에 게시글 작성
    //게시판 이름, 유저 ID, 제목, 내용
    //관리자 이외에는 접근 불가하게 타 크래스에서 설정
    public void Post(final String boardName, final String ID, final String title, final String content, final ArrayList<Uri> filePath) {
        final DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //해당 게시판의 마지막 글로
                int count=0;
                for(DataSnapshot snapshot:dataSnapshot.child(boardName).getChildren()) {
                    count=snapshot.child("id").getValue(Integer.class)+1;
                }
                reference.child(boardName).child(Integer.toString(count)).child("author").setValue(ID);
                reference.child(boardName).child(Integer.toString(count)).child("content").setValue(content);
                reference.child(boardName).child(Integer.toString(count)).child("title").setValue(title);
                reference.child(boardName).child(Integer.toString(count)).child("id").setValue(count);
                FirebaseImage firebaseImage=new FirebaseImage(context);
                for(int i=0; i<filePath.size(); i++) {
                    firebaseImage.UploadBoardImage(filePath.get(i), boardName, Integer.toString(count), i);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //동아리의 게시판에 게시글 작성
    //동아리 이름, 게시판 제목, 유저ID, 제목, 내용
    public void Post(final String groupName, final String boardName,final String ID,final String title,final String content, final ArrayList<Uri> filePath) {
        final DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //해당 게시판의 마지막 글로
                int count=0;
                for (DataSnapshot snapshot:dataSnapshot.child("Groups").child(groupName).child(boardName).getChildren()) {
                    count=snapshot.child("id").getValue(Integer.class)+1;
                }
                //내용 삽입
                reference.child("Groups").child(groupName).child(boardName).child(Integer.toString(count)).child("author").setValue(ID);
                reference.child("Groups").child(groupName).child(boardName).child(Integer.toString(count)).child("content").setValue(content);
                reference.child("Groups").child(groupName).child(boardName).child(Integer.toString(count)).child("title").setValue(title);
                reference.child("Groups").child(groupName).child(boardName).child(Integer.toString(count)).child("id").setValue(count);
                //받아온 이미지 개수만큼 이미지 업로드
                FirebaseImage firebaseImage=new FirebaseImage(context);
                for(int i=0; i<filePath.size(); i++)
                    firebaseImage.UploadBoardImage(filePath.get(i), groupName, boardName, Integer.toString(count), i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void Update(final String boardName, final String title, final String content, final String boardID, final ArrayList<String> RemoveFiles, final ArrayList<Uri> FilePath) {
        DatabaseReference ref=database.getReference();
        //제목 및 내용 설정
        ref.child(boardName).child(boardID).child("title").setValue(title);
        ref.child(boardName).child(boardID).child("content").setValue(content);
        //삭제 파일 삭제
        FirebaseStorage storage=FirebaseStorage.getInstance();
        for(int i=0; i<RemoveFiles.size(); i++) {
            StorageReference reference=storage.getReference().child(RemoveFiles.get(i));
            reference.delete();
        }
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count=0;
                for(DataSnapshot snapshot:dataSnapshot.child(boardName).child(boardID).child("Photos").getChildren()) {
                    String filePath=snapshot.child("FilePath").getValue(String.class);
                    count=filePath.charAt(filePath.length()-5)-'0'+1;
                }
                FirebaseImage firebaseImage=new FirebaseImage(context);
                for(int i=0; i<FilePath.size(); i++)
                    firebaseImage.UploadBoardImage(FilePath.get(i), boardName, boardID, count++);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Toast.makeText(context, "게시글이 수정되었습니다", Toast.LENGTH_SHORT).show();
    }

    public void Update(final String GroupName, final String boardName, final String title, final String content, final String boardID, final ArrayList<String> RemoveFiles, final ArrayList<Uri> FilePath) {
        DatabaseReference ref=database.getReference();
        //제목 및 내용 변경
        ref.child("Groups").child(GroupName).child(boardName).child(boardID).child("title").setValue(title);
        ref.child("Groups").child(GroupName).child(boardName).child(boardID).child("content").setValue(content);
        //삭제 파일 삭제
        FirebaseStorage storage=FirebaseStorage.getInstance();
        for(int i=0; i<RemoveFiles.size(); i++) {
            StorageReference reference=storage.getReference().child(RemoveFiles.get(i));
            reference.delete();
        }
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count=0;
                for(DataSnapshot snapshot:dataSnapshot.child("Groups").child(GroupName).child(boardName).child(boardID).child("Photos").getChildren()) {
                    String filePath=snapshot.child("FilePath").getValue(String.class);
                    count=filePath.charAt(filePath.length()-5)-'0'+1;
                }
                FirebaseImage firebaseImage=new FirebaseImage(context);
                for(int i=0; i<FilePath.size(); i++) {
                    firebaseImage.UploadBoardImage(FilePath.get(i), GroupName, boardName, boardID, count++);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Toast.makeText(context, "게시글이 수정되었습니다", Toast.LENGTH_SHORT).show();
    }

    public void Remove(final String boardName, String boardID) {
        DatabaseReference reference=database.getReference();
        DatabaseReference ref=reference.child(boardName).child(boardID);
        final FirebaseStorage storage=FirebaseStorage.getInstance();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.child("Photos").getChildren()) {
                    final String FilePath=snapshot.child("FilePath").getValue(String.class);
                    StorageReference storageReference=storage.getReference().child(FilePath);
                    storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("사진 삭제 성공", FilePath);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "사진 삭제 실패", Toast.LENGTH_SHORT).show();
                                return;
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ref.setValue(null);
        Toast.makeText(context, "게시글이 삭제되었습니다", Toast.LENGTH_SHORT).show();
    }
    public void Remove(final String groupName, final String boardName, String boardID) {
        DatabaseReference reference=database.getReference();
        DatabaseReference ref=reference.child("Groups").child(groupName).child(boardName).child(boardID);
        final FirebaseStorage storage=FirebaseStorage.getInstance();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.child("Photos").getChildren()){
                    final String FilePath=snapshot.child("FilePath").getValue(String.class);
                    StorageReference storageReference=storage.getReference().child(FilePath);
                    storageReference.delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("사진 삭제 성공", FilePath);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, "사진 삭제 실패", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ref.setValue(null);
        Toast.makeText(context, "게시글이 삭제되었습니다", Toast.LENGTH_SHORT).show();
    }

    public void SetIntroduce(String group, String location, String introduce) {
        DatabaseReference reference=database.getReference();
        reference.child("Groups").child(group).child("location").setValue(location);
        reference.child("Groups").child(group).child("introduce").setValue(introduce);
    }
}
