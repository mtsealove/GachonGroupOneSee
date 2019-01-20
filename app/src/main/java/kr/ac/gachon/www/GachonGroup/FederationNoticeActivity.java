package kr.ac.gachon.www.GachonGroup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import kr.ac.gachon.www.GachonGroup.modules.FirebaseHelper;

public class FederationNoticeActivity extends AppCompatActivity {
    ListView boardLV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_federation_notice);
        boardLV= findViewById(R.id.boardLV);
        FirebaseHelper helper=new FirebaseHelper();
        helper.setListView(boardLV, "FederationNotice", FederationNoticeActivity.this);
    }
}
