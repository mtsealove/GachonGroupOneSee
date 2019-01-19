package kr.ac.gachon.www.GachonGroup.modules;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

import kr.ac.gachon.www.GachonGroup.Account;
import kr.ac.gachon.www.GachonGroup.Calendar.EventdayDecorator;
import kr.ac.gachon.www.GachonGroup.FindIdActivity;
import kr.ac.gachon.www.GachonGroup.LoginActivity;
import kr.ac.gachon.www.GachonGroup.PRBoardActivity;
import kr.ac.gachon.www.GachonGroup.R;
import kr.ac.gachon.www.GachonGroup.SignUpActivity;

import static android.app.PendingIntent.getActivity;

public class FirebaseHelper extends Activity{
    FirebaseDatabase database;
    GmailSender gmailSender=new GmailSender("werqtt18@gmail.com", "kffdmoebguyivmyh");

    public FirebaseHelper() {
        database=FirebaseDatabase.getInstance();
    }
    //ID 찾기
    public void Check_ID_Reuse(final String ID, final boolean[] reuse, final Context context) {
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Account").child(ID).exists()) {
                    Alert alert=new Alert();
                    alert.MsgDialog("중복된 아이디가 있습니다.\n다른 아이디를 입력해주세요", context);
                    reuse[0]=false;
                } else {
                    reuse[0]=true;
                    Toast.makeText(context, "아이디를 사용할 수 있습니다", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void Check_Email_Reuse(final String email, final boolean[] reuse, final Context context) {
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean reuseb=true;
                for(DataSnapshot snapshot: dataSnapshot.child("Account").getChildren()) {
                    if(snapshot.child("email").getValue(String.class).equals(email)) {
                        reuseb=false;
                    }
                }
                if(!reuseb) {
                    reuse[0]=false;
                    Alert alert=new Alert();
                    alert.MsgDialog("이미 사용중인 이메일입니다", context);
                } else {

                    SignUpActivity.verify_code=gmailSender.CreateVerifyCode();
                    String msg = "가천대학교 동아리 한눈에 보자 회원가입 인증번호는 " + SignUpActivity.verify_code + " 입니다";
                    //GMailSender.sendMail(제목, 본문내용, 보내는사람, 받는사람);
                    try {
                        gmailSender.sendMail("가천대학교 동아리 한눈에 보자 회원가입 인증메일입니다", msg, "werqtt18@gmail.com", email);
                            VerifyCode(context);
                     } catch (SendFailedException e) {
                    Toast.makeText(getApplicationContext(), "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                } catch (MessagingException e) {
                    Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주십시오", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //인증번호 확인 메서드
    int time;

    private void VerifyCode(final Context context) {
        time=300;
        //레이아웃 inflate
        LayoutInflater inflater=LayoutInflater.from(context);
        View layout=inflater.inflate(R.layout.dialog_verify_code, null);
        //각 뷰 매칭
        final TextView timeTV= layout.findViewById(R.id.timeTV);
        final EditText verifyET= layout.findViewById(R.id.verify_code_ET);
        Button okay= layout.findViewById(R.id.okay);

        //남은 시간 표시
        TimerTask timerTask=new TimerTask() {
            @Override
            public void run() {
                if(time<0) Toast.makeText(context, "시간이 만료되었습니다", Toast.LENGTH_SHORT).show();
                else {
                    time--;
                    int min = time / 60;
                    int sec = time % 60;
                    timeTV.setText(min + "분 " + sec + "초 남음");
                }
            }
        };
        Timer timer=new Timer();
        timer.schedule(timerTask, 0, 1000);

        //다이얼로그 생성
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setView(layout);
        final AlertDialog dialog=builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        dialog.show();

        //확인 버튼
        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newCode=verifyET.getText().toString();
                if(time<0) Toast.makeText(context,"시간이 만료되었습니다", Toast.LENGTH_SHORT).show();
                else if(newCode.equals(SignUpActivity.verify_code)) {
                    Toast.makeText(context, "인증이 완료되었습니다", Toast.LENGTH_SHORT).show();
                    SignUpActivity.Verified=true;
                    dialog.cancel();
                    SignUpActivity.emailET.setEnabled(false);
                } else Toast.makeText(context, "인증번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
            }
        });

    }
    //계정 생성 메서드
    public void CreateAccount(final Account account) {
        final String ID=account.ID;
        final DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.child("Manager").getChildren()) {
                    if(snapshot.child("phone").getValue(String.class).equals(account.phone))
                        account.is_manager=true;
                }
                reference.child("Account").child(ID).setValue(account);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    //아이디 찾기 인증번호 발송 메서드
    public void Find_ID_mail(final String email, final Context context) {
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String ID=null;
                for(DataSnapshot snapshot:dataSnapshot.child("Account").getChildren()) {
                    if (snapshot.child("email").getValue(String.class).equals(email)) {
                        ID=snapshot.child("ID").getValue(String.class);
                        break;
                    }
                }
                if(ID==null) Toast.makeText(context, "존재하지 않는 이메일입니다", Toast.LENGTH_SHORT).show();
                else {
                    String verifyCode=gmailSender.CreateVerifyCode();
                    try {
                        gmailSender.sendMail("가천대학교 동아리 한눈에보자 ID 찾기 인증번호",
                                "가천대학교 동아리 ID 찾기 인증번호는 "+verifyCode+"입니다.",
                                "werqtt18@gmail.com",
                                email);
                        Toast.makeText(context, "인증번호가 발송되었습니다", Toast.LENGTH_SHORT).show();
                        FindIdActivity.VerifyCode=verifyCode;
                        FindIdActivity.ID=ID;
                        FindIdActivity.check_4_ID_btn.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //비밀번호 찾기 메서드
    public void Find_PW_mail(final String email, final String ID, final Context context) {
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String PW=null;
                for(DataSnapshot snapshot: dataSnapshot.child("Account").getChildren()) {
                    if(snapshot.child("ID").getValue(String.class).equals(ID)) {
                        if(snapshot.child("email").getValue(String.class).equals(email)) {
                            PW=snapshot.child("password").getValue(String.class);
                            break;
                        } else Toast.makeText(context, "메일 주소가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                    }
                }
                if(PW==null) Toast.makeText(context, "ID가 존재하지 않습니다", Toast.LENGTH_SHORT).show();
                else {
                    String verifyCode=gmailSender.CreateVerifyCode();
                    try {
                        gmailSender.sendMail("가천대학교 동아리 한눈에보자 비밀번호 찾기 인증번호",
                                "가천대학교 동아리 비밀번호 찾기 인증번호는 "+verifyCode+" 입니다.",
                                "werqtt18@gmail.com",
                                email);
                        Toast.makeText(context, "인증번호가 발송되었습니다" ,Toast.LENGTH_SHORT).show();
                        FindIdActivity.VerifyCode=verifyCode;
                        FindIdActivity.password=PW;
                        FindIdActivity.check_4_PW_btn.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //로그인 메서드
    public void Login(final String ID, final String PW, final boolean isManager, final Context context) {
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean find=false;
                for(DataSnapshot snapshot: dataSnapshot.child("Account").getChildren()) {
                    if(snapshot.child("ID").getValue(String.class).equals(ID)) {
                        find=true;
                        if(snapshot.child("password").getValue(String.class).equals(PW)) {
                            if(isManager) {
                                if(snapshot.child("is_manager").getValue(Boolean.class)==isManager) {
                                    String name=snapshot.child("name").getValue(String.class);
                                    String email=snapshot.child("email").getValue(String.class);
                                    String major=snapshot.child("major").getValue(String.class);
                                    int StudentNumber=snapshot.child("StudentNumber").getValue(Integer.class);
                                    String group=snapshot.child("group").getValue(String.class);
                                    String phone=snapshot.child("phone").getValue(String.class);
                                    LoginActivity.account=new Account(name, ID, email, major, StudentNumber, group, PW, phone, isManager);
                                    moveHome(ID, context);
                                } else Toast.makeText(context, "관리자 계정이 아닙니다", Toast.LENGTH_SHORT).show();
                            } else {
                                String name=snapshot.child("name").getValue(String.class);
                                String email=snapshot.child("email").getValue(String.class);
                                String major=snapshot.child("major").getValue(String.class);
                                int StudentNumber=snapshot.child("StudentNumber").getValue(Integer.class);
                                String group=snapshot.child("group").getValue(String.class);
                                String phone=snapshot.child("phone").getValue(String.class);
                                LoginActivity.account=new Account(name, ID, email, major, StudentNumber, group, PW, phone, isManager);
                                moveHome(ID, context);
                            }
                        } else Toast.makeText(context, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                    }
                }
                if(!find) Toast.makeText(context, "일치하는 ID를 찾지 못했습니다", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void GetAccount(final String ID, final Account account) {
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name=dataSnapshot.child("Account").child(ID).child("name").getValue(String.class);
                String email=dataSnapshot.child("Account").child(ID).child("email").getValue(String.class);
                String major=dataSnapshot.child("Account").child(ID).child("major").getValue(String.class);
                int StudentNumber=dataSnapshot.child("Account").child(ID).child("StudentNumber").getValue(Integer.class);
                String group=dataSnapshot.child("Account").child(ID).child("group").getValue(String.class);
                String password=dataSnapshot.child("Account").child(ID).child("password").getValue(String.class);
                String phone=dataSnapshot.child("Account").child(ID).child("phone").getValue(String.class);
                boolean is_manager=dataSnapshot.child("Account").child(ID).child("is_manager").getValue(Boolean.class);
                account.CopyAccount(new Account(name, ID, email, major, StudentNumber, group, password, phone, is_manager));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //홈화면 이동
    private void moveHome(String ID, Context context) {
        Intent home=new Intent();
        home.putExtra("ID", ID);
        home.setClass(context, kr.ac.gachon.www.GachonGroup.HomeActivity.class);
        home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(home);

        ((Activity)context).finish();
    }
    //텍스트뷰의 내용을 받아와 수정
    public void setTextView(final String child, final String ID, final TextView textView) {
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String result=dataSnapshot.child("Account").child(ID).child(child).getValue(String.class);
                if(child.equals("name")) result+=" 님";
                textView.setText(result);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //EditText의 내용을 수정
    public void setEditText(final String child, final String ID, final EditText editText) {
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!child.equals("StudentNumber")) {
                    String result = dataSnapshot.child("Account").child(ID).child(child).getValue(String.class);
                    editText.setText(result);
                } else {
                    int studentNumber=dataSnapshot.child("Account").child(ID).child("StudentNumber").getValue(Integer.class);
                    editText.setText(Integer.toString(studentNumber));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //특정 분야의 동아리 목록 표시
    public void getGroupList(final String category, final LinearLayout layout, final Context context) {
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.child("Groups").getChildren()) {
                    //해당 카테고리의 데이터 삽
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
                                groupMenu.setClass(context, kr.ac.gachon.www.GachonGroup.GroupMenuActivity.class);
                                groupMenu.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                groupMenu.putExtra("groupName",groupName);
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

    //모든 동아리 이름 가져와서 ArrayList에 넣기
    public void getAllGroupName(final Spinner spinner, final Context context) {
        final ArrayList<String> arrayList=new ArrayList<>();
        arrayList.add("동아리 선택");
        arrayList.add("동아리 없음");
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.child("Groups").getChildren()) {
                    String name=snapshot.child("name").getValue(String.class);
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

    //자신의 것과 일치하는 값을 스피너에 선택
    public void setSpinnerMatch(final Spinner spinner, final String child, final String ID) {
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value=dataSnapshot.child("Account").child(ID).child(child).getValue(String.class);
                ArrayAdapter arrayAdapter= (ArrayAdapter) spinner.getAdapter();
                int position=arrayAdapter.getPosition(value);
                spinner.setSelection(position);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //프로필 이미지 업로드
    public void UploadImage(Uri filePath, final Context context, String ID) {
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
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "파일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
        }
    }

    public void getProfileIcon(ImageView icon, String ID, Context context) {
        String filename=ID+".png";
        StorageReference ref=FirebaseStorage.getInstance().getReference("ProfileIcon/"+filename);
        Glide.with(context)
                .load(ref)
                .into(icon);
    }

    //계정 정보 수정
    public void UpdateAccountData(final String ID, final String child, final String value) {
        DatabaseReference reference=database.getReference();
        reference.child("Account").child(ID).child(child).setValue(value);
    }
    //계정 정보 수정(int값)
    public void UpdateAccountData(final String ID, final String child, final int value) {
        DatabaseReference reference=database.getReference();
        reference.child("Account").child(ID).child(child).setValue(value);
    }

    //계정 삭제
    public void RemoveAccount(String ID) {
        DatabaseReference reference=database.getReference();
        reference.child("Account").child(ID).setValue(null);
    }

    //달력에 이벤트 닷 추가
    public void Add_EventDay(final String GroupName, final MaterialCalendarView calendarView) {
        final HashSet<CalendarDay> days=new HashSet<>();
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.child("Groups").child(GroupName).child("Schedule").getChildren()) {
                    String eventDate=snapshot.child("EventDate").getValue(String.class);
                    String dates[]=eventDate.split(",");
                    int year=Integer.parseInt(dates[0]);
                    int month=Integer.parseInt(dates[1])-1;
                    int day=Integer.parseInt(dates[2]);
                    days.add(CalendarDay.from(year, month, day));
                }
                calendarView.addDecorators(new EventdayDecorator(days));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //달력에 이벤트 리스너 추가
    public void Add_EventDayEvent(final String GroupName, final String Day, final LinearLayout layout, final TextView no_Schedule, final Context context) {
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean exist=false;
                layout.removeAllViews();
                layout.addView(no_Schedule);
                for(DataSnapshot snapshot:dataSnapshot.child("Groups").child(GroupName).child("Schedule").getChildren()) {
                    if(snapshot.child("EventDate").getValue(String.class).equals(Day)) {
                        String date=(snapshot.child("EventDate").getValue(String.class).split(","))[2];
                        String name=snapshot.child("EventName").getValue(String.class);
                        LayoutInflater inflater=LayoutInflater.from(context);
                        View sub=inflater.inflate(R.layout.sub_schedule, null);
                        TextView SchDate= sub.findViewById(R.id.schdateTV);
                        TextView SchName= sub.findViewById(R.id.schNameTV);
                        SchDate.setText(date+"일");
                        SchName.setText(name);
                        layout.addView(sub);
                        exist=true;
                    }
                }
                if (exist) no_Schedule.setVisibility(View.GONE);
                else no_Schedule.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void SearchPRBoard(final String input, final Context context) {
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> titles=new ArrayList<>();
                ArrayList<Integer> ids=new ArrayList<>();
                for(DataSnapshot snapshot:dataSnapshot.child("PublicRelation").getChildren()) {
                    if(snapshot.child("title").getValue(String.class).contains(input)) {
                        titles.add(snapshot.child("title").getValue(String.class));
                        ids.add(snapshot.child("id").getValue(Integer.class));
                    }
                }
                PRBoardActivity activity=(PRBoardActivity)PRBoardActivity.PRBActivity;
                activity.finish();
                Intent intent=new Intent(context, PRBoardActivity.class);
                intent.putStringArrayListExtra("titles", titles);
                intent.putIntegerArrayListExtra("ids", ids);

                context.startActivity(intent);
                ((Activity)context).finish();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setTextViewBoard(final TextView author, final TextView title, final TextView content, final String boardName , final int id) {
        DatabaseReference reference=database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String authorStr=dataSnapshot.child(boardName).child(Integer.toString(id)).child("author").getValue(String.class);
                String titleStr=dataSnapshot.child(boardName).child(Integer.toString(id)).child("title").getValue(String.class);
                String contentStr=dataSnapshot.child(boardName).child(Integer.toString(id)).child("content").getValue(String.class);
                author.setText("작성자: "+authorStr);
                title.setText("제목: "+titleStr);
                content.setText(contentStr);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
