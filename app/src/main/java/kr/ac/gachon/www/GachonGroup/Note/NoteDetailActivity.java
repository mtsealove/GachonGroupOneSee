package kr.ac.gachon.www.GachonGroup.Note;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import kr.ac.gachon.www.GachonGroup.R;

public class NoteDetailActivity extends AppCompatActivity {
    String Sender, Receiver, Date, Content, Cat;
    TextView otherTV, otherContent, timeTV, timeContent, ContentTV, titleTV;
    RelativeLayout bottomLayout;
    Button returnBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);
        otherTV=findViewById(R.id.otherTV);
        otherContent=findViewById(R.id.otherContent);
        timeTV=findViewById(R.id.timeTV);
        timeContent=findViewById(R.id.timeContent);
        ContentTV=findViewById(R.id.contentTV);
        titleTV=findViewById(R.id.titleTV);
        bottomLayout=findViewById(R.id.bottomLayout);
        returnBtn=findViewById(R.id.returnBtn);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnNote();
            }
        });

        getParameter();
        if(Cat.equals("Receive")) { //받은 쪽지함일 때
            titleTV.setText("받은 쪽지함");
            otherTV.setText("보낸 사람");
            otherContent.setText(Sender);
            timeTV.setText("받은 시간");
        } else if(Cat.equals("Send")) { //보낸 쪽지함일 때
            titleTV.setText("보낸 쪽지함");
            otherTV.setText("받는 사람");
            otherContent.setText(Receiver);
            timeTV.setText("보낸 시간");
            bottomLayout.setVisibility(View.GONE);
        }
        ContentTV.setText(Content);
        timeContent.setText(Date);
    }

    private void getParameter() {   //이전 액티비티에서 데이터를 받아옴
        Intent intent=getIntent();
        Sender=intent.getStringExtra("Sender");
        Receiver=intent.getStringExtra("Receiver");
        Date=intent.getStringExtra("Date");
        Content=intent.getStringExtra("Content");
        Cat=intent.getStringExtra("Cat");
    }

    private void returnNote() {
        Intent intent=new Intent(this, SendNoteActivity.class);
        intent.putExtra("Sender", Receiver);
        intent.putExtra("Receiver", Sender);
        startActivity(intent);
    }

    public void close(View v) {
        finish();
    }
}
