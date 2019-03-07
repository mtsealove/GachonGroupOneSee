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
import kr.ac.gachon.www.GachonGroup.Board.PRBoardActivity;
import kr.ac.gachon.www.GachonGroup.R;

public class FirebaseList extends AppCompatActivity {   //firebase를 이용한 listview 설정
    final Context context;
    FirebaseDatabase database;
    public FirebaseList(Context context) {
        this.context=context;
        database=FirebaseDatabase.getInstance();
    }

    //일반 게시판 타이틀 설정
    public void setListView(final String userID, final ListView listView, final String board_name) {
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> titles=new ArrayList<>();
                final ArrayList<Integer> ids=new ArrayList<>();
                for(DataSnapshot snapshot: dataSnapshot.child(board_name).getChildren()) { //각 게시판에 있는 모든 데이터를 읽음
                    String temp=snapshot.child("temp").getValue(String.class);
                    if(temp==null) { //임시저장한 글인지 판단
                        String title = snapshot.child("title").getValue(String.class); //제목
                        int id = snapshot.child("id").getValue(Integer.class); //게시글 ID
                        titles.add(title); //제목과
                        ids.add(id);    //ID를 리스트에 추가
                    }
                }
                //최신순 정렬
                Collections.reverse(titles);
                Collections.reverse(ids);
                ArrayAdapter adapter=new ArrayAdapter(context, R.layout.dropown_item_custom, titles);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {//글 선택 시
                        Intent intent=new Intent(context, BoardActivity.class);
                        intent.putExtra("boardName", board_name); //게시판 이름
                        intent.putExtra("id", ids.get(position));   //게시글 번호
                        intent.putExtra("userID", userID);  //사용자 ID(자신의 글인지 판단하기 위함)
                        context.startActivity(intent);  //게시글로 이동
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
                String newName=TrimName(groupName); //동아리 이름 중 '.'이 들어가 있는 것 제거
                for(DataSnapshot snapshot: dataSnapshot.child("Groups").child(newName).child(board_name).getChildren()) { //데이터 읽기
                    String temp=snapshot.child("temp").getValue(String.class);
                    if(temp==null) { //임시저장 확인
                        String title = snapshot.child("title").getValue(String.class); //제목
                        int id = snapshot.child("id").getValue(Integer.class);  //게시글 번호
                        titles.add(title);
                        ids.add(id);
                    }
                }
                //최신순 정렬
                Collections.reverse(titles);
                Collections.reverse(ids);

                ArrayAdapter adapter=new ArrayAdapter(context, R.layout.dropown_item_custom, titles);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent=new Intent(context, BoardActivity.class);
                        intent.putExtra("groupName", groupName);    //동아리 이름
                        intent.putExtra("boardName", board_name);   //게시판 이름
                        intent.putExtra("id", ids.get(position));   //게시글 번호
                        intent.putExtra("userID", userID);  //사용자 ID
                        context.startActivity(intent);  //게시글로 이동
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
                    String tmp=snapshot.child("temp").getValue(String.class);
                    if(title.contains(value)&&tmp==null) { //제목에 검색어가 포함될 경우
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
                    String tmp=snapshot.child("temp").getValue(String.class);
                    if(title.contains(value)&&tmp==null) { //제목에 검색어가 포함될 경우
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

    //리스트뷰에 게시판의 타이틀 설정(개수 지정, fragment에서 사용)
    public void setCountListView(final String userID, final ListView listView, final String board_name, final int page, final int numOfRows) {//페이지 번호와 한 페이지당 표시될 개수
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count=0;
                ArrayList<String> titles=new ArrayList<>();
                final ArrayList<Integer> ids=new ArrayList<>();
                for(DataSnapshot snapshot: dataSnapshot.child(board_name).getChildren()) {
                    String temp=snapshot.child("temp").getValue(String.class);
                    if(temp==null) {
                        String title=snapshot.child("title").getValue(String.class);
                        int id=snapshot.child("id").getValue(Integer.class);
                            titles.add(title);
                            ids.add(id);
                    }
                }
                //최신정렬을 위해 reverse

                Collections.reverse(titles);
                Collections.reverse(ids);

                ArrayList<String> newTitle=new ArrayList<>();
                final ArrayList<Integer> newID=new ArrayList<>();
                for(int i=0; i<numOfRows; i++) { //페이지당 개수만큼
                    try {
                        newTitle.add(titles.get((page - 1) * numOfRows + i));   //페이지에 해당하는 데이터 얻기
                        newID.add(ids.get((page - 1) * numOfRows + i));
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }
                }

                ArrayAdapter adapter=new ArrayAdapter(context, R.layout.dropown_item_custom, newTitle);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent=new Intent(context, BoardActivity.class);
                        intent.putExtra("boardName", board_name);
                        intent.putExtra("id", newID.get(position));
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
    //리스트뷰에 게시판의 타이틀 설정(개수 지정), 검색값 지정
    public void setCountListView(final String userID, final ListView listView, final String board_name, final int page, final int numOfRows, final String value) {
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count=0;
                ArrayList<String> titles=new ArrayList<>();
                final ArrayList<Integer> ids=new ArrayList<>();
                for(DataSnapshot snapshot: dataSnapshot.child(board_name).getChildren()) {
                    String temp=snapshot.child("temp").getValue(String.class);
                    if(temp==null) {
                        String title=snapshot.child("title").getValue(String.class);
                        if(title.contains(value)) {
                            int id = snapshot.child("id").getValue(Integer.class);
                            titles.add(title);
                            ids.add(id);
                        }
                    }
                }
                //최신정렬을 위해 reverse

                Collections.reverse(titles);
                Collections.reverse(ids);

                ArrayList<String> newTitle=new ArrayList<>();
                final ArrayList<Integer> newID=new ArrayList<>();
                for(int i=0; i<numOfRows; i++) {
                    try {
                        newTitle.add(titles.get((page - 1) * numOfRows + i));
                        newID.add(ids.get((page - 1) * numOfRows + i));
                        count++;
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }
                }

                if(count==0) PRBoardActivity.fragments.remove(page-1);  //만약 받아온 데이터가 없으면 fragment삭제

                ArrayAdapter adapter=new ArrayAdapter(context, R.layout.dropown_item_custom, newTitle);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent=new Intent(context, BoardActivity.class);
                        intent.putExtra("boardName", board_name);
                        intent.putExtra("id", newID.get(position));
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

    private String TrimName(String name) {  //'.'을 포함하는 동아리 이름에서 .삭제
        name.replace(".", "");
        return name;
    }
}
