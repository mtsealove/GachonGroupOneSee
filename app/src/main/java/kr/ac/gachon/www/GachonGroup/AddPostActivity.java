package kr.ac.gachon.www.GachonGroup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import kr.ac.gachon.www.GachonGroup.modules.Alert;
import kr.ac.gachon.www.GachonGroup.modules.FirebaseHelper;

public class AddPostActivity extends AppCompatActivity {
    private String boardName, userID;
    EditText titleET, contentET;
    Button commitBtn, tmpCommitBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        Intent intent=getIntent();
        boardName=intent.getStringExtra("boardName");
        userID=intent.getStringExtra("userID");

        titleET=findViewById(R.id.titleET);
        contentET=findViewById(R.id.contentET);
        commitBtn=findViewById(R.id.commitBtn);
        tmpCommitBtn=findViewById(R.id.tmpCommitBtn);

        commitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Post();
            }
        });
    }


    private String title, content;
    private void Post() {
        title=titleET.getText().toString();
        content=contentET.getText().toString();
        if(title.length()==0) Toast.makeText(AddPostActivity.this, "제목을 입력하세요", Toast.LENGTH_SHORT).show();
        else if(content.length()==0) Toast.makeText(AddPostActivity.this, "내용을 입력하세요", Toast.LENGTH_SHORT).show();
        else {
            Alert alert=new Alert();
            alert.MsgDialogChoice("작성한 내용을\n등록하시겠습니까?", AddPostActivity.this, postListener);
        }
    }
    View.OnClickListener postListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FirebaseHelper helper=new FirebaseHelper();
            helper.Post(boardName, userID, title, content);
            finish();
        }
    };
}
