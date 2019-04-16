package kr.ac.gachon.www.GachonGroup.Manager;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import kr.ac.gachon.www.GachonGroup.R;

public class RequirementLogActivity extends AppCompatActivity {
    String boardName="Requirement";
    public static Activity _requirementLogActivity;
    Button postBtn;
    ListView boardLV;
    TextView titleTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        postBtn=findViewById(R.id.postBtn);
        postBtn.setVisibility(View.GONE);
        boardLV=findViewById(R.id.boardLV);
        titleTV=findViewById(R.id.titleTV);
        titleTV.setText("문의사항");

        _requirementLogActivity=RequirementLogActivity.this;

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference reference=database.getReference().child("Requirements");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> titles=new ArrayList<>();
                final ArrayList<Integer> ids=new ArrayList<>();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    String title=snapshot.child("title").getValue(String.class);
                    int id=snapshot.child("id").getValue(Integer.class);
                    ids.add(id);
                    titles.add(title);
                }
                ArrayAdapter adapter=new ArrayAdapter(RequirementLogActivity.this, R.layout.dropown_item_custom, titles);
                boardLV.setAdapter(adapter);
                boardLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent=new Intent(RequirementLogActivity.this, RequirementsLogDetialActivity.class);
                        intent.putExtra("ID", Integer.toString(ids.get(position)));
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void close(View v) {
        finish();
    }
}
