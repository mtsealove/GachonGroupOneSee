package kr.ac.gachon.www.GachonGroup.Account;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;

import kr.ac.gachon.www.GachonGroup.Entity.Account;
import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseAccount;
import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseImage;
import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseView;
import kr.ac.gachon.www.GachonGroup.etc.FullScreenImageActivity;
import kr.ac.gachon.www.GachonGroup.R;
import kr.ac.gachon.www.GachonGroup.etc.Alert;

public class EditMyInformationActivity extends AppCompatActivity {
    private String ID;
    private Account account;
    private EditText nameET, IDET, emailET, studentNumberET, passwordET;
    private Spinner majorSP, groupSP;
    ImageView profileIcon;
    Button editBtn;
    FirebaseDatabase database;
    DatabaseReference reference;
    private String major, group;
    private FirebaseImage firebaseImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_my_information);
        nameET= findViewById(R.id.nameET);
        nameET.setEnabled(false);
        IDET= findViewById(R.id.IDET);
        IDET.setEnabled(false);
        emailET= findViewById(R.id.emailET);
        studentNumberET= findViewById(R.id.studentNumberET);
        passwordET= findViewById(R.id.PWET);
        majorSP= findViewById(R.id.major_SP);
        groupSP= findViewById(R.id.group_SP);
        profileIcon= findViewById(R.id.ProfileIcon);
        editBtn= findViewById(R.id.edit_btn);

        //아이디 가져와서
        Intent intent=getIntent();
        ID=intent.getStringExtra("ID");
        //다른 정보들을 EditText 또는 Spinner로 출력
        FirebaseView firebaseView=new FirebaseView(EditMyInformationActivity.this);
        firebaseView.setEditText("name", ID, nameET);
        firebaseView.setEditText("ID", ID, IDET);
        firebaseView.setEditText("email", ID, emailET);
        firebaseView.setEditText("StudentNumber", ID, studentNumberET);
        firebaseView.getAllGroupName(groupSP);
        firebaseView.setSpinnerMatch(groupSP, "group", ID);
        firebaseView.setSpinnerMatch(majorSP, "major", ID);
        firebaseView.setEditText("password", ID, passwordET);

        firebaseImage=new FirebaseImage(EditMyInformationActivity.this);
        firebaseImage.ShowProfileIcon(ID, profileIcon);

        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageClick();
            }
        });
        database=FirebaseDatabase.getInstance();
        reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                group=dataSnapshot.child("Account").child(ID).child("group").getValue(String.class);
                major=dataSnapshot.child("Account").child(ID).child("major").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        majorSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                major=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        groupSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                group=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplyChange();
            }
        });
        //checkAccuse(ID);
    }

    private void ImageClick() {
        AlertDialog.Builder builder=new AlertDialog.Builder(EditMyInformationActivity.this);
        ListView listView=new ListView(EditMyInformationActivity.this);
        ArrayList<String> arrayList=new ArrayList<>();
        arrayList.add("이미지 보기");
        arrayList.add("편집");
        ArrayAdapter arrayAdapter=new ArrayAdapter(EditMyInformationActivity.this, R.layout.support_simple_spinner_dropdown_item, arrayList);
        listView.setAdapter(arrayAdapter);

        builder.setView(listView);
        final AlertDialog dialog=builder.create();
        dialog.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.cancel();
                switch (position) {
                    case 0:
                        Intent intent=new Intent(EditMyInformationActivity.this, FullScreenImageActivity.class);
                        intent.putExtra("userID", ID);
                        intent.putExtra("Profile", true);
                        startActivity(intent);
                        break;
                    case 1:
                        EditImage();
                        break;
                }
            }
        });
    }

    private Uri filePath;
    private void EditImage() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "프로필 이미지를 선택하세요"), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //request코드가 0이고 OK를 선택했고 data에 뭔가가 들어 있다면
        if(requestCode == 0 && resultCode == RESULT_OK){
            filePath = data.getData();
            Log.d("EditMyInformation", "uri:" + String.valueOf(filePath));
            try {
                //Uri 파일을 Bitmap으로 만들어서 ImageView에 집어 넣는다.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profileIcon.setImageBitmap(bitmap);
                FirebaseImage firebaseImage=new FirebaseImage(EditMyInformationActivity.this);
                firebaseImage.UploadProfileImage(filePath, ID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void ApplyChange() {
        String name=nameET.getText().toString();
        String email=emailET.getText().toString();
        int studentNumber=Integer.parseInt(studentNumberET.getText().toString());
        String password=passwordET.getText().toString();

        FirebaseAccount firebaseAccount=new FirebaseAccount(EditMyInformationActivity.this);
        firebaseAccount.UpdateAccountData(ID, "name", name);
        firebaseAccount.UpdateAccountData(ID, "email", email);
        firebaseAccount.UpdateAccountData(ID, "major", major);
        firebaseAccount.UpdateAccountData(ID, "StudentNumber", studentNumber);
        firebaseAccount.UpdateAccountData(ID, "group", group);
        firebaseAccount.UpdateAccountData(ID, "password", password);
        Alert alert=new Alert(EditMyInformationActivity.this);
        alert.MsgDialogEnd("정보가 변경되었습니다");
    }

    public void close(View v) {
        finish();
    }

    //신고 체크, 신고당했을 경우 정보 변경 비활성화
    private void checkAccuse(final String userID) {
        final FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference reference=database.getReference().child("Accuse");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    String BoardID=null, BoardName=null, ReplyID=null, GroupName=null;
                    BoardID=snapshot.child("BoardID").getValue(String.class);
                    BoardName=snapshot.child("BoardName").getValue(String.class);
                    try {
                        GroupName=snapshot.child("GroupName").getValue(String.class);
                    } catch (Exception e) {
                    }
                    try {
                        ReplyID=snapshot.child("ReplyID").getValue(String.class);
                    } catch (Exception e) {
                    }
                    DatabaseReference resultRef;
                    //동아리 게시물이 아닐 경우
                    if(GroupName==null) {
                        if(ReplyID==null) { //댓글이 아닐 경우
                            resultRef=database.getReference().child(BoardName).child(BoardID);
                            resultRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.child("author").getValue(String.class).equals(userID))
                                        DisableEdit();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        } else { //댓글일 경우
                            resultRef=database.getReference().child(BoardName).child(BoardID).child("reply");
                            resultRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(DataSnapshot snapshot:dataSnapshot.getChildren()) {
                                        if(snapshot.child("author").getValue(String.class).equals(userID))
                                            DisableEdit();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                        //동아리 게시물일 경우
                    } else {
                        if(ReplyID==null) { //댓글이 아닐 경우
                            resultRef=database.getReference().child("Groups").child(GroupName).child(BoardName).child(BoardID);
                            resultRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.child("author").getValue(String.class).equals(userID))
                                        DisableEdit();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        } else { //댓글일 경우
                            resultRef=database.getReference().child("Groups").child(GroupName).child(BoardName).child(BoardID).child("reply");
                            resultRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                                        if(snapshot.child("author").getValue(String.class).equals(userID))
                                            DisableEdit();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    boolean disabled=false;
    private void DisableEdit() {
        if(!disabled) {
            disabled=true;
            Alert alert=new Alert(EditMyInformationActivity.this);
            alert.MsgDialog("신고당한 계정은 정보를 수정할 수 없습니다");

            nameET.setEnabled(false);
            IDET.setEnabled(false);
            emailET.setEnabled(false);
            studentNumberET.setEnabled(false);
            passwordET.setEnabled(false);
            majorSP.setEnabled(false);
            groupSP.setEnabled(false);
            profileIcon.setClickable(false);
        }
    }
}
