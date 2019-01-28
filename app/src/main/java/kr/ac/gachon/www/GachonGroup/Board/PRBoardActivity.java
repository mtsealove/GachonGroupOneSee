package kr.ac.gachon.www.GachonGroup.Board;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import kr.ac.gachon.www.GachonGroup.R;

public class PRBoardActivity extends AppCompatActivity {
    public static Activity PRBActivity;
    Button prevBtn, nextBtn, searchBtn,addBtn;
    ArrayList<PRFragment> fragments;
    int pageCount=0;
    boolean is_manger;
    private ArrayList<Integer> ids;
    private ArrayList<String> titles;
    private String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prboard);
        PRBActivity=PRBoardActivity.this;
        //버튼 매칭
        prevBtn= findViewById(R.id.prevBtn);
        nextBtn= findViewById(R.id.nextBtn);
        searchBtn= findViewById(R.id.searchBtn);
        addBtn= findViewById(R.id.addBtn);
        //프래그먼트 arraylist 생성
        fragments=new ArrayList<>();

        Intent intent=getIntent();
        userID=intent.getStringExtra("userID");
        titles= intent.getStringArrayListExtra("titles");
        is_manger=intent.getBooleanExtra("is_manager", false);
        try {
            ids= intent.getIntegerArrayListExtra("ids");
        } catch (Exception e) {
        }

        if(titles!=null)
            resultFragment(titles, ids);
        else
            createFragment();

        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPrevBtn();
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNextBtn();
            }
        });
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSearchBtn();
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAddBtn();
            }
        });
    }
    //검색결과를 통해 프래그먼트 설정
    private void resultFragment(ArrayList<String> titles, ArrayList<Integer> ids) {
        fragments.add(new PRFragment());
        int page=0;
        ArrayList<String> newTitles=new ArrayList<>();
        ArrayList<Integer> newIds=new ArrayList<>();
        try {
            for(int i=0; i<titles.size(); i++) {
                newTitles.add(titles.get(i));
                newIds.add(ids.get(i));
                if(newTitles.size()==8) {
                    Bundle bundle=new Bundle(2);
                    bundle.putStringArrayList("titles", newTitles);
                    bundle.putIntegerArrayList("ids", newIds);
                    fragments.get(page).setArguments(bundle);
                    fragments.add(new PRFragment());
                    newTitles=new ArrayList<>();
                }
            }
            Bundle bundle=new Bundle(2);
            bundle.putStringArrayList("titles", newTitles);
            bundle.putIntegerArrayList("ids", newIds);
            fragments.get(page).setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentView, fragments.get(0)).commit();
        } catch (Exception e) {

        }
    }
    //검색 없이 프래그먼트 설정
    private void createFragment() {
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int page=0;
                ArrayList<String> titles=new ArrayList<>();
                fragments.add(new PRFragment());
                for(DataSnapshot snapshot: dataSnapshot.child("PublicRelation").getChildren()) {
                    String title=snapshot.child("title").getValue(String.class);
                    titles.add(title);
                    if(titles.size()==8) {
                        Bundle bundle=new Bundle(2);
                        bundle.putStringArrayList("titles", titles);
                        bundle.putInt("page", page);
                        fragments.get(page).setArguments(bundle);
                        fragments.add(new PRFragment());
                        page++;
                        titles=new ArrayList<>();
                    }
                }
                Bundle bundle=new Bundle(2);
                bundle.putStringArrayList("titles", titles);
                bundle.putInt("page", page);
                fragments.get(page).setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentView, fragments.get(0)).commit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //다음 페이지 보기
    private void setNextBtn() {
        if(pageCount<fragments.size()-1) {
            pageCount++;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentView, fragments.get(pageCount)).commit();

        } else {
            Toast.makeText(PRBoardActivity.this, "마지막 페이지 입니다", Toast.LENGTH_SHORT).show();
        }
    }
    //이전 페이지 보기
    private void setPrevBtn() {
        if(pageCount>0) {
            pageCount--;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentView, fragments.get(pageCount)).commit();
        } else {
            Toast.makeText(PRBoardActivity.this, "첫 번째 페이지 입니다", Toast.LENGTH_SHORT).show();
        }
    }

    //검색 페이지로 이동
    private void setSearchBtn() {
        Intent intent=new Intent(PRBoardActivity.this, PRSearchActivity.class);
        startActivity(intent);
    }

    //작성 버튼
    private void setAddBtn() {
        if(!is_manger) {
            Toast.makeText(PRBoardActivity.this, "작성 권한이 없습니다", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent=new Intent(PRBoardActivity.this, AddPostActivity.class);
            intent.putExtra("boardName", "PublicRelation");
            intent.putExtra("userID", userID);
            startActivity(intent);
        }
    }

    public void close(View v){
        finish();
    }

    @Override
    public void onResume(){
        super.onResume();
        if(titles!=null)
            resultFragment(titles, ids);
        else
            createFragment();
    }
}
