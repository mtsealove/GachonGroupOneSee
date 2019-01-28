package kr.ac.gachon.www.GachonGroup.FirebaseActivity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import kr.ac.gachon.www.GachonGroup.Board.BoardActivity;
import kr.ac.gachon.www.GachonGroup.R;

public class FirebaseList extends AppCompatActivity {
    final Context context;
    FirebaseDatabase database;
    public FirebaseList(Context context) {
        this.context=context;
        database=FirebaseDatabase.getInstance();
    }

    //리스트뷰에 게시판의 타이틀 설정
    public void setListView(final String userID, final ListView listView, final String board_name) {
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> titles=new ArrayList<>();
                final ArrayList<Integer> ids=new ArrayList<>();
                for(DataSnapshot snapshot: dataSnapshot.child(board_name).getChildren()) {
                    String title=snapshot.child("title").getValue(String.class);
                    int id=snapshot.child("id").getValue(Integer.class);
                    titles.add(title);
                    ids.add(id);
                }
                Collections.reverse(titles);
                Collections.reverse(ids);
                ArrayAdapter adapter=new ArrayAdapter(context, R.layout.dropown_item_custom, titles);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent=new Intent(context, BoardActivity.class);
                        intent.putExtra("boardName", board_name);
                        intent.putExtra("id", ids.get(position));
                        intent.putExtra("userID", userID);
                        context.startActivity(intent);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //특정 그룹의 게시판 설정
    public void setGroupListView(final String groupName, final String userID, final ListView listView, final String board_name) {
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> titles=new ArrayList<>();
                final ArrayList<Integer> ids=new ArrayList<>();
                String newName=TrimName(groupName);
                for(DataSnapshot snapshot: dataSnapshot.child("Groups").child(newName).child(board_name).getChildren()) {
                    String title=snapshot.child("title").getValue(String.class);
                    int id=snapshot.child("id").getValue(Integer.class);
                    titles.add(title);
                    ids.add(id);
                }
                Collections.reverse(titles);
                Collections.reverse(ids);
                ArrayAdapter adapter=new ArrayAdapter(context, R.layout.dropown_item_custom, titles);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent=new Intent(context, BoardActivity.class);
                        intent.putExtra("groupName", groupName);
                        intent.putExtra("boardName", board_name);
                        intent.putExtra("id", ids.get(position));
                        intent.putExtra("userID", userID);
                        context.startActivity(intent);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //리스트뷰에 게시판의 타이틀 설정, 검색 값 설정
    public void setListView(final String userID, final ListView listView, final String board_name, final String value) {
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> titles=new ArrayList<>();
                final ArrayList<Integer> ids=new ArrayList<>();
                for(DataSnapshot snapshot: dataSnapshot.child(board_name).getChildren()) {
                    String title=snapshot.child("title").getValue(String.class);
                    if(title.contains(value)) {
                        int id = snapshot.child("id").getValue(Integer.class);
                        titles.add(title);
                        ids.add(id);
                    }
                }
                Collections.reverse(titles);
                Collections.reverse(ids);
                ArrayAdapter adapter=new ArrayAdapter(context, R.layout.dropown_item_custom, titles);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent=new Intent(context, BoardActivity.class);
                        intent.putExtra("boardName", board_name);
                        intent.putExtra("id", ids.get(position));
                        intent.putExtra("userID", userID);
                        context.startActivity(intent);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //특정 그룹의 리스트뷰에 게시판의 타이틀 설정, 검색 값 설정
    public void setGroupListView(final String groupName, final String userID, final ListView listView, final String board_name, final String value) {
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> titles=new ArrayList<>();
                final ArrayList<Integer> ids=new ArrayList<>();
                for(DataSnapshot snapshot: dataSnapshot.child("Groups").child(groupName).child(board_name).getChildren()) {
                    String title=snapshot.child("title").getValue(String.class);
                    if(title.contains(value)) {
                        int id = snapshot.child("id").getValue(Integer.class);
                        titles.add(title);
                        ids.add(id);
                    }
                }
                Collections.reverse(titles);
                Collections.reverse(ids);
                ArrayAdapter adapter=new ArrayAdapter(context, R.layout.dropown_item_custom, titles);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent=new Intent(context, BoardActivity.class);
                        intent.putExtra("groupName", groupName);
                        intent.putExtra("boardName", board_name);
                        intent.putExtra("id", ids.get(position));
                        intent.putExtra("userID", userID);
                        context.startActivity(intent);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String TrimName(String name) {
        name.replace(".", "");
        return name;
    }
}
