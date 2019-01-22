package kr.ac.gachon.www.GachonGroup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import kr.ac.gachon.www.GachonGroup.modules.FirebaseHelper;

public class PRSearchActivity extends AppCompatActivity {
    ArrayList<String> titles;
    Button searchBtn;
    EditText searchET;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchET= findViewById(R.id.searchET);
        searchBtn= findViewById(R.id.searchBtn);
        searchET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId==EditorInfo.IME_ACTION_SEARCH) {
                    Search();
                    return true;
                }
                return false;
            }
        });
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Search();
            }
        });
    }

    private void Search() {
        String input=searchET.getText().toString();
        FirebaseHelper helper=new FirebaseHelper();
        helper.SearchPRBoard(input, PRSearchActivity.this);

    }

}
