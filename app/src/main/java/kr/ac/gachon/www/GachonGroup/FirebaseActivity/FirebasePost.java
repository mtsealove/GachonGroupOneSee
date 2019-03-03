package kr.ac.gachon.www.GachonGroup.FirebaseActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
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
import kr.ac.gachon.www.GachonGroup.Board.AddPostActivity;
import kr.ac.gachon.www.GachonGroup.R;
import kr.ac.gachon.www.GachonGroup.etc.Alert;

public class FirebasePost extends AppCompatActivity {
    FirebaseDatabase database;
    final Context context;
    public FirebasePost(Context context){
        this.context=context;
        database=FirebaseDatabase.getInstance();
    }

    @Override
    protected void onCreate(Bundle si) {
        super.onCreate(si);
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

                    //시간 옵션
                    String time=snapshot.child("time").getValue(String.class);
                    Date date=new Date();
                    SimpleDateFormat dateFormat=new SimpleDateFormat("yy/MM/dd HH:mm");
                    String now=dateFormat.format(date);
                    if(now.substring(7).equals(time.substring(7))) {
                        int hour1=Integer.parseInt(time.substring(8, 9));
                        int hour2=Integer.parseInt(now.substring(8, 9));
                        int min1=Integer.parseInt(time.substring(11, 12));
                        int min2=Integer.parseInt(now.substring(11, 12));

                        int hour=hour2-hour1;
                        int min=min2-min1;
                        if(min2-min1<0) {
                            min+=60;
                            hour--;
                        }
                        time=hour+"시간 "+min+"분 전";
                    }

                    timeTV.setText(time);
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

                    //시간 옵션
                    String time=snapshot.child("time").getValue(String.class);
                    Date date=new Date();
                    SimpleDateFormat dateFormat=new SimpleDateFormat("yy/MM/dd HH:mm");
                    String now=dateFormat.format(date);
                    if(now.substring(7).equals(time.substring(7))) {
                        int hour1=Integer.parseInt(time.substring(8, 9));
                        int hour2=Integer.parseInt(now.substring(8, 9));
                        int min1=Integer.parseInt(time.substring(11, 12));
                        int min2=Integer.parseInt(now.substring(11, 12));

                        int hour=hour2-hour1;
                        int min=min2-min1;
                        if(min2-min1<0) {
                            min+=60;
                            hour--;
                        }
                        time=hour+"시간 "+min+"분 전";
                    }

                    timeTV.setText(time);
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
    public void Post(final String boardName, final String ID, final String title, final String content, final ArrayList<Uri> filePath, final boolean temp) {
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
                if(temp) reference.child(boardName).child(Integer.toString(count)).child("temp").setValue("true");
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
    public void Post(final String groupName, final String boardName,final String ID,final String title,final String content, final ArrayList<Uri> filePath, final boolean temp) {
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
                if(temp) reference.child("Groups").child(groupName).child(boardName).child(Integer.toString(count)).child("temp").setValue("true");
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
        ref.child(boardName).child(boardID).child("temp").setValue(null);
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
                    //경로 삭제
                    DatabaseReference removeRef=snapshot.getRef();
                    for(int i=0; i<RemoveFiles.size(); i++) {
                        if(filePath.equals(RemoveFiles.get(i))) removeRef.child("FilePath").setValue(null);
                    }
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
        ref.child("Groups").child(GroupName).child(boardName).child(boardID).child("temp").setValue(null);
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
                    //경로 삭제
                    DatabaseReference removeRef=snapshot.getRef();
                    for(int i=0; i<RemoveFiles.size(); i++) {
                        if(filePath.equals(RemoveFiles.get(i))) removeRef.child("FilePath").setValue(null);
                    }
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

    //일반 게시판 임시저장 체크 메서드
    public void CheckTempBoard(final String boardName, final String ID, final EditText titleET, final EditText contentET, final LinearLayout linearLayout) {
        DatabaseReference reference=database.getReference().child(boardName);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(final DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    String userID=snapshot.child("author").getValue(String.class);
                    String temp=snapshot.child("temp").getValue(String.class);
                    //ID가 일치하며 임시 글인 경우
                    if(userID.equals(ID)&&temp!=null) {
                        final Alert alert=new Alert(context);
                        alert.MsgDialogChoice("임시저장한 내용을\n불러오시겠습니까?", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String content=snapshot.child("content").getValue(String.class);
                                String title=snapshot.child("title").getValue(String.class);
                                String boardId=Integer.toString(snapshot.child("id").getValue(Integer.class));
                                titleET.setText(title);
                                contentET.setText(content);
                                ArrayList<String> photos=new ArrayList<>();
                                FirebaseImage firebaseImage=new FirebaseImage(context);
                                for(DataSnapshot photoSnap: snapshot.child("Photos").getChildren()) {
                                    photos.add(photoSnap.child("FilePath").getValue(String.class));
                                    firebaseImage.getTempBoardPhotos(linearLayout, photoSnap.child("FilePath").getValue(String.class));
                                }
                                Alert.dialog.cancel();
                                AddPostActivity.temp=true;
                                AddPostActivity.boardID=boardId;
                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //업데이트 시 원래 업로드 되었던 이미지 불러오기
    private void LoadImages(final ArrayList<Uri> filePath, final ArrayList<String> removeFile, final LinearLayout contentLayout) {
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin=30;
        layoutParams.rightMargin=30;
        layoutParams.topMargin=10;
        layoutParams.bottomMargin=30;
        FirebaseImage firebaseImage=new FirebaseImage(context);
        for(int i=0; i<filePath.size(); i++) {
            final ImageView imageView=new ImageView(context);
            try {
                imageView.setImageURI(filePath.get(i));
                imageView.setLayoutParams(layoutParams);
                contentLayout.addView(imageView);

                final int finalI = i;
                //이미지를 길게 누르면 삭제 다이얼로그 출력
                imageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        //진동 반응
                        //Vibrator vibrator=(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                        //vibrator.vibrate(5);
                        Alert alert = new Alert(context);
                        alert.MsgDialogChoice("이미지를 삭제하시겠습니까?", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //이미지 제거 및 uri제거
                                contentLayout.removeView(imageView);
                                //제거 변수에 추가
                                removeFile.add(filePath.get(finalI).toString());
                                Alert.dialog.cancel();
                            }
                        });
                        return false;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //일반게시판 임시저장
    public void TempPost(final String ID, final String title, final String content, final String boardName, final ArrayList<Uri> filePath) {
        DatabaseReference reference=database.getReference().child("Account").child(ID).child("Temp").push();
        reference.child("board").setValue(boardName);
        reference.child("content").setValue(content);
        reference.child("title").setValue(title);
        FirebaseImage firebaseImage=new FirebaseImage(context);
        for(int i=0; i<filePath.size(); i++) {
            firebaseImage.UploadTempBoardImage(filePath.get(i), ID, i, reference);
        }

    }
}
