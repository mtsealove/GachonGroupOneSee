package kr.ac.gachon.www.GachonGroup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

public class PRSearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prsearch);
        Intent intent=getIntent();
        ArrayList<ArrayList<String>> titles=(ArrayList<ArrayList<String>>)getIntent().getSerializableExtra("titles");
    }
}
