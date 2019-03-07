package kr.ac.gachon.www.GachonGroup.Board;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;

import kr.ac.gachon.www.GachonGroup.Account.EditMyInformationActivity;
import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseImage;
import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebasePost;
import kr.ac.gachon.www.GachonGroup.R;
import kr.ac.gachon.www.GachonGroup.etc.Alert;

//게시글 작성 및 수정 클래스
public class AddPostActivity extends AppCompatActivity { //게시글 작성
    private String boardName, userID, groupName;
    private String title, content;
    public static String boardID;
    private EditText titleET, contentET;
    private Button commitBtn, tmpCommitBtn, imageBtn, clipBtn;
    private LinearLayout contentLayout;
    private LinearLayout.LayoutParams layoutParams;
    private final int MaxImage=5;
    private ArrayList<Uri> filePath=new ArrayList<>();
    private ArrayList<String> filePathStr;
    private ArrayList<String> removeFile=new ArrayList<>();
    private FirebasePost firebasePost;
    public static boolean temp=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        Intent intent=getIntent();
        //새 게시글 작성을 위한 기본정보
        boardName=intent.getStringExtra("boardName");
        userID=intent.getStringExtra("userID");
        //동아리용 게시판일 경우 사용
        groupName=intent.getStringExtra("groupName");
        //기존 게시글 업데이트용 추가정보
        title=intent.getStringExtra("title");
        content=intent.getStringExtra("content");
        boardID=intent.getStringExtra("boardID");
        filePathStr=intent.getStringArrayListExtra("FilePath");

        //뷰 매칭
        titleET=findViewById(R.id.titleET);
        contentET=findViewById(R.id.contentET);
        commitBtn=findViewById(R.id.commitBtn);
        tmpCommitBtn=findViewById(R.id.tmpCommitBtn);
        imageBtn=findViewById(R.id.imageBtn);
        clipBtn=findViewById(R.id.clipBtn);
        contentLayout=findViewById(R.id.contentLayout);

        //업데이트 시 제목과 내용 설정
        titleET.setText(title);
        contentET.setText(content);

        commitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Post();
            }
        });
        tmpCommitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TempPost();
            }
        });

        //동아리거나 홍보게시판이면 내용을 입력해주세요로 설정
        if(groupName!=null) contentET.setHint("내용을 입력해주세요");
        else if(boardName.equals("PublicRelation")) contentET.setHint("내용을 입력해주세요");

        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //이미지가 5장 이하일 경우 추가 가능
                if(filePath.size()<MaxImage)
                    EditImage();
                else Toast.makeText(AddPostActivity.this, "이미지는"+MaxImage+"장까지 첨부할 수 있습니다", Toast.LENGTH_SHORT).show();
            }
        });

        //화면에 추가될 이미지뷰들의 속성
        layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin=30;
        layoutParams.rightMargin=30;
        layoutParams.topMargin=10;
        layoutParams.bottomMargin=30;


        firebasePost = new FirebasePost(AddPostActivity.this);
        if(filePathStr!=null) LoadImages(); //업데이트인 경우 이미지 로드
        else {
            filePathStr=new ArrayList<>();
            //임시저장 확인
            if(groupName==null)firebasePost.CheckTempBoard(boardName, userID, titleET, contentET, contentLayout);   //일반용
            else firebasePost.CheckTempBoard(groupName, boardName, userID, titleET, contentET, contentLayout);  //동아리용
        }

    }

    //업데이트 시 원래 업로드 되었던 이미지 불러오기
    private void LoadImages() {
        FirebaseImage firebaseImage=new FirebaseImage(AddPostActivity.this);
        for(int i=0; i<filePathStr.size(); i++) {
            final ImageView imageView=new ImageView(AddPostActivity.this);
            firebaseImage.LoadImageView(filePathStr.get(i), imageView);
            Log.d("ImageFilePath", filePathStr.get(i));
            imageView.setLayoutParams(layoutParams);
            contentLayout.addView(imageView);

            final int finalI=i;
            //이미지를 길게 누르면 삭제 다이얼로그 출력
            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //진동 반응
                    Vibrator vibrator=(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(5);
                    Alert alert=new Alert(AddPostActivity.this);
                    alert.MsgDialogChoice("이미지를 삭제하시겠습니까?", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //이미지 제거 및 uri제거
                            contentLayout.removeView(imageView);
                            //제거 변수에 추가
                            removeFile.add(filePathStr.get(finalI));
                            Alert.dialog.cancel();
                        }
                    });
                    return false;
                }
            });
        }
    }

    //게시글 작성 메서드
    private void Post() {
        title=titleET.getText().toString();
        content=contentET.getText().toString();
        if(title.length()==0) Toast.makeText(AddPostActivity.this, "제목을 입력하세요", Toast.LENGTH_SHORT).show();
        else if(content.length()==0) Toast.makeText(AddPostActivity.this, "내용을 입력하세요", Toast.LENGTH_SHORT).show();
        else {
            Alert alert=new Alert(AddPostActivity.this);
            //동아리게시판||업데이트 여부에 따라 다른 메서드 설정
            if(boardID==null) {
                if (groupName == null)
                    alert.MsgDialogChoice("작성한 내용을\n등록하시겠습니까?", postListener);
                else
                    alert.MsgDialogChoice("작성한 내용을\n등록하시겠습니까?", GroupPostListener);
            } else {
                if(groupName==null)
                    alert.MsgDialogChoice("수정한 내용을\n등록하시겠습니까?", UpdateListener);
                else
                    alert.MsgDialogChoice("수정한 내용을\n등록하시겠습니까?", GroupUpdateListener);
            }
        }
    }
    //일반 게시판의 게시글을 작성하는 메서드를 가진 리스너
    View.OnClickListener postListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            firebasePost.Post(boardName, userID, title, content, filePath, false);
            finish();
        }
    };
    //동아리 게시판의 게시글을 작성하는 메서드를 가진 리스너
    View.OnClickListener GroupPostListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            firebasePost.Post(groupName, boardName, userID, title, content, filePath, false);
            finish();
        }
    };
    //일반 게시판의 게시글을 업데이트하는 메서드를 가진 리스너
    View.OnClickListener UpdateListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            firebasePost.Update(boardName, title, content, boardID, removeFile, filePath);
            finish();
        }
    };
    //동아리 게시판의 게시판을 업데이트하는 메서드를 가진 리스너
    View.OnClickListener GroupUpdateListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            firebasePost.Update(groupName, boardName, title, content, boardID, removeFile, filePath);
            finish();
        }
    };

    //이미지 삽입 메서드
    private void EditImage() { //이미지 업로드
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요"), 0);
    }
    //임시저장 메서드
    private void TempPost() {
        title=titleET.getText().toString();
        content=contentET.getText().toString();
        if(title.length()==0) Toast.makeText(AddPostActivity.this, "제목을 입력하세요", Toast.LENGTH_SHORT).show();
        else if(content.length()==0) Toast.makeText(AddPostActivity.this, "내용을 입력하세요", Toast.LENGTH_SHORT).show();
        else {
            Alert alert=new Alert(AddPostActivity.this);
            if(boardID==null) {
                if(groupName==null)
                    alert.MsgDialogChoice("작성한 내용을\n임시저장하시겠습니까?", TempPostListener);
                else
                    alert.MsgDialogChoice("작성한 내용을\n임시저장하시겠습니까?", TempGroupPostListener);
            }
        }
    }
    View.OnClickListener TempPostListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) { //일반 게시판의 게시글을 임시저장하는 리스너
            firebasePost.Post(boardName, userID, title, content, filePath, true);
            finish();
        }
    };
    View.OnClickListener TempGroupPostListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) { //동아리 게시판의 게시글을 임시저장하는 리스너
            firebasePost.Post(groupName, boardID, userID, title, content, filePath, true);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //결과가 있을 경우
        if(requestCode == 0 && resultCode == RESULT_OK){
            final Uri uri=data.getData();
            filePath.add(uri);
            try {
                //화면에 이미지 추가
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath.get(filePath.size()-1));
                final ImageView imageView=new ImageView(AddPostActivity.this);
                imageView.setImageBitmap(bitmap);
                imageView.setLayoutParams(layoutParams);
                contentLayout.addView(imageView);
                //이미지를 길게 누르면 삭제 다이얼로그 출력
                imageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        //진동 반응
                        Vibrator vibrator=(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(5);
                        Alert alert=new Alert(AddPostActivity.this);
                        alert.MsgDialogChoice("이미지를 삭제하시겠습니까?", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //이미지 제거 및 uri제거
                                contentLayout.removeView(imageView);
                                filePath.remove(uri);
                                Alert.dialog.cancel();
                            }
                        });
                        return false;
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
