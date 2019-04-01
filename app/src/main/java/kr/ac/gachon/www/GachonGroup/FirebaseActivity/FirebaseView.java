package kr.ac.gachon.www.GachonGroup.FirebaseActivity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import kr.ac.gachon.www.GachonGroup.Account.AES256Util;
import kr.ac.gachon.www.GachonGroup.Group.GroupMenuActivity;
import kr.ac.gachon.www.GachonGroup.R;

public class FirebaseView extends AppCompatActivity {   //firebase를 이용한 개별 View 수정 클래스
    final Context context;
    FirebaseDatabase database;

    public FirebaseView(Context context) {
        this.context=context;
        database=FirebaseDatabase.getInstance();
    }

    //텍스트뷰의 내용을 받아와 수정
    public void setTextView(final String child, final String ID, final TextView textView) { //계정에서 찾아 표시
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String result=dataSnapshot.child("Account").child(ID).child(child).getValue(String.class);
                Boolean isManager=dataSnapshot.child("Account").child(ID).child("is_manager").getValue(Boolean.class);
                if(child.equals("name")) result+=" 님";  //이름일 경우 "님"을 붙임
                else if(isManager) result+=" 임원";   //관리자일 경우 동아리 이름 뒤 임원 붙임
                textView.setText(result);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //EditText의 내용을 수정
    public void setEditText(final String child, final String ID, final EditText editText) { //계정에서 찾아 표시
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!child.equals("StudentNumber")) {    //학번이 아닐 경우 문자열로 받음
                    String result = dataSnapshot.child("Account").child(ID).child(child).getValue(String.class);
                    if(child.equals("password")) {  //비밀번호인 경우 복호화
                        try {
                            AES256Util aes256Util=new AES256Util();
                            result=aes256Util.decrypt(aes256Util.decrypt(result));
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (GeneralSecurityException e) {
                            e.printStackTrace();
                        }
                    }
                    editText.setText(result);
                } else {
                    int studentNumber=dataSnapshot.child("Account").child(ID).child("StudentNumber").getValue(Integer.class);   //학번인 경우 정수형으로 받음
                    editText.setText(Integer.toString(studentNumber));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //특정 분야의 동아리 목록 표시
    public void getGroupList(final String category, final LinearLayout layout, final String ID) {
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.child("Groups").getChildren()) {
                    //해당 카테고리의 데이터 삽입
                    if(snapshot.child("category").getValue(String.class).equals(category)) {
                        final String groupName=snapshot.child("name").getValue(String.class);
                        LayoutInflater inflater=LayoutInflater.from(context);
                        View sub_group=inflater.inflate(R.layout.sub_group_list, null);

                        //이름과 설명 설정
                        TextView groupNameTV= sub_group.findViewById(R.id.group_name);
                        TextView groupDescriptionTV= sub_group.findViewById(R.id.group_description);
                        LinearLayout btn= sub_group.findViewById(R.id.linearBtn);

                        groupNameTV.setText(groupName);
                        groupDescriptionTV.setText(snapshot.child("description").getValue(String.class));
                        //화면에 추가
                        layout.addView(sub_group);

                        //클릭으로 이동 메서드 추가
                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent groupMenu=new Intent();
                                groupMenu.setClass(context, GroupMenuActivity.class);
                                groupMenu.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                String Group=groupName.replace(".", "");    //DB특성 상 .제거
                                groupMenu.putExtra("groupName",Group);
                                groupMenu.putExtra("ID", ID);
                                context.startActivity(groupMenu);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //모든 동아리 이름 가져와서 spinner에 설정
    public void getAllGroupName(final Spinner spinner) {
        final ArrayList<String> arrayList=new ArrayList<>();
        arrayList.add("동아리 선택");    //기본으로 표시될 내용
        arrayList.add("동아리 없음");    //없을 수도 있으니까
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.child("Groups").getChildren()) {
                    String name=snapshot.child("name").getValue(String.class);  //이름 표시
                    arrayList.add(name);
                }
                ArrayAdapter<String> adapter=new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item, arrayList);
                spinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //자신의 것(전공/동아리)과 일치하는 값을 스피너에 선택
    public void setSpinnerMatch(final Spinner spinner, final String child, final String ID) {
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value=dataSnapshot.child("Account").child(ID).child(child).getValue(String.class);   //자신의 것
                ArrayAdapter arrayAdapter= (ArrayAdapter) spinner.getAdapter();
                int position=arrayAdapter.getPosition(value);   //값 비교
                spinner.setSelection(position); //선택
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //child기반으로 Textview 설정
    public void setStringTextView(final TextView textView, final String child1, final String child2, final String child3, final String alt) {
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                textView.setText(alt);  //대체 텍스트, 없을 수도 있으니까
                try {
                    String value = dataSnapshot.child(child1).child(child2).child(child3).getValue(String.class);
                    textView.setText(value);
                } catch (DatabaseException e) {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //소개글에서 동아리 이름에 따라 버튼 텍스트 설정
    public void setButtonText(final String groupName, final Button button) {
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String introduce=dataSnapshot.child("Groups").child(groupName).child("introduce").getValue(String.class);   //동아리의 소개글
                if(introduce==null) {   //등록하지 않았다면
                    button.setText("등록하기"); //등록
                } else {
                    button.setText("수정하기"); //이미 존재하면 수정
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
