package kr.ac.gachon.www.GachonGroup;

import android.app.Fragment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import java.util.ArrayList;

import kr.ac.gachon.www.GachonGroup.modules.FirebaseHelper;

public class PRBoardActivity extends AppCompatActivity {
    Button prevBtn, nextBtn;
    ArrayList<PRFragment> fragments;
    ArrayList<String> titles;
    int pageCount=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prboard);
        prevBtn=(Button)findViewById(R.id.prevBtn);
        nextBtn=(Button)findViewById(R.id.nextBtn);

        //프래그먼트 초기 셋팅
        fragments=new ArrayList<>();
        fragments.add(new PRFragment());
        Bundle bundle=new Bundle(1);
        bundle.putString("pageCount", "0");
        fragments.get(0).setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragments.get(0)).commit();

        titles=new ArrayList<>();
        FirebaseHelper helper=new FirebaseHelper();
        helper.getPRtitles(titles, fragments);


        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragments.get(0)).commit();
            }
        }, 3000);

    }
}
