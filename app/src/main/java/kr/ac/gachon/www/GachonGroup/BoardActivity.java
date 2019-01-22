package kr.ac.gachon.www.GachonGroup;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import kr.ac.gachon.www.GachonGroup.modules.FirebaseHelper;

public class BoardActivity extends AppCompatActivity {
    TextView authorTV, titleTV, contentTV, boardNameTV;
    Button functionBtn;
    LinearLayout ReplyShowLayout;
    FrameLayout ReplyInputLayout;
    EditText replyET;
    Button replyBtn;
    private String userID, boardName;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        authorTV= findViewById(R.id.authorTV);
        titleTV= findViewById(R.id.titleTV);
        contentTV= findViewById(R.id.contentTV);
        boardNameTV= findViewById(R.id.BoardNameTV);
        functionBtn=findViewById(R.id.functionBtn);
        //댓글 출력 레이아웃
        ReplyShowLayout=findViewById(R.id.ShowReplyLayout);
        ReplyInputLayout=findViewById(R.id.InputReplyLayout);
        replyET=findViewById(R.id.replyET);
        replyBtn=findViewById(R.id.replyBtn);


        FirebaseHelper helper=new FirebaseHelper();

        Intent intent=getIntent();
        boardName=intent.getStringExtra("boardName");
        String boardNameKR=null;
        id=intent.getIntExtra("id", 0);
        userID=intent.getStringExtra("userID");
        switch (boardName) {
            case "PublicRelation":
                boardNameKR="홍보게시판";
                functionBtn.setText("신고");
                break;
            case "FederationNotice":
                boardNameKR="연합회 공지사항";
                functionBtn.setText("수정");
                break;
            case "QnA":
                boardNameKR="Q&A";
                functionBtn.setText("신고");
                ReplyInputLayout.setVisibility(View.VISIBLE);
                ReplyShowLayout.setVisibility(View.VISIBLE);
                helper.AddReply(ReplyShowLayout, boardName, id, BoardActivity.this);
                replyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputReply();
                    }
                });
                functionBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Accuse();
                    }
                });
                break;
        }
        boardNameTV.setText(boardNameKR);
        helper.setTextViewBoard(authorTV, titleTV, contentTV, boardName, id);
    }

    private void InputReply() {
        String content=replyET.getText().toString();
        if(content.length()==0) Toast.makeText(BoardActivity.this, "댓글을 입력해 주세요", Toast.LENGTH_SHORT).show();
        else {
            FirebaseHelper helper=new FirebaseHelper();
            helper.CommitReply(boardName, Integer.toString(id), userID, content);
            replyET.setText("");
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(replyET.getWindowToken(), 0);
        }
    }

    private void Accuse() {
        Intent intent=new Intent(BoardActivity.this, AccuseActivity.class);
        intent.putExtra("boardName", boardName);
        intent.putExtra("userID", userID);
        intent.putExtra("id", id);
        startActivity(intent);
    }
}
