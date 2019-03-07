package kr.ac.gachon.www.GachonGroup.Group;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseView;
import kr.ac.gachon.www.GachonGroup.R;

public class GroupListActivity extends AppCompatActivity {  //특정 분과에 속한 동아리 출력
    TextView categoryTV;
    LinearLayout group_list_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        group_list_layout= findViewById(R.id.group_list_layout);
        //이전 액티비티에서 분과를 받아옴
        Intent intent=getIntent();
        String category=intent.getStringExtra("category");  //영문 이름
        String categorykr=intent.getStringExtra("categorykr");  //한글 이름
        String ID=intent.getStringExtra("ID");  //사용자 ID

        //분과 표시
        categoryTV= findViewById(R.id.Group_category);
        categoryTV.setText(categorykr);

        FirebaseView firebaseView=new FirebaseView(GroupListActivity.this);
        firebaseView.getGroupList(category, group_list_layout, ID); //분과 소속 동아리 리스트 출력
    }
    public void back(View v) {
        onBackPressed();
    }
}
