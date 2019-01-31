package kr.ac.gachon.www.GachonGroup.Board;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import kr.ac.gachon.www.GachonGroup.Account.EditMyInformationActivity;
import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseImage;
import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebasePost;
import kr.ac.gachon.www.GachonGroup.R;
import kr.ac.gachon.www.GachonGroup.etc.Alert;

public class AddPostActivity extends AppCompatActivity {
    private String boardName, userID, groupName;
    private String title, content, boardID;
    private EditText titleET, contentET;
    private Button commitBtn, tmpCommitBtn, imageBtn, clipBtn;
    private LinearLayout contentLayout;
    private LinearLayout.LayoutParams layoutParams;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        Intent intent=getIntent();
        //새 게시글 작성을 위한 기본정보
        boardName=intent.getStringExtra("boardName");
        userID=intent.getStringExtra("userID");
        //동아리용 게시판일 경우 사용
        groupName=intent.getStringExtra("groupName");
        //기존 게시글 업데이트용 추가정보
        title=intent.getStringExtra("title");
        content=intent.getStringExtra("content");
        boardID=intent.getStringExtra("boardID");

        titleET=findViewById(R.id.titleET);
        contentET=findViewById(R.id.contentET);
        commitBtn=findViewById(R.id.commitBtn);
        tmpCommitBtn=findViewById(R.id.tmpCommitBtn);
        imageBtn=findViewById(R.id.imageBtn);
        clipBtn=findViewById(R.id.clipBtn);
        contentLayout=findViewById(R.id.contentLayout);

        titleET.setText(title);
        contentET.setText(content);

        commitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Post();
            }
        });
        if(groupName!=null) contentET.setHint("내용을 입력해주세요");
        else if(boardName.equals("PublicRelation")) contentET.setHint("내용을 입력해주세요");

        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditImage();
            }
        });

        layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin=30;
        layoutParams.rightMargin=30;
        layoutParams.topMargin=10;
        layoutParams.bottomMargin=30;
    }

    //게시글 작성 메서드
    private void Post() {
        title=titleET.getText().toString();
        content=contentET.getText().toString();
        if(title.length()==0) Toast.makeText(AddPostActivity.this, "제목을 입력하세요", Toast.LENGTH_SHORT).show();
        else if(content.length()==0) Toast.makeText(AddPostActivity.this, "내용을 입력하세요", Toast.LENGTH_SHORT).show();
        else {
            Alert alert=new Alert(AddPostActivity.this);
            if(boardID==null) {
                if (groupName == null)
                    alert.MsgDialogChoice("작성한 내용을\n등록하시겠습니까?", postListener);
                else
                    alert.MsgDialogChoice("작성한 내용을\n등록하시겠습니까?", GroupPostListener);
            } else {
                if(groupName==null)
                    alert.MsgDialogChoice("수정한 내용을\n등록하시겠습니까?", UpdateListener);
                else
                    alert.MsgDialogChoice("수정한 내용을\n등록하시겠습니까?", GroupUpdateListener);
            }
        }
    }
    //일반 게시판의 게시글을 작성하는 메서드를 가진 리스너
    View.OnClickListener postListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FirebasePost firebasePost=new FirebasePost(AddPostActivity.this);
            firebasePost.Post(boardName, userID, title, content, filePath);
            finish();
        }
    };
    //동아리 게시판의 게시글을 작성하는 메서드를 가진 리스너
    View.OnClickListener GroupPostListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FirebasePost firebasePost=new FirebasePost(AddPostActivity.this);
            firebasePost.Post(groupName, boardName, userID, title, content);
            finish();
        }
    };
    //일반 게시판의 게시글을 업데이트하는 메서드를 가진 리스너
    View.OnClickListener UpdateListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FirebasePost firebasePost=new FirebasePost(AddPostActivity.this);
            firebasePost.Update(boardName, title, content, boardID);
            finish();
        }
    };
    //동아리 게시판의 게시판을 업데이트하는 메서드를 가진 리스너
    View.OnClickListener GroupUpdateListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FirebasePost firebasePost=new FirebasePost(AddPostActivity.this);
            firebasePost.Update(groupName, boardName, title, content, boardID);
            finish();
        }
    };

    private ArrayList<Uri> filePath=new ArrayList<>();
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
            filePath.add( data.getData());
            Log.d("EditMyInformation", "uri:" + String.valueOf(filePath));
            try {
                //Uri 파일을 Bitmap으로 만들어서 ImageView에 집어 넣는다.
                contentLayout.removeAllViews();
                contentLayout.addView(contentET);
                for(int i=0; i<filePath.size(); i++) {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath.get(i));
                    ImageView imageView=new ImageView(AddPostActivity.this);
                    imageView.setImageBitmap(bitmap);
                    imageView.setLayoutParams(layoutParams);
                    contentLayout.addView(imageView);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
