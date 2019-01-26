package kr.ac.gachon.www.GachonGroup;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import kr.ac.gachon.www.GachonGroup.Account.LoginActivity;

public class LoadActivity extends AppCompatActivity {
    ProgressBar PB;
    public static final String WIFE_STATE = "WIFE";
    public static final String MOBILE_STATE = "MOBILE";
    public static final String NONE_STATE = "NONE";
    private boolean newtwork = true;

    public static final int PERMISSION_READ_PHONE_NUMBER=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        String getNetwork =  getWhatKindOfNetwork(getApplication());
        if(getNetwork.equals("NONE")){
            newtwork = false;
        }
        //인터넷에 연결되어 있지 않으면 0.5초 후 종료
        if(!newtwork) {
            Toast.makeText(getApplicationContext(), "인터넷 연결상태를 확인해 주세요\n프로그램을 종료합니다", Toast.LENGTH_SHORT).show();
            Handler handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    System.exit(0);
                }
            }, 1000);
        }

        int phonePermission=ContextCompat.checkSelfPermission(LoadActivity.this, Manifest.permission.READ_PHONE_NUMBERS);

        if(phonePermission==PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(LoadActivity.this,
                    new String[]{Manifest.permission.READ_PHONE_NUMBERS},
                    PERMISSION_READ_PHONE_NUMBER);
        } else {
            Move_Login();
        }



    }

    public static String getWhatKindOfNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return WIFE_STATE;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return MOBILE_STATE;
            }
        }
        return NONE_STATE;
    }
    private void Move_Login() {
        //hander 객체로 0.7초 딜레이 설정
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //로그인 액티비티로 이동
                Intent intent=new Intent(LoadActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 700);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult){
        super.onRequestPermissionsResult(requestCode, permissions, grantResult);
        switch (requestCode) {
            case PERMISSION_READ_PHONE_NUMBER:
                if(grantResult.length>0) {
                    Move_Login();
                } else {
                    Toast.makeText(getApplicationContext(), "전화번호 읽기 권한을 설정하지 않으면 회원가입을 할 수 없습니다\n곧 애플리케이션이 종료됩니다", Toast.LENGTH_SHORT).show();
                    Handler handler=new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            System.exit(0);
                        }
                    }, 2000);
                }
        }
    }
}
