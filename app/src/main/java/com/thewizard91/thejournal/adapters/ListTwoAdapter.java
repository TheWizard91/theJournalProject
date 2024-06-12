package com.thewizard91.thejournal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.thewizard91.thejournal.R;
import com.thewizard91.thejournal.models.listTwo.ListTwo;

import java.util.ArrayList;

public class ListTwoAdapter extends BaseAdapter {
    private ArrayList<ListTwo> listTwoData;
    private LayoutInflater layoutInflater;
    public ListTwoAdapter(Context myContext, ArrayList<ListTwo> listTwoData) {
        this.listTwoData = listTwoData;
        layoutInflater = LayoutInflater.from(myContext);
    }

    public ListTwoAdapter() {
    }

    @Override
    public int getCount() {
        return listTwoData.size();
    }

    @Override
    public Object getItem(int i) {
        return listTwoData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.list_row_two, null);
            holder = new ViewHolder();
            holder.hImage = view.findViewById(R.id.photo_in_list_list_row_two);
            holder.hDescription = view.findViewById(R.id.text_view_in_list_row_two);
            holder.hForwardArrow = view.findViewById(R.id.forward_arrow_in_list_row_two);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.hImage.setImageDrawable(listTwoData.get(i).getImageUri());
        holder.hDescription.setText(listTwoData.get(i).getDescription());
//        holder.hForwardArrow.setF(listTwoData.get(i).getForwardArrow());
        return view;
    }

    static class ViewHolder {
        ImageView hImage;
        TextView hDescription;
        ImageView hForwardArrow;
    }
}
