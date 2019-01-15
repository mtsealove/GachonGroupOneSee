package kr.ac.gachon.www.GachonGroup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import kr.ac.gachon.www.GachonGroup.modules.FirebaseHelper;

public class MyInformationActivity extends AppCompatActivity {
    TextView nameTV, groupTV;

    Account account;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_information);
        nameTV=(TextView)findViewById(R.id.nameTV);
        groupTV=(TextView)findViewById(R.id.groupTV);

        account=new Account();
        Intent intent=getIntent();
        String ID=intent.getStringExtra("ID");
        FirebaseHelper helper=new FirebaseHelper();

        helper.setTextView("name", ID, nameTV);
        helper.setTextView("group", ID, groupTV);
    }
}
