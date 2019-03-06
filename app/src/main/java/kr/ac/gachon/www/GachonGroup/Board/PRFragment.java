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


public class PRFragment extends Fragment { //홍보게시판의 페이지로 표시될 fragment
    ListView listView;
    int page;
    int count;
    private String userID, value;
    FirebaseList firebaseList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle si) {
        View view=inflater.inflate(R.layout.fragment_pr, container,false);
        listView=view.findViewById(R.id.listView);
        listView.setHorizontalScrollBarEnabled(false);
        firebaseList=new FirebaseList(getContext());

        //아이템 받아오기
        page=getArguments().getInt("page"); //페이지 번호
        count=getArguments().getInt("count"); //페이지 당 표시될 개수
        userID=getArguments().getString("userID"); //사용자 ID
        value=getArguments().getString("value"); //검색 값

        //리스트 뷰 설정
        if(value!=null) firebaseList.setCountListView(userID, listView, "PublicRelation", page, count, value);
        else firebaseList.setCountListView(userID, listView, "PublicRelation", page, count);


        return view;
    }
}