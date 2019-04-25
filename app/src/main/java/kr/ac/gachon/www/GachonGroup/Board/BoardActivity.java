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
import kr.ac.gachon.www.GachonGroup.Account.LoginActivity;
import kr.ac.gachon.www.GachonGroup.Entity.Account;
import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseAccount;
import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseBoard;
import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseImage;
import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebasePost;
import kr.ac.gachon.www.GachonGroup.R;

public class BoardActivity extends AppCompatActivity { //게시글 글 보기 액티비티
    TextView authorTV, titleTV, contentTV, boardNameTV, timeTV;
    Button functionBtn, replyBtn, removeBtn, noteBtn;
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

        //뷰 매칭
        authorTV= findViewById(R.id.authorTV);
        titleTV= findViewById(R.id.titleTV);
        contentTV= findViewById(R.id.contentTV);
        boardNameTV= findViewById(R.id.BoardNameTV);
        functionBtn=findViewById(R.id.functionBtn);
        removeBtn=findViewById(R.id.removeBtn);
        ContentLayout=findViewById(R.id.contentLayout);
        timeTV=findViewById(R.id.timeTV);
        noteBtn=findViewById(R.id.noteBtn);
        //댓글 출력 레이아웃
        ReplyShowLayout=findViewById(R.id.ShowReplyLayout);
        ReplyInputLayout=findViewById(R.id.InputReplyLayout);
        replyET=findViewById(R.id.replyET);
        replyBtn=findViewById(R.id.replyBtn);

        firebasePost=new FirebasePost(BoardActivity.this);
        firebaseImage=new FirebaseImage(BoardActivity.this);

        init();
    }

    private void init() { //초기화
        Intent intent=getIntent();
        boardName=intent.getStringExtra("boardName"); //게시판 이름 수신
        String boardNameKR=null;
        id=intent.getIntExtra("id", 0); //게시판 번호 수신
        userID=intent.getStringExtra("userID"); //사용자 ID수신
        groupName=intent.getStringExtra("groupName"); //동아리 이름 수신

        //게시판 이름에 따라 다르게 설정
        switch (boardName) {
            case "PublicRelation":
                boardNameKR="홍보게시판";
                functionBtn.setVisibility(View.VISIBLE);
                functionBtn.setText("신고"); //신고버튼 활성화
                functionBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Accuse();
                    }
                });
                break;
            case "FederationNotice":
                boardNameKR="연합회 공지사항";
                functionBtn.setVisibility(View.GONE);
                break;
            case "QnA":
                boardNameKR="Q&A";
                functionBtn.setVisibility(View.VISIBLE);
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
                functionBtn.setVisibility(View.VISIBLE);
                functionBtn.setText("신고"); //신고 기능 활성화
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
                break;
            case "GroupQnA":
                boardNameKR="Q&A";
                functionBtn.setVisibility(View.VISIBLE);
                functionBtn.setText("신고");
                functionBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Accuse();
                    }
                });
                //댓글 기능 활성화
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
                break;
            case "PublicNotice":
                boardNameKR="공지사항";
                noteBtn.setVisibility(View.GONE);   //공지사항에는 쪽지 봇보내게
                break;
        }

        boardNameTV.setText(boardNameKR);
        FirebaseBoard firebaseBoard=new FirebaseBoard(BoardActivity.this);
        //동아리게시판인지 확인하고 해당하는 내용 불러오기
        if(groupName==null) { //동아리 게시판이 아닐 경우
            firebaseBoard.setTextViewBoard(authorTV, titleTV, contentTV, boardName, id, timeTV); //게시물 불러오기
            firebaseBoard.MyContent(boardName, Integer.toString(id), userID, functionBtn, removeBtn, noteBtn); //자신의 글인지 확인
            firebaseImage.getBoardPhotos(boardName, Integer.toString(id), ContentLayout, contentTV); //해당하는 사진 불러오기
        } else { //동아리 게시판일 경우
            firebaseBoard.setTextViewBoard(groupName, authorTV, titleTV, contentTV, boardName, id, timeTV); //게시물 불러오기
            firebaseBoard.MyContent(groupName, boardName, Integer.toString(id), userID, functionBtn, removeBtn, noteBtn); //자신의 글인지 확인
            firebaseImage.getBoardPhotos(groupName, boardName, Integer.toString(id), ContentLayout, contentTV); //해당하는 사진 불러오기
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
                firebasePost.CommitReply(boardName, Integer.toString(id), userID, content); //댓글 입력
            else //동아리게시판 명시
                firebasePost.CommitReply(groupName,boardName, Integer.toString(id), userID, content); //댓글 입력
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
        //게시글의 모든 정보 전송
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
