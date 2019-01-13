package kr.ac.gachon.www.GachonGroup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
