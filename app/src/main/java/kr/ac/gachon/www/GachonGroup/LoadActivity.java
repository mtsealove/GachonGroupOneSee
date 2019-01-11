package kr.ac.gachon.www.GachonGroup;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

public class LoadActivity extends AppCompatActivity {
    ProgressBar PB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        //hander 객체로 0.7초 딜레이 설정
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //로그인 액티비티로 이동
                Intent intent=new Intent(LoadActivity.this, LoadActivity.class);
                startActivity(intent);
                finish();
            }
        }, 700);
    }
}
