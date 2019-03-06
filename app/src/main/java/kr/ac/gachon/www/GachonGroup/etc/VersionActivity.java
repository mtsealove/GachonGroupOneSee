package kr.ac.gachon.www.GachonGroup.etc;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import kr.ac.gachon.www.GachonGroup.R;

public class VersionActivity extends AppCompatActivity { //버전확인 액티비티
    TextView currentVersionTV, useLastTV, lastVersionTV, downloadLastTV;
    LinearLayout checkVersionLO, lastVersionLayout;
    private String version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version);

        currentVersionTV=findViewById(R.id.current_versionTV);
        checkVersionLO=findViewById(R.id.check_last_versionLayout);
        useLastTV=findViewById(R.id.useLastTV);
        lastVersionLayout=findViewById(R.id.lastVersionLayout);
        lastVersionTV=findViewById(R.id.lastVersionTV);
        downloadLastTV=findViewById(R.id.downloadLastTV);
        CheckCurrentVersion();
        CheckLastVersion();
    }

    public void close(View v) {
        finish();
    }

    private void CheckCurrentVersion() { //현재 버전 확인
        PackageInfo packageInfo=null;
        try {
            //gradle에서 버전 읽어오기
            packageInfo=getPackageManager().getPackageInfo(getPackageName(), 0);
            version=packageInfo.versionName;
            currentVersionTV.setText("현재 버전: "+version); //사용자에게 현재 버전 표시

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void CheckLastVersion() { //최신 버전 확인
        final float version=Float.parseFloat(this.version);
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference reference=database.getReference();

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                float lastVersion=dataSnapshot.child("version").getValue(Float.class); //데이터베이스에서 최신버전 읽기
                if(lastVersion==version) useLastTV.setVisibility(View.VISIBLE); //최신버전과 현재 버전이 일치할 경우, 최신버전임을 출력
                else { //일치하지 않을 경우
                    lastVersionLayout.setVisibility(View.VISIBLE);
                    lastVersionTV.setText("최신 버전: "+lastVersion); //최신버번명과
                    final String link=dataSnapshot.child("downloadLink").getValue(String.class);
                    downloadLastTV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) { //다운로드 가능한 링크 출력
                            Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                            startActivity(intent);
                        }
                    });
                }
                checkVersionLO.setVisibility(View.GONE); //버전체크 중을 표시하는 레이아웃 감추기
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
