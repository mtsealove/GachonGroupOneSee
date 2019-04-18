package kr.ac.gachon.www.GachonGroup.FirebaseActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import kr.ac.gachon.www.GachonGroup.Note.NoteDetailActivity;
import kr.ac.gachon.www.GachonGroup.Entity.NoteItem;
import kr.ac.gachon.www.GachonGroup.Entity.NoteListAdapter;

public class FirebaseNote extends AppCompatActivity {   //쪽지 읽기 클래스
    final Context context;
    FirebaseDatabase database;
    public FirebaseNote(Context context) {
        this.context=context;
        database=FirebaseDatabase.getInstance();
    }

    public void GetReceiveNote(final String userID, final ListView listView, final ArrayList<Integer> ids) {  //받은 쪽지 읽어오기
        DatabaseReference reference=database.getReference().child("Note");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final NoteListAdapter noteListAdapter=new NoteListAdapter();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    String content=snapshot.child("Content").getValue(String.class);
                    String date=snapshot.child("Date").getValue(String.class);
                    String receiver=snapshot.child("Receiver").getValue(String.class);
                    String sender=snapshot.child("Sender").getValue(String.class);
                    int id=snapshot.child("ID").getValue(Integer.class);
                    boolean read=snapshot.child("Read").getValue(Boolean.class);

                    if(receiver.equals(userID)) {
                        noteListAdapter.addItem(sender, receiver, content, date, read);
                        ids.add(id);    //삭제를 위한 ID추가
                    }
                }
                listView.setAdapter(noteListAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        setRead(ids.get(position)); //읽음으로 설정
                        //다음 페이지에 보낼 데이터
                        NoteItem noteItem= (NoteItem) noteListAdapter.getItem(position);
                        Intent intent=new Intent(context, NoteDetailActivity.class);
                        intent.putExtra("Content", noteItem.getContent());
                        intent.putExtra("Date", noteItem.getDate());
                        intent.putExtra("Receiver", noteItem.getReceiver());
                        intent.putExtra("Sender", noteItem.getSender());
                        intent.putExtra("Cat", "Receive");
                        context.startActivity(intent);  //액티비티 시작
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void GetSendNote(final String userID, final ListView listView, final ArrayList<Integer> ids) {  //보낸 쪽지 읽어오기
        DatabaseReference reference=database.getReference().child("Note");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final NoteListAdapter noteListAdapter=new NoteListAdapter();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    String content=snapshot.child("Content").getValue(String.class);
                    String date=snapshot.child("Date").getValue(String.class);
                    String receiver=snapshot.child("Receiver").getValue(String.class);
                    String sender=snapshot.child("Sender").getValue(String.class);
                    int id=snapshot.child("ID").getValue(Integer.class);
                    boolean read=snapshot.child("Read").getValue(Boolean.class);

                    if(sender.equals(userID)) {
                        noteListAdapter.addItem(sender, receiver, content, date, read);
                        ids.add(id);    //삭제를 위한 ID추가
                    }
                }
                listView.setAdapter(noteListAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //다음 페이지에 보낼 데이터
                        NoteItem noteItem= (NoteItem) noteListAdapter.getItem(position);
                        Intent intent=new Intent(context, NoteDetailActivity.class);
                        intent.putExtra("Content", noteItem.getContent());
                        intent.putExtra("Date", noteItem.getDate());
                        intent.putExtra("Receiver", noteItem.getReceiver());
                        intent.putExtra("Sender", noteItem.getSender());
                        intent.putExtra("Cat", "Send");
                        context.startActivity(intent);  //액티비티 시작
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setRead(int id) {
        DatabaseReference reference=database.getReference().child("Note").child(Integer.toString(id)).child("Read");
        reference.setValue(true);
    }

    public void SendNote(final String Sender, final String Content, final String[] Receivers) {   //메세지 보내기
        final DatabaseReference AccountRef=database.getReference().child("Account");
        final DatabaseReference reference=database.getReference().child("Note");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final int[] count = {0};
                for(DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    try {
                        count[0] =snapshot.child("ID").getValue(Integer.class)+1;
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                for(int i=0; i<Receivers.length; i++) { //상대의 수만큼
                    final int finalI = i;
                    final int finalCount = count[0];
                    AccountRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.child(Receivers[finalI]).exists()) {    //존재하면
                                Date date=new Date();
                                SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                String dateStr=dateFormat.format(date);
                                DatabaseReference ref=reference.child(Integer.toString(finalCount));
                                ref.child("ID").setValue(finalCount);
                                ref.child("Content").setValue(Content);
                                ref.child("Read").setValue(false);
                                ref.child("Sender").setValue(Sender);
                                ref.child("Receiver").setValue(Receivers[finalI]);
                                ref.child("Date").setValue(dateStr);
                                count[0]++;
                            }
                            Toast.makeText(context, "쪽지가 발송되었습니다", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                ((Activity)context).finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void DeleteReceiveNote(String userID, int NoteID) {   //받은 쪽지 삭제
        DatabaseReference reference=database.getReference().child("Account").child(userID).child("Note").child("Receive").child(Integer.toString(NoteID));
        reference.setValue(null);   //삭제
    }


}
