package kr.ac.gachon.www.GachonGroup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import kr.ac.gachon.www.GachonGroup.modules.FirebaseHelper;

public class GroupListActivity extends AppCompatActivity {
    TextView categoryTV;
    LinearLayout group_list_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        group_list_layout= findViewById(R.id.group_list_layout);
        //이전 액티비티에서 분과를 받아옴
        Intent intent=getIntent();
        String category=intent.getStringExtra("category");
        String categorykr=intent.getStringExtra("categorykr");
        String ID=intent.getStringExtra("ID");

        //분과 표시
        categoryTV= findViewById(R.id.Group_category);
        categoryTV.setText(categorykr);

        FirebaseHelper helper=new FirebaseHelper();
        helper.getGroupList(category, group_list_layout, GroupListActivity.this, ID);
    }
    public void back(View v) {
        onBackPressed();
    }
}
