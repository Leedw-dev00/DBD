package com.example.dbd;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ShoplistListAdapter extends BaseAdapter {

    private Context context;
    private List<Shoplist> shoplistList;

    public ShoplistListAdapter(Context context, List<Shoplist> shoplistList) {
        this.context = context;
        this.shoplistList = shoplistList;
    }

    @Override
    public int getCount() {
        return shoplistList.size();
    }

    @Override
    public Object getItem(int i) {
        return shoplistList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View v = View.inflate(context, R.layout.shoplist, null);
        TextView userID = (TextView)v.findViewById(R.id.userID);
        TextView item_Name = (TextView)v.findViewById(R.id.item_Name);
        TextView price = (TextView)v.findViewById(R.id.price);
        TextView store_Code = (TextView)v.findViewById(R.id.store_Code);

        userID.setText(shoplistList.get(i).getUserID());
        item_Name.setText(shoplistList.get(i).getItem_Name());
        price.setText(shoplistList.get(i).getPrice());
        store_Code.setText(shoplistList.get(i).getStore_Code());

        v.setTag(shoplistList.get(i).getItem_Name());
        return v;
    }
}
