package kr.ac.gachon.www.GachonGroup.etc;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import kr.ac.gachon.www.GachonGroup.R;

public class Alert extends AppCompatActivity {
    private final Context context;
    public static AlertDialog dialog;

    public Alert(Context context) {
        this.context=context;
    }
    //메세지 다이얼로그 출력 메서드
    public void MsgDialog(String msg) {
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
    public void MsgDialogEnd(String msg) {
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
    //2개의 버튼 출력, 메세지와 onclicklistener를 통해 버튼 클릭 시 수행할 활동 지정 가능
    public void MsgDialogChoice(String msg, View.OnClickListener positiveListener) {
        dialog=null;
        LayoutInflater inflater=LayoutInflater.from(context);
        View layout=inflater.inflate(R.layout.dialog_msg_choice, null);
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setView(layout);
        TextView msgTV=layout.findViewById(R.id.dialog_msgTV);
        msgTV.setText(msg);
        Button negative=layout.findViewById(R.id.negative);
        Button positive=layout.findViewById(R.id.positive);
        dialog=builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        positive.setOnClickListener(positiveListener);
        dialog.show();
    }
}
