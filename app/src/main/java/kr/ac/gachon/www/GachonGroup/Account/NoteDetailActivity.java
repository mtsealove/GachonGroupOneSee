package kr.ac.gachon.www.GachonGroup.Account;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import kr.ac.gachon.www.GachonGroup.R;

public class NoteDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);
    }

    public void close(View v) {
        finish();
    }
}
