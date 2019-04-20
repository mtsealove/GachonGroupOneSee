package kr.ac.gachon.www.GachonGroup.Entity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import kr.ac.gachon.www.GachonGroup.R;

public class NoteListAdapter extends BaseAdapter { //가입 신청 표시용 어댑터
    private ArrayList<NoteItem> noteItems =new ArrayList<>();

    public NoteListAdapter() {

    }

    @Override
    public int getCount() {
        return noteItems.size();
    }

    @Override
    public Object getItem(int position) {
        return noteItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos=position;
        final Context context=parent.getContext();

        if(convertView==null) {
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.item_note, parent, false);
        }

        TextView contentTV= convertView.findViewById(R.id.contentTV); //내용
        TextView dateTV= convertView.findViewById(R.id.dateTV);   //날짜
        final CheckBox checkBox=convertView.findViewById(R.id.noteCheck); //쪽지 및 삭제를 위한
        //checkBox.setVisibility(View.GONE);
        checkBox.setChecked(((ListView)parent).isItemChecked(pos));
        checkBox.setFocusable(false);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {  //체크 지정
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {    //체크하면 참으로 변경
                noteItems.get(pos).setChecked(isChecked);
            }
        });

        NoteItem noteItem = noteItems.get(position);

        String content4show=noteItem.getContent().replace("\n", " ");
        if(content4show.length()>10) content4show=content4show.substring(0, 10)+"...";
        contentTV.setText(content4show);
        String date4show=noteItem.getDate().substring(5, 7)+"/"+noteItem.getDate().substring(8, 10);
        dateTV.setText(date4show);
        if(noteItem.isRead()) {
            convertView.setBackground(new ColorDrawable(Color.parseColor("#DBDBDB")));    //만약에 읽었으면 회색바탕
            dateTV.setText("읽음");   //읽음 표시
        }
        return convertView;
    }

    public void addItem(String sender, String receiver, String content, String date, boolean read) {
        NoteItem noteItem=new NoteItem();
        noteItem.setContent(content); //내용
        noteItem.setDate(date); //날짜
        noteItem.setRead(read); //읽었는지
        noteItem.setSender(sender); //보내는 사람
        noteItem.setReceiver(receiver); //받는 사람
        noteItems.add(0, noteItem); //마지막에 넣기
    }
}
