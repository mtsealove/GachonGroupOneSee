package kr.ac.gachon.www.GachonGroup.modules;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import kr.ac.gachon.www.GachonGroup.R;

public class Alert extends AppCompatActivity {
    //메세지 다이얼로그 출력 메서드
    public void MsgDialog(String msg, Context context) {
        //레이아웃을 inflate함
        LayoutInflater inflater=LayoutInflater.from(context);
        View layout=inflater.inflate(R.layout.dialog_msg, null);
        //텍스트 설정
        TextView msgTV= layout.findViewById(R.id.dialog_msgTV);
        msgTV.setText(msg);

        //다이얼로그 생성
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setView(layout);
        final AlertDialog dialog=builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        //ok버튼 설정
        Button ok= layout.findViewById(R.id.okay);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        //출력
        dialog.show();
    }

    //OK버튼으로 액티비티 종료
    public void MsgDialogEnd(String msg, final Context context) {
        //레이아웃을 inflate함
        LayoutInflater inflater=LayoutInflater.from(context);
        View layout=inflater.inflate(R.layout.dialog_msg, null);
        //텍스트 설정
        TextView msgTV= layout.findViewById(R.id.dialog_msgTV);
        msgTV.setText(msg);

        //다이얼로그 생성
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setView(layout);
        final AlertDialog dialog=builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));

        //ok버튼 설정
        Button ok= layout.findViewById(R.id.okay);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                ((Activity)context).finish();
                //버튼을 누름과 동시에 Activity 종료
            }
        });
        //출력
        dialog.show();
    }
}
