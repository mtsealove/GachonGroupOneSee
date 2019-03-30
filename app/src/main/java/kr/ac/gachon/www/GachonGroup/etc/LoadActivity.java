package kr.ac.gachon.www.GachonGroup.etc;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import kr.ac.gachon.www.GachonGroup.Account.LoginActivity;
import kr.ac.gachon.www.GachonGroup.Account.SignUpActivity;
import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseAccount;
import kr.ac.gachon.www.GachonGroup.R;

public class LoadActivity extends AppCompatActivity { //애플리케이션 시작 시 실행되는 로드 액티비티
    ProgressBar PB;
    public static final String WIFE_STATE = "WIFE";
    public static final String MOBILE_STATE = "MOBILE";
    public static final String NONE_STATE = "NONE";
    private String ID, pw;
    private boolean newtwork = true;

    public static final int PERMISSION_READ_PHONE_NUMBER=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        String getNetwork =  getWhatKindOfNetwork(getApplication()); //네트워크 상태 확인
        if(getNetwork.equals("NONE")){
            newtwork = false;
        }
        //인터넷에 연결되어 있지 않으면 1초 후 종료
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

        //전화번호 읽기 권한 확인
        int phonePermission=ContextCompat.checkSelfPermission(LoadActivity.this, Manifest.permission.READ_PHONE_NUMBERS);

        if(phonePermission==PackageManager.PERMISSION_DENIED) { //권한 확인
            TedPermission.with(LoadActivity.this)
                    .setPermissionListener(PhonePermissionListener)
                    .setRationaleMessage("한눈에 보자를 사용하기 위해 권한이 필요합니다")
                    .setPermissions(Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_SMS)
                    .check();
        } else {
            if(read_ID()) { //ID, 비밀번호가 존재하면
                FirebaseAccount firebaseAccount=new FirebaseAccount(LoadActivity.this);
                firebaseAccount.AutoLogin(ID, pw); //자동로그인 수행
            }
            else {
                Move_Login(); //존재하지 않으면 로그인 화면으로 이동
            }
        }
    }

    public static String getWhatKindOfNetwork(Context context) { //네트워크 상태 체크
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return WIFE_STATE; //Wi-Fi 사용 중
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return MOBILE_STATE; //모바일 데이터 사용 중
            }
        }
        return NONE_STATE; //네트워크 연결 안됨
    }
    private void Move_Login() {
        //로그인 액티비티로 이동
        Intent intent=new Intent(LoadActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean read_ID() { //ID, 비밀번호 단말에서 읽기
        try {
            BufferedReader br=new BufferedReader(new FileReader(new File(getFilesDir()+"login.dat"))); //ID, 비밀번호 저장 파일
            ID=br.readLine();
            pw=br.readLine();
            br.close();
            if(ID!=null&&ID.length()>1) //ID를 읽었을 경우 true 반환
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    PermissionListener PhonePermissionListener=new PermissionListener() {
        @Override
        public void onPermissionGranted() { //권한 허용 시
            Move_Login(); //로그인 화면으로 이동
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {   //권한 미허용 시
            Toast.makeText(getApplicationContext(), "권한을 설정하지 않으면 회원가입을 할 수 없습니다\n곧 애플리케이션이 종료됩니다", Toast.LENGTH_SHORT).show();
            Handler handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    System.exit(0);
                }
            }, 2000);   //2초 후 프로그램 종료
        }
    };
}
