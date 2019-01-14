package kr.ac.gachon.www.GachonGroup;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.InputMismatchException;
import java.util.Timer;
import java.util.TimerTask;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

import kr.ac.gachon.www.GachonGroup.modules.Alert;
import kr.ac.gachon.www.GachonGroup.modules.FirebaseHelper;
import kr.ac.gachon.www.GachonGroup.modules.GmailSender;

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

    Alert alert = new Alert();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());

        //뷰 매칭
        nameET = (EditText) findViewById(R.id.nameET);
        IDET = (EditText) findViewById(R.id.ID_ET);
        emailET = (EditText) findViewById(R.id.email_ET);
        majorSP = (Spinner) findViewById(R.id.major_SP);
        majorET = (EditText) findViewById(R.id.major_ET);
        StudentNumberET = (EditText) findViewById(R.id.StdNumber_ET);
        groupSP = (Spinner) findViewById(R.id.group_SP);
        passwordET = (EditText) findViewById(R.id.password_ET);
        password_confirmET = (EditText) findViewById(R.id.password_confirm_ET);
        signUpBTN = (Button) findViewById(R.id.sign_up_btn);
        checkReuseBTN = (Button) findViewById(R.id.check_id_reuse_btn);
        sendVerifyBTN = (Button) findViewById(R.id.send_verify_btn);

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
            FirebaseHelper firebaseHelper = new FirebaseHelper();
            firebaseHelper.Check_ID_Reuse(IDstr, IDreuse, SignUpActivity.this);
        }
    }

    //인증메일 발송 메서드
    public static String verify_code; //인증번호

    private void SendVerifyCode() {
        String email = emailET.getText().toString();
        if (email.length() == 0) shortToast("이메일을 입력해주세요");
        else if ((!email.contains("@gachon.ac.kr")) && (!email.contains("@mc.gachon.ac.kr")) && (!email.contains("@gc.gachon.ac.kr"))) { //가천대학교 메일이 아닌 경우 알림 출력
            alert.MsgDialog("올바른 이메일을\n입력해주세요", SignUpActivity.this);
        } else {
            boolean reuse[] = {true};
            FirebaseHelper helper = new FirebaseHelper();
            helper.Check_Email_Reuse(email, reuse, SignUpActivity.this);
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
                return;
            }
            myNumber = mgr.getLine1Number();
            myNumber = myNumber.replace("+82", "0");

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
        else  {
            //모든 조건이 만족하면 계정 생성
            AlertDialog.Builder builder=new AlertDialog.Builder(SignUpActivity.this);
            LayoutInflater inflater=getLayoutInflater();
            View layout=inflater.inflate(R.layout.dialog_msg_choice, null);
            builder.setView(layout);
            TextView msgTV=(TextView)layout.findViewById(R.id.dialog_msgTV);
            msgTV.setText("회원가입을 하시겠습니까?");
            Button negativeBtn=(Button)layout.findViewById(R.id.negative);
            Button positiveBtn=(Button)layout.findViewById(R.id.positive);
            final AlertDialog dialog=builder.create();
            negativeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
            final String finalMyNumber = myNumber;
            positiveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Account account=new Account(name, ID, email, major, stdNumber, group, password, finalMyNumber, false);
                    FirebaseHelper firebaseHelper=new FirebaseHelper();
                    firebaseHelper.CreateAccount(account);
                    alert.MsgDialogEnd("회원가입이\n완료되었습니다", SignUpActivity.this);
                }
            });
            dialog.show();
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
