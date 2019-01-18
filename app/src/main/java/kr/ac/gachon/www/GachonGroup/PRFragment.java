package kr.ac.gachon.www.GachonGroup;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

import kr.ac.gachon.www.GachonGroup.modules.FirebaseHelper;


public class PRFragment extends Fragment {
    ArrayList<String> titles;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle si) {
        View view=inflater.inflate(R.layout.fragment_pr, container,false);
        titles=new ArrayList<>();
        try {
            titles = getArguments().getStringArrayList("titles");
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
        return view;
    }
}