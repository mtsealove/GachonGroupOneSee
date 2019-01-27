package kr.ac.gachon.www.GachonGroup.Account;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

import kr.ac.gachon.www.GachonGroup.Entity.Account;
import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseAccount;
import kr.ac.gachon.www.GachonGroup.R;
import kr.ac.gachon.www.GachonGroup.modules.Alert;

public class LoginActivity extends AppCompatActivity {
    EditText ID_et;
    EditText PW_et;
    Button LoginBtn;
    RadioGroup UserCategoryRg;
    public static Account account;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ID_et= findViewById(R.id.ID_et);
        PW_et= findViewById(R.id.PW_et);
        LoginBtn= findViewById(R.id.LoginBtn);
        UserCategoryRg= findViewById(R.id.user_categoryRG);

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });

        Intent intent=getIntent();
        boolean logout=intent.getBooleanExtra("logout", false);
        if(!logout) {
            read_ID();
            if (ID_et.length() != 0 && PW_et.length() != 0) LoginBtn.performClick();
        } else {
            Alert alert=new Alert(LoginActivity.this);
            alert.MsgDialog("로그아웃 되었습니다");
        }
    }
    //로그인 버튼 이벤트
    private void Login() {
        int radioID=UserCategoryRg.getCheckedRadioButtonId();
        String UserCategoryKR=((RadioButton)findViewById(radioID)).getText().toString();
        boolean isManager;
        isManager = UserCategoryKR.equals("관리자");
        String ID=ID_et.getText().toString();
        String password=PW_et.getText().toString();
        AES256Util aes256Util= null;
        try {
            aes256Util = new AES256Util();
            password=aes256Util.encrypt(password);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        //아이디, 비밀번호 미입력 체크
        if(ID.length()==0) Toast.makeText(LoginActivity.this, "ID를 입력해 주세요", Toast.LENGTH_SHORT).show();
        else if(password.length()==0) Toast.makeText(LoginActivity.this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
        else {
            FirebaseAccount firebaseAccount=new FirebaseAccount(LoginActivity.this);
            firebaseAccount.Login(ID, password, isManager);
                write_ID(ID, password);

        }
    }

    private void write_ID(String ID, String password) {
        try {
            BufferedWriter bw=new BufferedWriter(new FileWriter(new File(getFilesDir()+"login.dat")));
            bw.write(ID);
            bw.newLine();
            bw.write(password);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void read_ID() {
        try {
            BufferedReader br=new BufferedReader(new FileReader(new File(getFilesDir()+"login.dat")));
            String ID=br.readLine();
            String pw=br.readLine();
            AES256Util aes256Util=new AES256Util();
            pw=aes256Util.decrypt(pw);
            ID_et.setText(ID);
            PW_et.setText(pw);
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    //아이디, 비밀번호 찾기 화면으로 이동
    public void Find_ID(View v) {
        Intent FindID=new Intent(LoginActivity.this, FindIdActivity.class);
        startActivity(FindID);
    }

    //회원가입 화면으로 이동
    public void Sign_Up(View v) {
        Intent SignUp=new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(SignUp);
    }

}
