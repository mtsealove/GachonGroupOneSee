package kr.ac.gachon.www.GachonGroup.Board;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebasePost;
import kr.ac.gachon.www.GachonGroup.R;
import kr.ac.gachon.www.GachonGroup.modules.Alert;

public class AddPostActivity extends AppCompatActivity {
    private String boardName, userID, groupName;
    private String title, content, boardID;
    EditText titleET, contentET;
    Button commitBtn, tmpCommitBtn;
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

    }
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
    View.OnClickListener postListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FirebasePost firebasePost=new FirebasePost(AddPostActivity.this);
            firebasePost.Post(boardName, userID, title, content);
            finish();
        }
    };
    View.OnClickListener GroupPostListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FirebasePost firebasePost=new FirebasePost(AddPostActivity.this);
            firebasePost.Post(groupName, boardName, userID, title, content);
            finish();
        }
    };
    View.OnClickListener UpdateListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FirebasePost firebasePost=new FirebasePost(AddPostActivity.this);
            firebasePost.Update(boardName, title, content, boardID);
            finish();
        }
    };
    View.OnClickListener GroupUpdateListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FirebasePost firebasePost=new FirebasePost(AddPostActivity.this);
            firebasePost.Update(groupName, boardName, title, content, boardID);
            finish();
        }
    };
}
