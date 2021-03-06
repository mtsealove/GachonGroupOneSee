package kr.ac.gachon.www.GachonGroup.FirebaseActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import kr.ac.gachon.www.GachonGroup.R;
import kr.ac.gachon.www.GachonGroup.etc.FullScreenImageActivity;

public class FirebaseImage  extends AppGlideModule {    //firebase와 glide를 이용하여 이미지 표시
    final Context context;
    RequestOptions requestOptions;
    RequestOptions profileOptions;
    FirebaseDatabase database;
    public FirebaseImage(Context context) {
        this.context=context;
        profileOptions=new RequestOptions() //프로필 사진 옵션
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
        .placeholder(R.drawable.user_icon)
        .error(R.drawable.user_icon);

        requestOptions=new RequestOptions() //일반 사진 옵션
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.loadimage);

        database=FirebaseDatabase.getInstance();
    }

    //프로필 이미지 업로드
    public void UploadProfileImage(Uri filePath,  String ID) {
        //업로드할 파일이 있으면 수행
        if (filePath != null) {
            //업로드 진행 Dialog 보이기
            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("업로드중...");
            progressDialog.setCancelable(false);    //취소 불가
            progressDialog.show();

            //storage
            FirebaseStorage storage = FirebaseStorage.getInstance();

            String filename =ID + "PF.png"; //파일 이름
            StorageReference storageRef = storage.getReferenceFromUrl("gs://gachongrouponesee.appspot.com").child("ProfileIcon/" + filename); //업로드할 위치
            //올라가거라...
            storageRef.putFile(filePath)
                    //성공시
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {   //성공
                            progressDialog.dismiss(); //업로드 진행 Dialog 상자 닫기
                            Toast.makeText(context, "업로드 완료!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //실패시
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {   //실패
                            progressDialog.dismiss();
                            Toast.makeText(context, "업로드 실패!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //진행중
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {  //
                            @SuppressWarnings("VisibleForTests")
                                    double progress = (100 * taskSnapshot.getBytesTransferred()) /  taskSnapshot.getTotalByteCount();
                            //진행률 표시
                            progressDialog.setMessage("사진 업로드 중 " + ((int) progress) + "% ...");
                        }
                    });
        } else {
            Toast.makeText(context, "파일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
        }
    }

    //동아리 게시판 이미지 업로드
    public void UploadGroupImage(Uri filePath,  String Group) {
        //업로드할 파일이 있으면 수행
        if (filePath != null) {
            //업로드 진행 Dialog 보이기
            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("업로드중...");
            progressDialog.setCancelable(false);    //취소 불가
            progressDialog.show();
            //storage
            FirebaseStorage storage = FirebaseStorage.getInstance();

            String filename ="Groups/"+Group+"/"+Group+"Icon.png";  //파일 위치, 이름
            StorageReference storageRef = storage.getReferenceFromUrl("gs://gachongrouponesee.appspot.com").child(filename);

            storageRef.putFile(filePath)
                    //성공시
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss(); //업로드 진행 Dialog 상자 닫기
                            Toast.makeText(context, "업로드 완료!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //실패시
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "업로드 실패!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //진행중
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests")
                                    double progress = (100 * taskSnapshot.getBytesTransferred()) /  taskSnapshot.getTotalByteCount();
                            //dialog에 진행률을 퍼센트로 출력해 준다
                            progressDialog.setMessage("사진 업로드 중 " + ((int) progress) + "% ...");
                        }
                    });
        } else {
            Toast.makeText(context, "파일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
        }
    }
    //일반 게시판 사진 업로드
    public void UploadBoardImage(final Uri filePath,  final String boardName, final String boardID, int count) {
        //업로드할 파일이 있으면 수행
        if (filePath != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            //업로드 진행 Dialog 보이기
            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("업로드중...");
            progressDialog.setCancelable(false);    //취소 불가
            progressDialog.show();

            final DatabaseReference reference=database.getReference().child(boardName).child(boardID).child("Photos");
            reference.push().child("FilePath").setValue(boardName+"/"+boardID+"/"+Integer.toString(count)+".png");
                    //storage 주소와 폴더 파일명을 지정해 준다.
                    StorageReference storageRef = storage.getReferenceFromUrl("gs://gachongrouponesee.appspot.com").child(boardName+"/"+boardID+"/"+Integer.toString(count)+".png");
                    storageRef.putFile(filePath)
                            //성공시
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    progressDialog.dismiss();
                                    Toast.makeText(context, "업로드 완료!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            //실패시
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(context, "업로드 실패!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            //진행중
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    @SuppressWarnings("VisibleForTests")
                                            double progress = (100 * taskSnapshot.getBytesTransferred()) /  taskSnapshot.getTotalByteCount();
                                    progressDialog.setMessage("사진 업로드 중 "+((int)progress+"%..."));
                                }
                            });
        } else {
            Toast.makeText(context, "파일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
        }
    }

    //임시 게시판 사진 업로드(일반 게시판)
    public void UploadTempBoardImage(final Uri filePath, final String ID, int count, final DatabaseReference TempRef) {
        //업로드할 파일이 있으면 수행
        if (filePath != null) {
            //storage
            FirebaseStorage storage = FirebaseStorage.getInstance();
            //업로드 진행 Dialog 보이기
            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("업로드중...");
            progressDialog.setCancelable(false);    //취소 불가
            progressDialog.show();

            String FilePath="Temp/"+ID+"/"+count+".png";

            TempRef.child("Photos").push().child("FilePath").setValue(FilePath);
            final StorageReference storageRef = storage.getReferenceFromUrl("gs://gachongrouponesee.appspot.com").child(FilePath);

            storageRef.putFile(filePath)
                    //성공시
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "업로드 완료!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //실패시
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "업로드 실패!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //진행중
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests")
                                    double progress = (100 * taskSnapshot.getBytesTransferred()) /  taskSnapshot.getTotalByteCount();
                            //dialog에 진행률을 퍼센트로 출력해 준다
                            progressDialog.setMessage("사진 업로드 중 "+((int)progress)+"%...");
                        }
                    });
        } else {
            Toast.makeText(context, "파일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
        }
    }

    //동아리 게시판 사진 업로드(동아리 게시판)
    public void UploadBoardImage(final Uri filePath, final String GroupName,  final String boardName, final String boardID, int count) {
        //업로드할 파일이 있으면 수행
        if (filePath != null) {

            //storage
            FirebaseStorage storage = FirebaseStorage.getInstance();

            //업로드 진행 Dialog 보이기
            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("업로드중...");
            progressDialog.setCancelable(false);    //취소 불가
            progressDialog.show();

            final DatabaseReference reference=database.getReference().child("Groups").child(GroupName).child(boardName).child(boardID).child("Photos");
            reference.push().child("FilePath").setValue("Groups/"+GroupName+"/"+boardName+"/"+boardID+"/"+Integer.toString(count)+".png");
            StorageReference storageRef = storage.getReferenceFromUrl("gs://gachongrouponesee.appspot.com").child("Groups/"+GroupName+"/"+boardName+"/"+boardID+"/"+Integer.toString(count)+".png");

            storageRef.putFile(filePath)
                    //성공시
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(context, "업로드 완료!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //실패시
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "업로드 실패!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //진행중
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests")
                                    double progress = (100 * taskSnapshot.getBytesTransferred()) /  taskSnapshot.getTotalByteCount();
                            //dialog에 진행률을 퍼센트로 출력해 준다
                        }
                    });
        } else {
            Toast.makeText(context, "파일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {   //firebase storage를 glide에 표시하기 위해
        registry.append(StorageReference.class, InputStream.class, new FirebaseImageLoader.Factory());
    }

    //프로필 아이콘 표시
    public void ShowProfileIcon(String ID, ImageView imageView) {
        String FilePath ="ProfileIcon/"+ID+"PF.png";    //프로필 사진 경로
        FirebaseStorage fs=FirebaseStorage.getInstance();
        StorageReference imageRef=fs.getReference().child(FilePath);

        Glide.with(context)
                .load(imageRef)
                .apply(profileOptions)
                .thumbnail(0.3f)    //0.3배율
                .into(imageView);
        imageView.setBackground(new ShapeDrawable(new OvalShape()));
        if(Build.VERSION.SDK_INT>=21) imageView.setClipToOutline(true);
    }

    //이미지를 0.5배율로 표시||속도 향상
    public void LoadImageView(String FilePath, ImageView imageView) {
        FirebaseStorage fs=FirebaseStorage.getInstance();
        StorageReference reference=fs.getReference().child(FilePath);
        Glide.with(context)
                .load(reference)
                .apply(requestOptions)
                .thumbnail(0.5f)    //0.5 배율
                .into(imageView);
    }

    //이미지를 전체크기로 표시
    public void LoadFullImageView(String FilePath, ImageView imageView) {
        FirebaseStorage fs=FirebaseStorage.getInstance();
        StorageReference reference=fs.getReference().child(FilePath);
        Glide.with(context)
                .load(reference)
                .apply(requestOptions)
                .into(imageView);
    }

    //일반 게시판에 해당하는 사진 표시
    public void getBoardPhotos(final String BoardName, final String BoardID, final LinearLayout layout, final TextView contentTV) {
        final FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference=database.getReference().child(BoardName).child(BoardID).child("Photos");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //모든 뷰를 제거 후
                layout.removeAllViews();
                //텍스트 삽입
                layout.addView(contentTV);
                ArrayList<ImageView> photos=new ArrayList<>();
                //이미지뷰 속성
                LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.leftMargin=30;
                layoutParams.rightMargin=30;
                layoutParams.topMargin=10;
                layoutParams.bottomMargin=30;
                //게시글에 있는 모든 사진의 경로를 불러오기
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    final String FilePath=snapshot.child("FilePath").getValue(String.class);
                    ImageView photo=new ImageView(context);
                    photo.setLayoutParams(layoutParams);    //속성 적용
                    photo.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {    //길게 누르면
                            Vibrator vibrator=(Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(5);
                            Intent intent=new Intent(context, FullScreenImageActivity.class);   //전체화면 액티비티
                            intent.putExtra("FilePath", FilePath);  //파일 경로 전송
                            context.startActivity(intent);  //실행
                            return false;
                        }
                    });
                    LoadImageView(FilePath, photo); //이미지 불러오기
                    photos.add(photo);  //이미지뷰 리스트에 추가
                }
                for(int i=0; i<photos.size(); i++) {
                    layout.addView(photos.get(i));  //화면에 추가
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //동아리 게시판에 해당하는 사진 표시
    public void getBoardPhotos(final String groupName, final String BoardName, final String BoardID, final LinearLayout layout, final TextView contentTV) {
        final FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference=database.getReference().child("Groups").child(groupName).child(BoardName).child(BoardID).child("Photos");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //모든 뷰를 제거 후
                layout.removeAllViews();
                //텍스트 삽입
                layout.addView(contentTV);
                ArrayList<ImageView> photos=new ArrayList<>();
                //이미지뷰 속성
                LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.leftMargin=30;
                layoutParams.rightMargin=30;
                layoutParams.topMargin=10;
                layoutParams.bottomMargin=30;
                //게시글에 있는 모든 사진의 경로를 불러오기
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    final String FilePath=snapshot.child("FilePath").getValue(String.class);
                    ImageView photo=new ImageView(context);
                    photo.setLayoutParams(layoutParams);
                    photo.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {    //길게 누르면
                            Vibrator vibrator=(Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(5);
                            Intent intent=new Intent(context, FullScreenImageActivity.class);   //전체화면 액티비티
                            intent.putExtra("FilePath", FilePath);  //파일 경로 전송
                            context.startActivity(intent);  //실행
                            return false;
                        }
                    });
                    LoadImageView(FilePath, photo); //이미지 불러오기
                    photos.add(photo);  //리스트에 추가
                }
                for(int i=0; i<photos.size(); i++) {
                    layout.addView(photos.get(i));  //화면에 추가
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //임시 저장 일반 게시판에 해당하는 사진 표시
    public void getTempBoardPhotos(final LinearLayout layout, final String FilePath) {
                LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.leftMargin=30;
                layoutParams.rightMargin=30;
                layoutParams.topMargin=10;
                layoutParams.bottomMargin=30;
                //게시글에 있는 모든 사진의 이름을 불러오기
                ImageView photo=new ImageView(context);
                LoadImageView(FilePath, photo);
                layout.addView(photo);
    }
}
