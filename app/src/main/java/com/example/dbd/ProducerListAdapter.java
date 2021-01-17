package com.example.dbd;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class ProducerListAdapter extends BaseAdapter {

    private Context context;
    private List<Producer> producerList;

    public ProducerListAdapter(Context context, List<Producer> producerList){
        this.context = context;
        this.producerList = producerList;
    }

    @Override
    public int getCount() {
        return producerList.size();
    }

    @Override
    public Object getItem(int i) {
        return producerList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(context, R.layout.producer, null);
        TextView item_Code = (TextView)v.findViewById(R.id.item_Code);
        TextView item_Name = (TextView)v.findViewById(R.id.item_Name);
        TextView count = (TextView)v.findViewById(R.id.count);
        TextView warehouse_Code = (TextView)v.findViewById(R.id.warehouse_Code);

        item_Code.setText(producerList.get(i).getItem_Code());
        item_Name.setText(producerList.get(i).getItem_Name());
        count.setText(producerList.get(i).getCount());
        warehouse_Code.setText(producerList.get(i).getWarehouse_Code());

        v.setTag(producerList.get(i).getItem_Code());
        return v;
    }
}
