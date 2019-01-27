package kr.ac.gachon.www.GachonGroup.Entity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import kr.ac.gachon.www.GachonGroup.R;

public class ListAdapter extends BaseAdapter {
    private ArrayList<ListViewItem> listViewItems=new ArrayList<>();

    public ListAdapter() {

    }

    @Override
    public int getCount() {
        return listViewItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listViewItems.get(position);
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
            convertView=inflater.inflate(R.layout.join_request_list, parent, false);
        }

        TextView nameTV= convertView.findViewById(R.id.nameTV);
        TextView majorTV= convertView.findViewById(R.id.majorTV);

        ListViewItem listViewItem=listViewItems.get(position);

        nameTV.setText(listViewItem.getName());
        majorTV.setText(listViewItem.getMajor());
        return convertView;
    }

    public void addItem(String name, String major) {
        ListViewItem item=new ListViewItem();
        item.setMajor(major);
        item.setName(name);
        listViewItems.add(item);
    }
}
