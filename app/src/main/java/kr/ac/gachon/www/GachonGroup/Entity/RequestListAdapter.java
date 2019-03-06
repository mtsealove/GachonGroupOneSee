package kr.ac.gachon.www.GachonGroup.Entity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import kr.ac.gachon.www.GachonGroup.R;

public class RequestListAdapter extends BaseAdapter { //가입 신청 표시용 어댑터
    private ArrayList<RequestListViewItem> requestListViewItems =new ArrayList<>();

    public RequestListAdapter() {

    }

    @Override
    public int getCount() {
        return requestListViewItems.size();
    }

    @Override
    public Object getItem(int position) {
        return requestListViewItems.get(position);
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

        RequestListViewItem requestListViewItem = requestListViewItems.get(position);

        nameTV.setText(requestListViewItem.getName());
        majorTV.setText(requestListViewItem.getMajor());
        return convertView;
    }

    public void addItem(String name, String major) {
        RequestListViewItem item=new RequestListViewItem();
        item.setMajor(major); //전공
        item.setName(name); //이름
        requestListViewItems.add(item);
    }
}
