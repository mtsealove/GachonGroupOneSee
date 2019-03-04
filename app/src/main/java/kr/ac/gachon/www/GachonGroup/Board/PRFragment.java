package kr.ac.gachon.www.GachonGroup.Board;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import kr.ac.gachon.www.GachonGroup.FirebaseActivity.FirebaseList;
import kr.ac.gachon.www.GachonGroup.R;


public class PRFragment extends Fragment {
    ArrayList<String> titles;
    ArrayList<Integer> ids;
    ListView listView;
    int page;
    int count;
    private String userID, value;
    FirebaseList firebaseList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle si) {
        View view=inflater.inflate(R.layout.fragment_pr, container,false);
        listView=view.findViewById(R.id.listView);
        firebaseList=new FirebaseList(getContext());

        //아이템 받아오기
        page=getArguments().getInt("page");
        count=getArguments().getInt("count");
        userID=getArguments().getString("userID");
        value=getArguments().getString("value");

        if(value!=null) firebaseList.setCountListView(userID, listView, "PublicRelation", page, count, value);
        else firebaseList.setCountListView(userID, listView, "PublicRelation", page, count);

        return view;
    }
    private void OpenBoard(int id) {
        String boardName="PublicRelation";
        Intent intent=new Intent(getContext(), BoardActivity.class);
        intent.putExtra("boardName", boardName);
        intent.putExtra("id", id);
        startActivity(intent);
    }
}