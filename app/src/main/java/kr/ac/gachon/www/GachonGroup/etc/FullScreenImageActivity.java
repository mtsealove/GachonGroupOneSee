package kr.ac.gachon.www.GachonGroup.etc;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.PhotoViewAttacher;

import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseImage;
import kr.ac.gachon.www.GachonGroup.R;

public class FullScreenImageActivity extends AppCompatActivity { //전체화면 사진 액티비티
    private String FilePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        ImageView imageView=findViewById(R.id.ImageView);

        Intent intent=getIntent();
        boolean profile=intent.getBooleanExtra("Profile", false); //프로필 사진 여부
        String ID=intent.getStringExtra("userID"); //사용자 ID
        String path=intent.getStringExtra("FilePath"); //파일 경로

        if(profile) { //프로필 사진일 경우
            FilePath="ProfileIcon/"+ID+"PF.png"; //프로필 사진 경로로 설정
        } else FilePath=path; //임의의 위치 지정

        FirebaseImage firebaseImage=new FirebaseImage(FullScreenImageActivity.this);
        firebaseImage.LoadFullImageView(FilePath, imageView); //전체 크기로 이미지 불러오기

        //사진 핀치줌 지정
        PhotoViewAttacher photoViewAttacher=new PhotoViewAttacher(imageView);
        photoViewAttacher.setScaleType(ImageView.ScaleType.FIT_XY);

        //상단바 색상 검은색
        View view=getWindow().getDecorView();
        if(Build.VERSION.SDK_INT>=21) {
            if(view!=null) {
                getWindow().setStatusBarColor(Color.BLACK);
            }
        }

    }
}
