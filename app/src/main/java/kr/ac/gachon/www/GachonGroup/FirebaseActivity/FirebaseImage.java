package kr.ac.gachon.www.GachonGroup.FirebaseActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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

import java.io.InputStream;
import java.util.ArrayList;


public class FirebaseImage  extends AppGlideModule {
    final Context context;
    RequestOptions requestOptions;
    FirebaseDatabase database;
    public FirebaseImage(Context context) {
        this.context=context;
        requestOptions=new RequestOptions()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE);
        database=FirebaseDatabase.getInstance();
    }

    //프로필 이미지 업로드
    public void UploadProfileImage(Uri filePath,  String ID) {
        //업로드할 파일이 있으면 수행
        if (filePath != null) {
            //업로드 진행 Dialog 보이기
            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("업로드중...");
            progressDialog.show();

            //storage
            FirebaseStorage storage = FirebaseStorage.getInstance();

            //Unique한 파일명을 만들자.

            String filename =ID + "PF.png";
            //storage 주소와 폴더 파일명을 지정해 준다.
            StorageReference storageRef = storage.getReferenceFromUrl("gs://gachongrouponesee.appspot.com").child("ProfileIcon/" + filename);
            //올라가거라...
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
                            @SuppressWarnings("VisibleForTests") //이걸 넣어 줘야 아랫줄에 에러가 사라진다. 넌 누구냐?
                                    double progress = (100 * taskSnapshot.getBytesTransferred()) /  taskSnapshot.getTotalByteCount();
                            //dialog에 진행률을 퍼센트로 출력해 준다
                            progressDialog.setMessage("사진 업로드 중 " + ((int) progress) + "% ...");
                        }
                    });
        } else {
            Toast.makeText(context, "파일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
        }
    }
    //게시판 사진 업로드
    public void UploadBoardImage(final Uri filePath,  final String boardName, final String boardID, int count) {
        //업로드할 파일이 있으면 수행
        if (filePath != null) {

            //storage
            FirebaseStorage storage = FirebaseStorage.getInstance();

            final DatabaseReference reference=database.getReference().child(boardName).child(boardID).child("Photos");
            reference.push().child("FilePath").setValue(boardName+"/"+boardID+"/"+Integer.toString(count)+".png");
                    //storage 주소와 폴더 파일명을 지정해 준다.
                    StorageReference storageRef = storage.getReferenceFromUrl("gs://gachongrouponesee.appspot.com").child(boardName+"/"+boardID+"/"+Integer.toString(count)+".png");
                    //올라가거라...
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
                                    @SuppressWarnings("VisibleForTests") //이걸 넣어 줘야 아랫줄에 에러가 사라진다. 넌 누구냐?
                                            double progress = (100 * taskSnapshot.getBytesTransferred()) /  taskSnapshot.getTotalByteCount();
                                    //dialog에 진행률을 퍼센트로 출력해 준다
                                }
                            });
        } else {
            Toast.makeText(context, "파일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        registry.append(StorageReference.class, InputStream.class, new FirebaseImageLoader.Factory());
    }

    //프로필 아이콘 표시
    public void ShowProfileIcon(String ID, ImageView imageView) {
        String FilePath ="ProfileIcon/"+ID+"PF.png";
        FirebaseStorage fs=FirebaseStorage.getInstance();
        StorageReference imageRef=fs.getReference().child(FilePath);

        Glide.with(context)
                .load(imageRef)
                .apply(requestOptions)
                .into(imageView);
        imageView.setBackground(new ShapeDrawable(new OvalShape()));
        if(Build.VERSION.SDK_INT>=21) imageView.setClipToOutline(true);
    }

    //이미지를 전체화면으로 표시
    public void LoadImageView(String FilePath, ImageView imageView) {
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
                LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.leftMargin=30;
                layoutParams.rightMargin=30;
                layoutParams.topMargin=10;
                layoutParams.bottomMargin=30;
                //게시글에 있는 모든 사진의 이름을 불러오기
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    String FilePath=snapshot.child("FilePath").getValue(String.class);
                    ImageView photo=new ImageView(context);
                    photo.setLayoutParams(layoutParams);
                    LoadImageView(FilePath, photo);
                    photos.add(photo);
                }
                for(int i=0; i<photos.size(); i++) {
                    layout.addView(photos.get(i));
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
                LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.leftMargin=30;
                layoutParams.rightMargin=30;
                layoutParams.topMargin=10;
                layoutParams.bottomMargin=30;
                //게시글에 있는 모든 사진의 이름을 불러오기
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    String FilePath=snapshot.child("FilePath").getValue(String.class);
                    ImageView photo=new ImageView(context);
                    photo.setLayoutParams(layoutParams);
                    LoadImageView(FilePath, photo);
                    photos.add(photo);
                }
                for(int i=0; i<photos.size(); i++) {
                    layout.addView(photos.get(i));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
