package kr.ac.gachon.www.GachonGroup.Group;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseImage;
import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebasePost;
import kr.ac.gachon.www.GachonGroup.R;
import kr.ac.gachon.www.GachonGroup.etc.Alert;
import kr.ac.gachon.www.GachonGroup.etc.FullScreenImageActivity;

public class EditGroupIntroduceActivity extends AppCompatActivity {  //동아리 소개글 수정 액티비티
    private ImageView GroupIcon;
    private EditText locationET, introduceET;
    private Button functionBtn;
    private String location, introduce, group;
    private String resultMsg, FilePath;
    private FirebaseImage firebaseImage;
    @Override
    protected void onCreate(Bundle si) {
        super.onCreate(si);
        setContentView(R.layout.activity_edit_group_introduce);

        locationET=findViewById(R.id.locationET);
        introduceET=findViewById(R.id.introduceET);
        functionBtn=findViewById(R.id.functionBtn);
        GroupIcon=findViewById(R.id.GroupIcon);

        //이미 이전에 등록된 데이터가 있으면 데이터 받아오기
        Intent intent=getIntent();
        location=intent.getStringExtra("location");
        introduce=intent.getStringExtra("introduce");
        group=intent.getStringExtra("groupName");

        locationET.setText(location);
        introduceET.setText(introduce);

        if(location==null&&introduce==null) {   //이전에 등록한 적 없다면
            functionBtn.setText("등록하기");
            functionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setFunctionBtn(false);
                }
            }); //등록할 수 있게
            resultMsg="등록이 완료되었습니다";
        }
        else {
            functionBtn.setText("수정하기");    //이전에 등록한 적이 있다면 수정할 수 있게
            functionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setFunctionBtn(true);
                }
            });
            resultMsg="수정이 완료되었습니다";
        }

        //동아리 아이콘 설정

        setGroupIcon();
    }
    //동아리 아이콘 누르기
    private void setGroupIcon() {
        firebaseImage=new FirebaseImage(EditGroupIntroduceActivity.this);
        FilePath="Groups/"+group+"/"+group+"Icon.png";  //동아리 프로필 저장경로
        firebaseImage.LoadImageView(FilePath, GroupIcon);   //경로를 통해 불러오기

        GroupIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(EditGroupIntroduceActivity.this);
                ListView listView=new ListView(EditGroupIntroduceActivity.this);
                ArrayList<String> arrayList=new ArrayList<>();
                arrayList.add("이미지 보기");    //보기 가능
                arrayList.add("편집");    //바꾸기 가능
                ArrayAdapter adapter=new ArrayAdapter(EditGroupIntroduceActivity.this, R.layout.support_simple_spinner_dropdown_item, arrayList);
                listView.setAdapter(adapter);
                builder.setView(listView);
                final AlertDialog dialog=builder.create();
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        dialog.cancel();
                        switch (position) {
                            case 0:
                                Intent intent=new Intent(EditGroupIntroduceActivity.this, FullScreenImageActivity.class);
                                intent.putExtra("FilePath", FilePath);  //전체화면으로 이미지 표시
                                startActivity(intent);
                                break;
                            case 1:
                                EditImage(); //이미지 수정
                                break;
                        }
                    }
                });
                dialog.show();
            }
        });
    }

    private void setFunctionBtn(boolean update) {   //수정 또는 등록 버튼, 업데이트 여부 판단
        location=locationET.getText().toString();
        introduce=introduceET.getText().toString();

        if(location.length()==0) Toast.makeText(EditGroupIntroduceActivity.this, "동아리방을 입력해 주세요", Toast.LENGTH_SHORT).show();
        else if(introduce.length()==0) Toast.makeText(EditGroupIntroduceActivity.this, "내용을 입력해 주세요", Toast.LENGTH_SHORT).show();
        else {
            String msg; //등록/수정에 따라 다른 메세지
            if (update) msg="작성한 내용을\n수정하시겠습니까?";
            else msg="작성한 내용을\n등록하시겠습니까?";
            Alert alert=new Alert(EditGroupIntroduceActivity.this);
            alert.MsgDialogChoice(msg, IntroduceListener);
        }
    }
    View.OnClickListener IntroduceListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FirebasePost firebasePost=new FirebasePost(EditGroupIntroduceActivity.this);
            firebasePost.SetIntroduce(group, location, introduce);  //동아리 정보 수정
            Toast.makeText(EditGroupIntroduceActivity.this, resultMsg, Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    private Uri filePath;
    private void EditImage() {  //이미지 불러오기
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "동아리 프로필 이미지를 선택하세요"), 0);
    }

    //파일을 불러 온 다음에
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //request코드가 0이고 OK를 선택했고 data에 뭔가가 들어 있다면
        if(requestCode == 0 && resultCode == RESULT_OK){
            filePath = data.getData();
            Log.d("EditMyInformation", "uri:" + String.valueOf(filePath));
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                GroupIcon.setImageBitmap(bitmap);   //화면에 표시
                FirebaseImage firebaseImage=new FirebaseImage(EditGroupIntroduceActivity.this);
                firebaseImage.UploadGroupImage(filePath, group);    //파일 업로드
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void close(View v) {
        finish();
    }
}
