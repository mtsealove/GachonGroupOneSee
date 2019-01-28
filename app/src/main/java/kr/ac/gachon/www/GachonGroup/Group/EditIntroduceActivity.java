package kr.ac.gachon.www.GachonGroup.Group;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebasePost;
import kr.ac.gachon.www.GachonGroup.R;
import kr.ac.gachon.www.GachonGroup.modules.Alert;

public class EditIntroduceActivity extends AppCompatActivity {
    private ImageView imageView;
    private EditText locationET, introduceET;
    private Button functionBtn;
    private String location, introduce, group;
    @Override
    protected void onCreate(Bundle si) {
        super.onCreate(si);
        setContentView(R.layout.activity_edit_introduce);

        locationET=findViewById(R.id.locationET);
        introduceET=findViewById(R.id.introduceET);
        functionBtn=findViewById(R.id.functionBtn);

        Intent intent=getIntent();
        location=intent.getStringExtra("location");
        introduce=intent.getStringExtra("introduce");
        group=intent.getStringExtra("groupName");

        locationET.setText(location);
        introduceET.setText(introduce);

        if(location==null&&introduce==null) {
            functionBtn.setText("등록하기");
            functionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setFunctionBtn(false);
                }
            });
        }
        else {
            functionBtn.setText("수정하기");
            functionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setFunctionBtn(true);
                }
            });
        }
    }
    private void setFunctionBtn(boolean update) {
        location=locationET.getText().toString();
        introduce=introduceET.getText().toString();

        if(location.length()==0) Toast.makeText(EditIntroduceActivity.this, "동아리방을 입력해 주세요", Toast.LENGTH_SHORT).show();
        else if(introduce.length()==0) Toast.makeText(EditIntroduceActivity.this, "내용을 입력해 주세요", Toast.LENGTH_SHORT).show();
        else {
            String msg;
            if (update) msg="작성한 내용을\n수정하시겠습니까?";
            else msg="작성한 내용을\n등록하시겠습니까?";
            Alert alert=new Alert(EditIntroduceActivity.this);
            alert.MsgDialogChoice(msg, IntroduceListener);
        }
    }
    View.OnClickListener IntroduceListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FirebasePost firebasePost=new FirebasePost(EditIntroduceActivity.this);
            firebasePost.SetIntroduce(group, location, introduce);
            finish();
        }
    };

    public void close(View v) {
        finish();
    }
}
