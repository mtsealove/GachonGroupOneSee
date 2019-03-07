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

public class FirebasePost extends AppCompatActivity {   //firebase를 이용한 게시글/댓글 삽입, 삭제, 수정
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

    //게시글에 해당하는 댓글 표시
    public void AddReply(final LinearLayout layout, final String boardName, final int BoardID, final String userID) {
        final DatabaseReference reference=database.getReference();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                LayoutInflater inflater=LayoutInflater.from(context);
                layout.removeAllViews();    //모든 댓글 뷰 제거
                ArrayList<View> replys=new ArrayList<>();
                final ArrayList<String> userIDs=new ArrayList<>();
                final ArrayList<Integer> replyIDS=new ArrayList<>();
                for(DataSnapshot snapshot: dataSnapshot.child(boardName).child(Integer.toString(BoardID)).child("reply").getChildren()) {
                    View reply=inflater.inflate(R.layout.sub_reply, null);  //각 댓글을 표시할 레이아웃
                    TextView authorTV=reply.findViewById(R.id.authorTV);    //댓글 작성자 TextView
                    TextView timeTV=reply.findViewById(R.id.timeTV);        //댓글 작성 시간 Textview
                    TextView contentTV=reply.findViewById(R.id.contentTV);  //댓글 내용 표시 textview

                    String authorID=snapshot.child("author").getValue(String.class);    //작성자 ID
                    String authorName=dataSnapshot.child("Account").child(authorID).child("name").getValue(String.class);   //ID를 기반으로 찾은 작성자 이름
                    authorTV.setText(authorName);   //작성자 표시

                    //시간 옵션
                    String time=snapshot.child("time").getValue(String.class);  //작성 시간
                    Date date=new Date();   //현재 시간
                    SimpleDateFormat dateFormat=new SimpleDateFormat("yy/MM/dd HH:mm");
                    String now=dateFormat.format(date); //온라인 상의 데이터와 같으 형식으로 변경
                    if(now.substring(7).equals(time.substring(7))) {    //같은 날짜(오늘)이면
                        int hour1=Integer.parseInt(time.substring(8, 9));
                        int hour2=Integer.parseInt(now.substring(8, 9));
                        int min1=Integer.parseInt(time.substring(11, 12));
                        int min2=Integer.parseInt(now.substring(11, 12));

                        //시간 계산
                        int hour=hour2-hour1;
                        int min=min2-min1;
                        if(min2-min1<0) {
                            min+=60;
                            hour--;
                        }
                        time=hour+"시간 "+min+"분 전";
                    }
                    timeTV.setText(time);   //시간 표시
                    contentTV.setText(snapshot.child("content").getValue(String.class));    //내용 표시
                    replys.add(reply);  //표시되는 뷰 리스트에 추가
                    userIDs.add(authorID);  //표시되지 않는 작성자 ID리스트에 추가
                    replyIDS.add(snapshot.child("id").getValue(Integer.class)); //표시되지 않는 댓글 ID 리스트에 추가
                }
                for(int i=0; i<replys.size(); i++) {    //읽은 댓글의 개수만큼
                    final int finalI = i;
                    replys.get(i).setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {    //댓글을 길게 눌렀을 때
                            AlertDialog.Builder builder=new AlertDialog.Builder(context);
                            ArrayList<String> arrayList=new ArrayList<>();
                            arrayList.add("신고하기");  //모든 사용자에 대해 신고 기능 추가
                            if (userID.equals(userIDs.get(finalI))) arrayList.add("삭제하기");  //자신의 댓글이면 삭제 가능
                            ArrayAdapter adapter=new ArrayAdapter(context, R.layout.dropown_item_custom, arrayList);
                            ListView listView=new ListView(context);
                            listView.setAdapter(adapter);
                            builder.setView(listView);  //화면에 리스트 형식으로 삭제/신고 표시
                            final AlertDialog dialog=builder.create();
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {  //삭제/신고 클릭 시
                                    switch (position) {
                                        case 0: //신고
                                        dialog.cancel();
                                        //신고에 필요한 정보를 전송
                                        Intent intent = new Intent(context, AccuseActivity.class);
                                        intent.putExtra("userID", id);
                                        intent.putExtra("boardName", boardName);
                                        intent.putExtra("id", BoardID);
                                        intent.putExtra("replyID", Integer.toString(finalI));
                                        //신고 액티비티로 이동
                                        context.startActivity(intent);
                                        break;
                                        case 1: //삭제
                                            //DB에서 삭제
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
                    layout.addView(replys.get(i));  //화면에 모든 댓글 뷰 표시
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
                for(DataSnapshot snapshot:dataSnapshot.child(boardName).child(boardID).child("reply").getChildren()) {  //해당 게시글의 마지막 댓글로
                    count=snapshot.child("id").getValue(Integer.class)+1;
                }
                reference.child(boardName).child(boardID).child("reply").child(Integer.toString(count)).child("author").setValue(userID);   //작성자 ID추가
                reference.child(boardName).child(boardID).child("reply").child(Integer.toString(count)).child("content").setValue(Content); //내용 추가
                reference.child(boardName).child(boardID).child("reply").child(Integer.toString(count)).child("id").setValue(count);    //댓글 ID 설정

                Date date=new Date();   //현재 시간
                SimpleDateFormat dateFormat=new SimpleDateFormat("yy/MM/dd HH:mm"); //시간 포맷
                String dateStr=dateFormat.format(date);
                reference.child(boardName).child(boardID).child("reply").child(Integer.toString(count)).child("time").setValue(dateStr);    //시간 추가
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
    public void Post(final String boardName, final String ID, final String title, final String content, final ArrayList<Uri> filePath, final boolean temp) {
        final DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int count=0;
                for(DataSnapshot snapshot:dataSnapshot.child(boardName).getChildren()) { //해당 게시판의 마지막 글로
                    count=snapshot.child("id").getValue(Integer.class)+1;
                }
                reference.child(boardName).child(Integer.toString(count)).child("author").setValue(ID); //작성자 ID
                reference.child(boardName).child(Integer.toString(count)).child("content").setValue(content);   //내용
                reference.child(boardName).child(Integer.toString(count)).child("title").setValue(title);   //제목
                reference.child(boardName).child(Integer.toString(count)).child("id").setValue(count);  //글 ID
                if(temp) reference.child(boardName).child(Integer.toString(count)).child("temp").setValue("true");  //임시 저장 체크
                FirebaseImage firebaseImage=new FirebaseImage(context);
                for(int i=0; i<filePath.size(); i++) {
                    firebaseImage.UploadBoardImage(filePath.get(i), boardName, Integer.toString(count), i); //파일 업로드
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

    //게시글 수정
    public void Update(final String boardName, final String title, final String content, final String boardID, final ArrayList<String> RemoveFiles, final ArrayList<Uri> FilePath) {
        DatabaseReference ref=database.getReference();
        //화면에 표시될 제목 및 내용 설정
        ref.child(boardName).child(boardID).child("title").setValue(title);
        ref.child(boardName).child(boardID).child("content").setValue(content);
        ref.child(boardName).child(boardID).child("temp").setValue(null);

        //삭제할 이미지 파일 삭제
        FirebaseStorage storage=FirebaseStorage.getInstance();
        for(int i=0; i<RemoveFiles.size(); i++) {
            StorageReference reference=storage.getReference().child(RemoveFiles.get(i));
            reference.delete();
        }
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count=0;
                for(DataSnapshot snapshot:dataSnapshot.child(boardName).child(boardID).child("Photos").getChildren()) { //DB에 저장된 사진 파일의 경로 삭제
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
                    firebaseImage.UploadBoardImage(FilePath.get(i), boardName, boardID, count++);   //새로 설정한 사진 파일 업로드
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

    //일반 게시판 게시글 삭제
    public void Remove(final String boardName, String boardID) {
        DatabaseReference reference=database.getReference();
        final DatabaseReference ref=reference.child(boardName).child(boardID);
        final FirebaseStorage storage=FirebaseStorage.getInstance();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.child("Photos").getChildren()) {
                    final String FilePath=snapshot.child("FilePath").getValue(String.class);    //사진 파일의 경로
                    StorageReference storageReference=storage.getReference().child(FilePath);
                    storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) { //사진 삭제
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
                ref.setValue(null); //나머지 데이터 삭제
                Toast.makeText(context, "게시글이 삭제되었습니다", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //동아리 게시판 게시글 삭제
    public void Remove(final String groupName, final String boardName, String boardID) {
        DatabaseReference reference=database.getReference();
        final DatabaseReference ref=reference.child("Groups").child(groupName).child(boardName).child(boardID);
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
                ref.setValue(null);
                Toast.makeText(context, "게시글이 삭제되었습니다", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void SetIntroduce(String group, String location, String introduce) { //동아리 소개글 업로드
        DatabaseReference reference=database.getReference();
        reference.child("Groups").child(group).child("location").setValue(location);
        reference.child("Groups").child(group).child("introduce").setValue(introduce);
    }

    //일반 게시판 임시저장 체크
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
                                String content=snapshot.child("content").getValue(String.class);    //내용
                                String title=snapshot.child("title").getValue(String.class);    //제목
                                String boardId=Integer.toString(snapshot.child("id").getValue(Integer.class));  //게시글 번호
                                titleET.setText(title); //제목 표시
                                contentET.setText(content); //내용 표시
                                ArrayList<String> photos=new ArrayList<>(); //사진
                                FirebaseImage firebaseImage=new FirebaseImage(context);
                                for(DataSnapshot photoSnap: snapshot.child("Photos").getChildren()) {
                                    photos.add(photoSnap.child("FilePath").getValue(String.class)); //사진 경로 받아오기
                                    firebaseImage.getTempBoardPhotos(linearLayout, photoSnap.child("FilePath").getValue(String.class)); //경로에 포함된 사핀 표시
                                }
                                Alert.dialog.cancel();
                                AddPostActivity.temp=true;  //임시저장됨을 알림
                                AddPostActivity.boardID=boardId;    //게시글 ID 설정
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

    //동아리 게시판 임시저장 체크
    public void CheckTempBoard(final String GroupName, final String boardName, final String ID, final EditText titleET, final EditText contentET, final LinearLayout linearLayout) {
        DatabaseReference reference=database.getReference().child(GroupName).child(boardName);
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
                                String content=snapshot.child("content").getValue(String.class);    //내용
                                String title=snapshot.child("title").getValue(String.class);    //제목
                                String boardId=Integer.toString(snapshot.child("id").getValue(Integer.class));  //게시글 번호
                                titleET.setText(title); //제목 표시
                                contentET.setText(content); //내용 표시
                                ArrayList<String> photos=new ArrayList<>(); //사진
                                FirebaseImage firebaseImage=new FirebaseImage(context);
                                for(DataSnapshot photoSnap: snapshot.child("Photos").getChildren()) {
                                    photos.add(photoSnap.child("FilePath").getValue(String.class)); //사진 경로 받아오기
                                    firebaseImage.getTempBoardPhotos(linearLayout, photoSnap.child("FilePath").getValue(String.class)); //경로에 포함된 사핀 표시
                                }
                                Alert.dialog.cancel();
                                AddPostActivity.temp=true;  //임시저장됨을 알림
                                AddPostActivity.boardID=boardId;    //게시글 ID 설정
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
}
