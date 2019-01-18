package kr.ac.gachon.www.GachonGroup;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import kr.ac.gachon.www.GachonGroup.modules.FirebaseHelper;

public class PRBoardActivity extends AppCompatActivity {
    Button prevBtn, nextBtn, searchBtn;
    ArrayList<PRFragment> fragments;
    int pageCount=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prboard);
        prevBtn= findViewById(R.id.prevBtn);
        nextBtn= findViewById(R.id.nextBtn);
        searchBtn= findViewById(R.id.searchBtn);
        fragments=new ArrayList<>();

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
    }
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
                        Bundle bundle=new Bundle(1);
                        bundle.putStringArrayList("titles", titles);
                        fragments.get(page).setArguments(bundle);
                        fragments.add(new PRFragment());
                        page++;
                        titles=new ArrayList<>();
                    }
                }
                Bundle bundle=new Bundle(1);
                bundle.putStringArrayList("titles", titles);
                fragments.get(page).setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentView, fragments.get(0)).commit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void setNextBtn() {
        if(pageCount<fragments.size()-1) {
            pageCount++;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentView, fragments.get(pageCount)).commit();

        } else {
            Toast.makeText(PRBoardActivity.this, "마지막 페이지 입니다", Toast.LENGTH_SHORT).show();
        }
    }
    private void setPrevBtn() {
        if(pageCount>0) {
            pageCount--;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentView, fragments.get(pageCount)).commit();
        } else {
            Toast.makeText(PRBoardActivity.this, "첫 번째 페이지 입니다", Toast.LENGTH_SHORT).show();
        }
    }
    private void setSearchBtn() {
        Intent intent=new Intent(PRBoardActivity.this, PRSearchActivity.class);
        ArrayList<ArrayList<String>>titles =new ArrayList<>();
        for(int i=0; i<fragments.size(); i++) {
            titles.add(fragments.get(i).titles);
        }
        intent.putExtra("titles", titles);
        startActivity(intent);
    }
}
