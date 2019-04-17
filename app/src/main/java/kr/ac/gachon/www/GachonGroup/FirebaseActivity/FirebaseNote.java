package kr.ac.gachon.www.GachonGroup.FirebaseActivity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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
                NoteListAdapter noteListAdapter=new NoteListAdapter();
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
                NoteListAdapter noteListAdapter=new NoteListAdapter();
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
