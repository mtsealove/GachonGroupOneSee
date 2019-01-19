package kr.ac.gachon.www.GachonGroup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import kr.ac.gachon.www.GachonGroup.modules.FirebaseHelper;

public class BoardActivity extends AppCompatActivity {
    TextView authorTV, titleTV, contentTV, boardNameTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        authorTV= findViewById(R.id.authorTV);
        titleTV= findViewById(R.id.titleTV);
        contentTV= findViewById(R.id.contentTV);
        boardNameTV= findViewById(R.id.BoardNameTV);

        Intent intent=getIntent();
        String boardName=intent.getStringExtra("boardName");
        String boardNameKR=null;
        switch (boardName) {
            case "PublicRelation":
                boardNameKR="홍보게시판";
                break;
        }
        boardNameTV.setText(boardNameKR);
        int id=intent.getIntExtra("id", 0);

        FirebaseHelper helper=new FirebaseHelper();
        helper.setTextViewBoard(authorTV, titleTV, contentTV, boardName, id);
    }
}
