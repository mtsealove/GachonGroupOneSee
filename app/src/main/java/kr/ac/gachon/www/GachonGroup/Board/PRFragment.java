package kr.ac.gachon.www.GachonGroup.Board;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

import kr.ac.gachon.www.GachonGroup.R;


public class PRFragment extends Fragment {
    ArrayList<String> titles;
    ArrayList<Integer> ids;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle si) {
        View view=inflater.inflate(R.layout.fragment_pr, container,false);
        titles=new ArrayList<>();
        ids=new ArrayList<>();

        int page;
        try {
            titles = getArguments().getStringArrayList("titles");
        } catch (Exception e) {

        }
        try {
            ids=getArguments().getIntegerArrayList("ids");
        } catch (Exception e) {

        }

        int buttonID[]=new int[8];
        buttonID[0]=R.id.board0;
        buttonID[1]=R.id.board1;
        buttonID[2]=R.id.board2;
        buttonID[3]=R.id.board3;
        buttonID[4]=R.id.board4;
        buttonID[5]=R.id.board5;
        buttonID[6]=R.id.board6;
        buttonID[7]=R.id.board7;

        Button[] boards=new Button[8];
        for(int i=0; i<8; i++)
            boards[i]= view.findViewById(buttonID[i]);

        try {
            for (int i = 0; i < titles.size(); i++)
                boards[i].setText(titles.get(i));
            for(int i=titles.size(); i<8; i++) {
                boards[i].setText("");
            }
        } catch (NullPointerException e) {

        }
        if(ids==null) {
            //페이지로 아이디 설정
            try {
                page = getArguments().getInt("page");
                for (int i = 0; i < titles.size(); i++) {
                    final int id = page * 8 + i;
                    boards[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            OpenBoard(id);
                        }
                    });
                }
            } catch (Exception e) {

            }
        }
        else {
            for(int i=0; i<titles.size(); i++) {
                final int id=ids.get(i);
                boards[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OpenBoard(id);
                    }
                });
            }
        }

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