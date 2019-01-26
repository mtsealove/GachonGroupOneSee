package kr.ac.gachon.www.GachonGroup.Account;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.InputMismatchException;

import kr.ac.gachon.www.GachonGroup.Entity.Account;
import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseAccount;
import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseView;
import kr.ac.gachon.www.GachonGroup.R;
import kr.ac.gachon.www.GachonGroup.modules.Alert;
import kr.ac.gachon.www.GachonGroup.modules.FirebaseHelper;

public class SignUpActivity extends AppCompatActivity {
    //회원가입에 사용할 입력창들
    EditText nameET;
    EditText IDET;
    public static EditText emailET;
    Spinner majorSP;
    EditText StudentNumberET;
    Spinner groupSP;
    EditText passwordET;
    EditText password_confirmET;
    EditText majorET;
    //회원가입 버튼
    Button signUpBTN;
    //인증번호, 중복확인 버튼
    Button sendVerifyBTN;
    Button checkReuseBTN;

    private String major = "";
    private String group = "";

    Alert alert;
    FirebaseAccount firebaseAccount;
    FirebaseView firebaseView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        alert=new Alert(SignUpActivity.this);
        //뷰 매칭
        nameET = findViewById(R.id.nameET);
        IDET = findViewById(R.id.ID_ET);
        emailET = findViewById(R.id.email_ET);
        majorSP =findViewById(R.id.major_SP);
        majorET = findViewById(R.id.major_ET);
        StudentNumberET =findViewById(R.id.StdNumber_ET);
        groupSP =findViewById(R.id.group_SP);
        passwordET =findViewById(R.id.password_ET);
        password_confirmET =findViewById(R.id.password_confirm_ET);
        signUpBTN = findViewById(R.id.sign_up_btn);
        checkReuseBTN =findViewById(R.id.check_id_reuse_btn);
        sendVerifyBTN =findViewById(R.id.send_verify_btn);

        checkReuseBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckIDReuse();
            }
        });
        sendVerifyBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendVerifyCode();
            }
        });
        firebaseAccount =new FirebaseAccount(SignUpActivity.this);
        firebaseView=new FirebaseView(SignUpActivity.this);

        //학과 선택 스피너
        majorSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //기타 설정 시 EditText활성화
                if (parent.getItemAtPosition(position).equals("기타"))
                    majorET.setVisibility(View.VISIBLE);
                else { //나머지 경우 학과 Str에 저장
                    majorET.setVisibility(View.INVISIBLE);
                    major = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        FirebaseHelper helper=new FirebaseHelper();
        firebaseView.getAllGroupName(groupSP);
        //동아리 선택 스피너
        groupSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                group = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        signUpBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUp();
            }
        });
    }

    //아이디 중복 확인 메서드
    boolean IDreuse[] = {false}; //메서드의 결과를 반환할 변수

    private void CheckIDReuse() {
        String IDstr = IDET.getText().toString();
        if (IDstr.length() == 0) { //미입력 시 입력 요구 토스트 출력
            shortToast("아이디를 입력해주세요");
            IDreuse[0] = false;
        } else {
            firebaseAccount.Check_ID_Reuse(IDstr, IDreuse);
        }
    }

    //인증메일 발송 메서드
    public static String verify_code; //인증번호

    private void SendVerifyCode() {
        String email = emailET.getText().toString();
        if (email.length() == 0) shortToast("이메일을 입력해주세요");
        else if ((!email.contains("@gachon.ac.kr")) && (!email.contains("@mc.gachon.ac.kr")) && (!email.contains("@gc.gachon.ac.kr"))) { //가천대학교 메일이 아닌 경우 알림 출력
            alert.MsgDialog("올바른 이메일을\n입력해주세요");
        } else {
            boolean reuse[] = {true};
            firebaseAccount.Check_Email_Reuse(email, reuse);
        }
    }

    public static boolean Verified = false;

    private void SignUp() {
        final String name = nameET.getText().toString();
        final String ID = IDET.getText().toString();
        final String email = emailET.getText().toString();

        int stdNumberTry;
        try {
            stdNumberTry = Integer.parseInt(StudentNumberET.getText().toString());
        } catch (InputMismatchException e) {
            stdNumberTry = 0;
        }
        final int stdNumber = stdNumberTry;
        final String password = passwordET.getText().toString();
        String password_confirm = password_confirmET.getText().toString();

        String myNumber = null;
        TelephonyManager mgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                myNumber = mgr.getLine1Number();
                myNumber = myNumber.replace("+82", "0");
                Toast.makeText(SignUpActivity.this, "전화번호:" + myNumber, Toast.LENGTH_SHORT).show();
            }

        }catch(Exception e){}

        if(name.length()==0) shortToast("이름을 입력하세요");
        else if(!IDreuse[0]) shortToast("아이디 중복을 확인하세요");
        else if(!Verified) shortToast("이메일 인증이 완료되지 않았습니다");
        else if(major.equals("학과 선택        ")) shortToast("학과를 선택해주세요");
        else if(Integer.toString(stdNumber).length()!=9) shortToast("정확한 학번을 입력해주세요");
        else if(group.equals("동아리 선택")) shortToast("동아리를 선택해주세요");
        else if(password.length()==0) shortToast("비밀번호를 입력해주세요");
        else if(password_confirm.length()==0) shortToast("비밀번호를 확인해주세요");
        else if(!password.equals(password_confirm)) shortToast("비밀번호가 일치하지 않습니다");
        else {
            final String finalMyNumber1 = myNumber;
            alert.MsgDialogChoice("회원가입을 하시겠습니까?",  new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Account account = new Account(name, ID, email, major, stdNumber, group, password, finalMyNumber1, false);
                    firebaseAccount.CreateAccount(account);
                    alert.MsgDialogEnd("회원가입이\n완료되었습니다");
                }
            });
        }
    }

    //뒤로가기 메서드/화면상의 버튼에 매칭
    public void GoBack(View v) {
        onBackPressed();
    }

    //짧은 토스트 출력 메서드
    private void shortToast(String msg) {
        Toast.makeText(SignUpActivity.this, msg, Toast.LENGTH_SHORT).show();
    }


}
