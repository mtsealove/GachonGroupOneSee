package kr.ac.gachon.www.GachonGroup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class SearchActivity extends AppCompatActivity {
    TextView titleTV;
    Button searchBtn;
    EditText searchET;
    FederationNoticeActivity federationNoticeActivity;
    QnAActivity  QnAActivity;
    InformationBoardActivity InformationActivity;
    private String BoardName, groupName, userID;
    @Override
    protected void onCreate(Bundle si) {
        super.onCreate(si);
        setContentView(R.layout.activity_search);
        titleTV=findViewById(R.id.titleTV);
        searchBtn=findViewById(R.id.searchBtn);
        searchET=findViewById(R.id.searchET);

        federationNoticeActivity=(FederationNoticeActivity)FederationNoticeActivity._FederationNoticeActivity;
        QnAActivity=(QnAActivity) kr.ac.gachon.www.GachonGroup.QnAActivity._QnAActivity;
        InformationActivity=(InformationBoardActivity)InformationBoardActivity._InformationActivity;

        Intent intent=getIntent();
        BoardName=intent.getStringExtra("BoardName");
        groupName=intent.getStringExtra("groupName");
        userID=intent.getStringExtra("userID");
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
        }
        titleTV.setText(BoardNameKR);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Search();
            }
        });
    }
    private void Search() {
        String value=searchET.getText().toString();
        Intent intent;
        switch (BoardName) {
            case "FederationNotice":
                federationNoticeActivity.finish();
                intent=new Intent(SearchActivity.this, FederationNoticeActivity.class);
                intent.putExtra("value", value);
                startActivity(intent);
                finish();
                break;
            case "QnA":
                QnAActivity.finish();
                intent=new Intent(SearchActivity.this, QnAActivity.class);
                intent.putExtra("value", value);
                startActivity(intent);
                finish();
                break;
            case "Information":
                InformationActivity.finish();
                intent=new Intent(SearchActivity.this, InformationBoardActivity.class);
                intent.putExtra("value", value);
                intent.putExtra("groupName", groupName);
                intent.putExtra("ID", userID);
                startActivity(intent);
                finish();
                break;
        }
    }
}
