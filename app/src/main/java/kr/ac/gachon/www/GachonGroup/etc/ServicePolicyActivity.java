package kr.ac.gachon.www.GachonGroup.etc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import kr.ac.gachon.www.GachonGroup.R;

public class ServicePolicyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_policy);
    }

    public void close(View v) {
        finish();
    }
}
