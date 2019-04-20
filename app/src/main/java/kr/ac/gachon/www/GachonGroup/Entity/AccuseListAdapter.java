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

import kr.ac.gachon.www.GachonGroup.R;

public class AccuseListAdapter extends BaseAdapter { //가입 신청 표시용 어댑터
    private ArrayList<AccuseItem> accuseItems=new ArrayList<>();

    public AccuseListAdapter() {

    }

    @Override
    public int getCount() {
        return accuseItems.size();
    }

    @Override
    public Object getItem(int position) {
        return accuseItems.get(position);
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
            convertView=inflater.inflate(R.layout.item_accuse, parent, false);
        }

        TextView IDTV=convertView.findViewById(R.id.IDTV);
        TextView contentTV=convertView.findViewById(R.id.contentTV);
        TextView readTV=convertView.findViewById(R.id.readTV);
        //checkBox.setVisibility(View.GONE);

        AccuseItem accuseItem = accuseItems.get(position);

        String content4show=accuseItem.getReason().replace("\n", " ");
        if(content4show.length()>15) content4show=content4show.substring(0, 15)+"...";
        contentTV.setText(content4show);
        IDTV.setText(accuseItem.getUserID());
        if(accuseItem.isRead()) readTV.setText("읽음");
        else readTV.setText("");

        return convertView;
    }

    public void addItem(String Reason, String userID, boolean read) {
        AccuseItem accuseItem=new AccuseItem();
        accuseItem.setUserID(userID);
        accuseItem.setReason(Reason);
        accuseItem.setRead(read);
        accuseItems.add(0, accuseItem);
    }
}
