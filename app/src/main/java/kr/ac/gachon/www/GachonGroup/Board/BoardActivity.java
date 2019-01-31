package kr.ac.gachon.www.GachonGroup.Board;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import kr.ac.gachon.www.GachonGroup.Account.EditMyInformationActivity;
import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseBoard;
import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseImage;
import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebasePost;
import kr.ac.gachon.www.GachonGroup.R;

public class BoardActivity extends AppCompatActivity {
    TextView authorTV, titleTV, contentTV, boardNameTV;
    Button functionBtn, replyBtn, removeBtn;
    LinearLayout ReplyShowLayout, ContentLayout;
    FrameLayout ReplyInputLayout;
    EditText replyET;

    private String userID, boardName, groupName;
    int id;
    FirebasePost firebasePost;
    FirebaseImage firebaseImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        authorTV= findViewById(R.id.authorTV);
        titleTV= findViewById(R.id.titleTV);
        contentTV= findViewById(R.id.contentTV);
        boardNameTV= findViewById(R.id.BoardNameTV);
        functionBtn=findViewById(R.id.functionBtn);
        removeBtn=findViewById(R.id.removeBtn);
        ContentLayout=findViewById(R.id.contentLayout);
        //댓글 출력 레이아웃
        ReplyShowLayout=findViewById(R.id.ShowReplyLayout);
        ReplyInputLayout=findViewById(R.id.InputReplyLayout);
        replyET=findViewById(R.id.replyET);
        replyBtn=findViewById(R.id.replyBtn);

        firebasePost=new FirebasePost(BoardActivity.this);
        firebaseImage=new FirebaseImage(BoardActivity.this);

        init();
    }

    private void init() {
        Intent intent=getIntent();
        boardName=intent.getStringExtra("boardName");
        String boardNameKR=null;
        id=intent.getIntExtra("id", 0);
        userID=intent.getStringExtra("userID");
        groupName=intent.getStringExtra("groupName");
        //게시판 이름에 따라 다르게 설정
        switch (boardName) {
            case "PublicRelation":
                boardNameKR="홍보게시판";
                functionBtn.setText("신고");
                functionBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Accuse();
                    }
                });
                break;
            case "FederationNotice":
                boardNameKR="연합회 공지사항";
                functionBtn.setText("수정");
                break;
            case "QnA":
                boardNameKR="Q&A";
                functionBtn.setText("신고");
                //댓글 기능 활성화
                ReplyInputLayout.setVisibility(View.VISIBLE);
                ReplyShowLayout.setVisibility(View.VISIBLE);
                //댓글 추가
                firebasePost.AddReply(ReplyShowLayout, boardName, id, userID);
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
            case "Information":
                boardNameKR="정보게시판";

                functionBtn.setText("신고");
                functionBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Accuse();
                    }
                });
                //댓글 기능 활성화
                ReplyInputLayout.setVisibility(View.VISIBLE);
                ReplyShowLayout.setVisibility(View.VISIBLE);
                //댓글 추가
                firebasePost.AddReply(groupName,ReplyShowLayout, boardName, id, userID);
                replyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputReply();
                    }
                });
            case "GroupQnA":
                boardNameKR="Q&A";
                functionBtn.setText("신고");
                functionBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Accuse();
                    }
                });
                ReplyShowLayout.setVisibility(View.VISIBLE);
                ReplyInputLayout.setVisibility(View.VISIBLE);
                //댓글 추가
                firebasePost.AddReply(groupName, ReplyShowLayout, boardName, id, userID);
                replyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputReply();
                    }
                });
        }
        boardNameTV.setText(boardNameKR);
        FirebaseBoard firebaseBoard=new FirebaseBoard(BoardActivity.this);
        if(groupName==null) {
            firebaseBoard.setTextViewBoard(authorTV, titleTV, contentTV, boardName, id);
            firebaseBoard.MyContent(boardName, Integer.toString(id), userID, functionBtn, removeBtn);
            firebaseImage.getBoardPhotos(boardName, Integer.toString(id), ContentLayout, contentTV);

        }
        else {
            firebaseBoard.setTextViewBoard(groupName, authorTV, titleTV, contentTV, boardName, id);
            firebaseBoard.MyContent(groupName, boardName, Integer.toString(id), userID, functionBtn, removeBtn);
        }
    }

    //댓글 입력
    private void InputReply() {
        String content=replyET.getText().toString();
        //입력 체크
        if(content.length()==0) Toast.makeText(BoardActivity.this, "댓글을 입력해 주세요", Toast.LENGTH_SHORT).show();
        else {
            //동아리의 게시판아니면
            if(groupName==null)
                firebasePost.CommitReply(boardName, Integer.toString(id), userID, content);
            else //동아리게시판 명시
                firebasePost.CommitReply(groupName,boardName, Integer.toString(id), userID, content);
            //댓글 입력창 초기화
            replyET.setText("");
            //키보드 내리기
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(replyET.getWindowToken(), 0);
        }
    }

    //신고 페이지로 이동
    private void Accuse() {
        Intent intent=new Intent(BoardActivity.this, AccuseActivity.class);
        intent.putExtra("groupName", groupName);
        intent.putExtra("boardName", boardName);
        intent.putExtra("userID", userID);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    public void close(View v) {
        finish();
    }

    @Override
    public void onResume(){
        super.onResume();
        init();
    }

}
