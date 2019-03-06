package kr.ac.gachon.www.GachonGroup.etc;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;

import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseImageLoader;

@GlideModule
public class MyGlideModule extends AppGlideModule { //이미지 라이브러리 Glide를 사용하기위한 클래스
    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        registry.append(StorageReference.class, InputStream.class, new FirebaseImageLoader.Factory());
    }
}


