package com.example.currencyconverter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends BaseAdapter {
    ArrayList<Country> countries;
    Context myContext;

    MyAdapter(){}
    MyAdapter(ArrayList countries, Context context){
        this.countries = countries;
        myContext = context;
    }

    @Override
    public int getCount() {
        return countries.size();
    }

    @Override
    public Object getItem(int i) {
        return countries.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        View view = convertView;
        if(convertView == null){
            view = View.inflate(myContext, R.layout.my_list_item_layout, null);

            viewHolder = new ViewHolder();
            viewHolder.itemText = (TextView) view.findViewById(R.id.itemText);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Country c = countries.get(i);
        String iText = c.currency + " - " + c.name;
        viewHolder.itemText.setText(iText);

        return view;
    }
    class ViewHolder{
        TextView itemText;
    }
}
