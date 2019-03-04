package kr.ac.gachon.www.GachonGroup.Board;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import kr.ac.gachon.www.GachonGroup.R;

public class PRBoardActivity extends AppCompatActivity {
    public static Activity PRBActivity;
    Button prevBtn, nextBtn, searchBtn,addBtn;
    public static ArrayList<PRFragment> fragments;
    static int pageCount=0, page;
    boolean is_manger;
    private String userID;
    private String value;
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

        Intent intent=getIntent();
        userID=intent.getStringExtra("userID");
        is_manger=intent.getBooleanExtra("is_manager", false);
        value=intent.getStringExtra("value");

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
    private void resultFragment(final String value) {
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference reference=database.getReference().child("PublicRelation");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int total=(int)(dataSnapshot.getChildrenCount());
                 page=total/8;
                int nam=total%8;
                if(nam>0) page++;

                fragments=new ArrayList<>();
                for(int i=0; i<page; i++) {
                    PRFragment fragment=new PRFragment();
                    Bundle bundle=new Bundle(4);
                    bundle.putInt("page", i+1);
                    bundle.putInt("count", 8);
                    bundle.putString("userID", userID);
                    bundle.putString("value", value);
                    fragment.setArguments(bundle);
                    fragments.add(fragment);
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentView, fragments.get(0)).commit();
                setIndicator(page, 0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //검색 없이 프래그먼트 설정
    private void createFragment() {
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference reference=database.getReference().child("PublicRelation");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int total=(int)(dataSnapshot.getChildrenCount());
                int page=total/8;
                int nam=total%8;
                if(nam>0) page++;

                fragments=new ArrayList<>();
                for(int i=0; i<page; i++) {
                    PRFragment fragment=new PRFragment();
                    Bundle bundle=new Bundle(4);
                    bundle.putInt("page", i+1);
                    bundle.putInt("count", 8);
                    bundle.putString("userID", userID);
                    if(value!=null) bundle.putString("value", value);
                    fragment.setArguments(bundle);
                    fragments.add(fragment);
                }
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
            setIndicator(page, pageCount);

        } else {
            Toast.makeText(PRBoardActivity.this, "마지막 페이지 입니다", Toast.LENGTH_SHORT).show();
        }
    }
    //이전 페이지 보기
    private void setPrevBtn() {
        if(pageCount>0) {
            pageCount--;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentView, fragments.get(pageCount)).commit();
            setIndicator(page, pageCount);
        } else {
            Toast.makeText(PRBoardActivity.this, "첫 번째 페이지 입니다", Toast.LENGTH_SHORT).show();
        }
    }

    //검색 페이지로 이동
    private void setSearchBtn() {
        Intent intent=new Intent(PRBoardActivity.this, SearchActivity.class);
        intent.putExtra("userID", userID);
        intent.putExtra("BoardName", "PRBoard");
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

    private void setIndicator(int page, int current) {
        LinearLayout IndicatorLayout=findViewById(R.id.indicatorLayout);
        IndicatorLayout.removeAllViews();

        for(int i=0; i<page; i++) {
            View view=new View(PRBoardActivity.this);
            if(current==i) view.setBackgroundDrawable(getDrawable(R.drawable.selected_dot));
            else view.setBackgroundDrawable(getDrawable(R.drawable.default_dot));

            IndicatorLayout.addView(view);
        }
    }

    @Override
    public void onResume(){
        super.onResume();

            fragments=new ArrayList<>();
            createFragment();
    }
}
