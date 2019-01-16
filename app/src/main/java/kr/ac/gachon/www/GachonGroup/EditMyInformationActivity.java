package kr.ac.gachon.www.GachonGroup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import kr.ac.gachon.www.GachonGroup.modules.Alert;
import kr.ac.gachon.www.GachonGroup.modules.FirebaseHelper;

public class EditMyInformationActivity extends AppCompatActivity {
    private String ID;
    private Account account;
    EditText nameET, IDET, emailET, studentNumberET, passwordET;
    Spinner majorSP, groupSP;
    ImageView profileIcon;
    Button editBtn;
    FirebaseDatabase database;
    DatabaseReference reference;
    private String major, group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_my_information);
        nameET=(EditText)findViewById(R.id.nameET);
        IDET=(EditText)findViewById(R.id.IDET);
        emailET=(EditText)findViewById(R.id.emailET);
        studentNumberET=(EditText)findViewById(R.id.studentNumberET);
        passwordET=(EditText)findViewById(R.id.PWET);
        majorSP=(Spinner)findViewById(R.id.major_SP);
        groupSP=(Spinner)findViewById(R.id.group_SP);
        profileIcon=(ImageView)findViewById(R.id.ProfileIcon);
        editBtn=(Button)findViewById(R.id.edit_btn);

        //아이디 가져와서
        Intent intent=getIntent();
        ID=intent.getStringExtra("ID");
        //다른 정보들을 EditText 또는 Spinner로 출력
        FirebaseHelper helper=new FirebaseHelper();
        helper.setEditText("name", ID, nameET);
        helper.setEditText("ID", ID, IDET);
        helper.setEditText("email", ID, emailET);
        helper.setEditText("StudentNumber", ID, studentNumberET);
        helper.getAllGroupName(groupSP, EditMyInformationActivity.this);
        helper.setSpinnerMatch(groupSP, "group", ID);
        helper.setSpinnerMatch(majorSP, "major", ID);
        helper.setEditText("password", ID, passwordET);


        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageClick();
            }
        });
        database=FirebaseDatabase.getInstance();
        reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                group=dataSnapshot.child("Account").child(ID).child("group").getValue(String.class);
                major=dataSnapshot.child("Account").child(ID).child("major").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        helper.getProfileIcon(profileIcon, ID, EditMyInformationActivity.this);
        majorSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                major=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        groupSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                group=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplyChange();
            }
        });
    }

    private void ImageClick() {
        AlertDialog.Builder builder=new AlertDialog.Builder(EditMyInformationActivity.this);
        ListView listView=new ListView(EditMyInformationActivity.this);
        ArrayList<String> arrayList=new ArrayList<>();
        arrayList.add("이미지 보기");
        arrayList.add("편집");
        ArrayAdapter arrayAdapter=new ArrayAdapter(EditMyInformationActivity.this, R.layout.support_simple_spinner_dropdown_item, arrayList);
        listView.setAdapter(arrayAdapter);

        builder.setView(listView);
        final AlertDialog dialog=builder.create();
        dialog.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.cancel();
                switch (position) {
                    case 1:
                        EditImage();
                        break;
                }
            }
        });
    }

    private Uri filePath;
    private void EditImage() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "프로필 이미지를 선택하세요"), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //request코드가 0이고 OK를 선택했고 data에 뭔가가 들어 있다면
        if(requestCode == 0 && resultCode == RESULT_OK){
            filePath = data.getData();
            Log.d("EditMyInformation", "uri:" + String.valueOf(filePath));
            try {
                //Uri 파일을 Bitmap으로 만들어서 ImageView에 집어 넣는다.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profileIcon.setImageBitmap(bitmap);
                FirebaseHelper helper=new FirebaseHelper();
                helper.UploadImage(filePath, EditMyInformationActivity.this, ID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void ApplyChange() {
        String name=nameET.getText().toString();
        String email=emailET.getText().toString();
        int studentNumber=Integer.parseInt(studentNumberET.getText().toString());
        String password=passwordET.getText().toString();

        FirebaseHelper helper=new FirebaseHelper();
        helper.UpdateAccountData(ID, "name", name);
        helper.UpdateAccountData(ID, "email", email);
        helper.UpdateAccountData(ID, "major", major);
        helper.UpdateAccountData(ID, "StudentNumber", studentNumber);
        helper.UpdateAccountData(ID, "group", group);
        helper.UpdateAccountData(ID, "password", password);
        Alert alert=new Alert();
        alert.MsgDialogEnd("정보가 변경되었습니다", EditMyInformationActivity.this);
    }

    public void close(View v) {
        finish();
    }
}
