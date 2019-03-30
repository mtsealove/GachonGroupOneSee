package kr.ac.gachon.www.GachonGroup.Board;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
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

public class PRBoardActivity extends AppCompatActivity { //홍보게시판 액티비티
    public static Activity PRBActivity;
    Button prevBtn, nextBtn, searchBtn,addBtn;
    public static ArrayList<PRFragment> fragments;
    static int pageCount=0;
    int page;
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

    //프래그먼트 설정
    private void createFragment() {
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference reference=database.getReference().child("PublicRelation");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int total=(int)(dataSnapshot.getChildrenCount()); //데이터의 개수
                int ratio=getScreenRatio();
                int page=total/ratio;
                int nam=total%ratio;
                if(nam>0) page++; //9 또는 6의 배수가 아닐 경우 페이지 1개 추가

                fragments=new ArrayList<>();
                for(int i=0; i<page; i++) {
                    PRFragment fragment=new PRFragment();
                    Bundle bundle=new Bundle(4);
                    bundle.putInt("page", i+1); //페이지 번호 전송
                    bundle.putInt("count", ratio); //페이지당 게시물 개수
                    bundle.putString("userID", userID); //사용자 ID
                    if(value!=null) bundle.putString("value", value); //검색어 존재 시
                    fragment.setArguments(bundle); //fragment에 데이터 전달
                    fragments.add(fragment); //fragment리스트에 추가
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentView, fragments.get(0)).commit(); //첫번째 페이지로 설정
                setIndicator(0); //인디케이터 첫 페이지로 설정
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    //다음 페이지 보기
    private void setNextBtn() {
        if(pageCount<fragments.size()-1) { //마지막 페이지가 아닐 경우
            pageCount++; //현재 페이지 ++
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentView, fragments.get(pageCount)).commit(); //페이지 설정
            setIndicator(pageCount); //인디케이터 설정

        } else {
            Toast.makeText(PRBoardActivity.this, "마지막 페이지 입니다", Toast.LENGTH_SHORT).show();
        }
    }
    //이전 페이지 보기
    private void setPrevBtn() {
        if(pageCount>0) { //첫 번째 페이지가 아닐 경우
            pageCount--; //현재 페이지--
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentView, fragments.get(pageCount)).commit(); //페이지 설정
            setIndicator(pageCount); //인디케이터 설정
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
        if(!is_manger) { //관리자가 아닐 경우
            Toast.makeText(PRBoardActivity.this, "작성 권한이 없습니다", Toast.LENGTH_SHORT).show();
        } else { //관리자일 경우
            Intent intent=new Intent(PRBoardActivity.this, PostActivity.class);
            intent.putExtra("boardName", "PublicRelation");
            intent.putExtra("userID", userID);
            startActivity(intent);
        }
    }

    public void close(View v){
        finish();
    }

    //인디케이터 설정
    View[] dots=new View[7];
    int [] dotIds=new int[7];
    private void setIndicator(int current) {
        dotIds[0]=R.id.dot0;
        dotIds[1]=R.id.dot1;
        dotIds[2]=R.id.dot2;
        dotIds[3]=R.id.dot3;
        dotIds[4]=R.id.dot4;
        dotIds[5]=R.id.dot5;
        dotIds[6]=R.id.dot6;
        //모든 인디케이터를 안보이게
        for(int i=0; i<7; i++) {
            dots[i] = findViewById(dotIds[i]);
            dots[i].setVisibility(View.GONE);
        }

        //인디케이터 리소스
        Drawable selected_dot=getDrawable(R.drawable.selected_dot);
        Drawable default_dot=getDrawable(R.drawable.default_dot);
        //페이지 수만큼 표시
        for(int i=0; i<fragments.size(); i++) {
            dots[i].setVisibility(View.VISIBLE);
            dots[i].setBackgroundDrawable(default_dot);
            if(i==current) dots[i].setBackgroundDrawable(selected_dot);
            if(i==6) break;
        }
    }

    @Override
    public void onResume(){ //작성 후 복귀 시 업데이트 된 리스트 표시
        super.onResume();
            fragments=new ArrayList<>();
            createFragment();
    }
    @Override
    public void onBackPressed() { //검색 값이 있을 경우 검색 없는 액티비티로 이동
        if(value!=null) {
            Intent intent=new Intent(PRBoardActivity.this, PRBoardActivity.class);
            intent.putExtra("userID", userID);
            intent.putExtra("is_manager", is_manger);
            startActivity(intent);
            finish();
        } else super.onBackPressed();
    }

    //화면 비율에 따라 다른 리스트 개수 반환
    private int getScreenRatio() {
        DisplayMetrics displayMetrics=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width=displayMetrics.widthPixels; //가로 픽셀 수
        int height=displayMetrics.heightPixels; //세로 픽셀 수

        double ratio=((double)height)/((double)width); //화면 비율
        if(ratio>2) return 9; //2:1 이상일 경우 9개
        else return 6; //이하일 경우 6개 표시
    }
}
