package kr.ac.gachon.www.GachonGroup.Board;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import kr.ac.gachon.www.GachonGroup.Group.GroupQnAActivity;
import kr.ac.gachon.www.GachonGroup.R;

public class SearchActivity extends AppCompatActivity {
    TextView titleTV;
    Button searchBtn;
    EditText searchET;
    FederationNoticeActivity federationNoticeActivity;
    QnAActivity  QnAActivity;
    InformationBoardActivity InformationActivity;
    GroupQnAActivity GroupQnAActivity;
    PRBoardActivity prBoardActivity;
    private String BoardName, groupName, userID;
    private String userGroup;
    @Override
    protected void onCreate(Bundle si) {
        super.onCreate(si);
        setContentView(R.layout.activity_search);
        titleTV=findViewById(R.id.titleTV);
        searchBtn=findViewById(R.id.searchBtn);
        searchET=findViewById(R.id.searchET);
        searchET.requestFocus();

        //액티비티 종료를 위해 설정
        federationNoticeActivity=(FederationNoticeActivity)FederationNoticeActivity._FederationNoticeActivity;
        QnAActivity=(QnAActivity) kr.ac.gachon.www.GachonGroup.Board.QnAActivity._QnAActivity;
        InformationActivity=(InformationBoardActivity)InformationBoardActivity._InformationActivity;
        GroupQnAActivity=(GroupQnAActivity) kr.ac.gachon.www.GachonGroup.Group.GroupQnAActivity._GroupQnAActivity;
        prBoardActivity=(PRBoardActivity) PRBoardActivity.PRBActivity;

        //게시판 이름과 동아리 이름을 받아옴
        Intent intent=getIntent();
        BoardName=intent.getStringExtra("BoardName");
        groupName=intent.getStringExtra("groupName");
        userID=intent.getStringExtra("userID");
        userGroup=intent.getStringExtra("userGroup");
        String BoardNameKR=null;

        switch (BoardName) {
            case "FederationNotice":
                BoardNameKR="연합회 공지사항";
                break;
            case "QnA":
                BoardNameKR="Q&A";
                break;
            case "Information":
                BoardNameKR="정보게시판";
                break;
            case "GroupQnA":
                BoardNameKR="Q&A";
                break;
            case "PRBoard":
                BoardNameKR="홍보게시판";
                break;
        }
        titleTV.setText(BoardNameKR);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Search();
            }
        });
        //키보드 올리기
        InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        //엔터키로 검색 이벤트 수행
        searchET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        Search();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
    }

    //검색 버튼
    private void Search() {
        String value=searchET.getText().toString();
        if(value.length()==0) Toast.makeText(SearchActivity.this, "검색어를 입력해 주세요", Toast.LENGTH_SHORT).show();
        else {
            Intent intent;
            //게시판 제목에 따라 다른 활동 수행
            //검색을 위한 value값과 게시판 이름, 동아리 이름을 새 액티비티로 전송
            //이전에 존재했던 게시판 액티비티 종료
            switch (BoardName) {
                case "FederationNotice":
                    federationNoticeActivity.finish();
                    intent = new Intent(SearchActivity.this, FederationNoticeActivity.class);
                    intent.putExtra("value", value);
                    intent.putExtra("userID", userID);
                    intent.putExtra("group", userGroup);
                    startActivity(intent);
                    finish();
                    break;
                case "QnA":
                    QnAActivity.finish();
                    intent = new Intent(SearchActivity.this, QnAActivity.class);
                    intent.putExtra("value", value);
                    intent.putExtra("userID", userID);
                    startActivity(intent);
                    finish();
                    break;
                case "Information":
                    InformationActivity.finish();
                    intent = new Intent(SearchActivity.this, InformationBoardActivity.class);
                    intent.putExtra("value", value);
                    intent.putExtra("groupName", groupName);
                    intent.putExtra("ID", userID);
                    startActivity(intent);
                    finish();
                    break;
                case "GroupQnA":
                    GroupQnAActivity.finish();
                    intent = new Intent(SearchActivity.this, kr.ac.gachon.www.GachonGroup.Group.GroupQnAActivity.class);
                    intent.putExtra("value", value);
                    intent.putExtra("groupName", groupName);
                    intent.putExtra("ID", userID);
                    startActivity(intent);
                    finish();
                    break;
                case "PRBoard":
                    prBoardActivity.finish();
                    intent = new Intent(SearchActivity.this, PRBoardActivity.class);
                    intent.putExtra("value", value);
                    intent.putExtra("userID", userID);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    }
}
