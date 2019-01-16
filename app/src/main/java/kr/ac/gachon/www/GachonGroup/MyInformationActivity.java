package kr.ac.gachon.www.GachonGroup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import kr.ac.gachon.www.GachonGroup.modules.FirebaseHelper;

public class MyInformationActivity extends AppCompatActivity {
    TextView nameTV, groupTV;
    Button EditInfoBtn;
    ImageView profileIcon;
    private String ID;

    Account account;
    FirebaseHelper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_information);
        nameTV=(TextView)findViewById(R.id.nameTV);
        groupTV=(TextView)findViewById(R.id.groupTV);
        EditInfoBtn=(Button)findViewById(R.id.EditInfoBtn);
        profileIcon=(ImageView)findViewById(R.id.userIcon);

        account=new Account();
        Intent intent=getIntent();
        ID=intent.getStringExtra("ID");
        helper=new FirebaseHelper();

        helper.setTextView("name", ID, nameTV);
        helper.setTextView("group", ID, groupTV);

        EditInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Edit_information();
            }
        });
    }

    //정보 수정 활동
    private void Edit_information() {
        Intent intent=new Intent(MyInformationActivity.this, EditMyInformationActivity.class);
        intent.putExtra("ID", ID);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        helper.setTextView("name", ID, nameTV);
        helper.setTextView("group", ID, groupTV);
        super.onResume();
    }
}
