package kr.ac.gachon.www.GachonGroup.Group;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseView;
import kr.ac.gachon.www.GachonGroup.R;

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

        FirebaseView firebaseView=new FirebaseView(GroupListActivity.this);
        firebaseView.getGroupList(category, group_list_layout, ID);
    }
    public void back(View v) {
        onBackPressed();
    }
}
