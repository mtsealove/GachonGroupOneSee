package kr.ac.gachon.www.GachonGroup.etc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.PhotoViewAttacher;

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
        String path=intent.getStringExtra("FilePath");

        if(profile) {
            FilePath="ProfileIcon/"+ID+"PF.png";
        } else FilePath=path;

        FirebaseImage firebaseImage=new FirebaseImage(FullScreenImageActivity.this);
        firebaseImage.LoadFullImageView(FilePath, imageView);
        PhotoViewAttacher photoViewAttacher=new PhotoViewAttacher(imageView);
        photoViewAttacher.setScaleType(ImageView.ScaleType.FIT_CENTER);
    }
}
