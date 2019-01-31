package kr.ac.gachon.www.GachonGroup.etc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseImage;
import kr.ac.gachon.www.GachonGroup.R;

public class FullScreenImageActivity extends AppCompatActivity {
    private String FilePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        ImageView imageView=findViewById(R.id.ImageView);

        Intent intent=getIntent();
        boolean profile=intent.getBooleanExtra("Profile", false);
        String ID=intent.getStringExtra("userID");

        if(profile) {
            FilePath="ProfileIcon/"+ID+"PF.png";
        }

        FirebaseImage firebaseImage=new FirebaseImage(FullScreenImageActivity.this);
        firebaseImage.LoadImageView(FilePath, imageView);
    }
}
