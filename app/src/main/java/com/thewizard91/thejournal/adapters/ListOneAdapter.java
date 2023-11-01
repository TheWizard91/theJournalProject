package com.thewizard91.thejournal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.thewizard91.thejournal.R;
import com.thewizard91.thejournal.models.listOne.ListenItemInListOne;

import java.util.ArrayList;

public class ListOneAdapter extends BaseAdapter {
    private ArrayList<ListenItemInListOne> listOneData;
    private LayoutInflater layoutInflater;
    public ListOneAdapter(Context myContest, ArrayList<ListenItemInListOne> listOneData) {
        this.listOneData = listOneData;
        layoutInflater = LayoutInflater.from(myContest);
    }

    @Override
    public int getCount() {
        return listOneData.size();
    }

    @Override
    public Object getItem(int i) {
        return listOneData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.list_row_one, null);
            holder = new ViewHolder();
            holder.hImage = view.findViewById(R.id.list_row_one_image_view);
            holder.hDescription = view.findViewById(R.id.text_view_one);
            holder.hNumberOfElementsInSection = view.findViewById(R.id.text_view_two);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.hImage.setImageDrawable(listOneData.get(i).getImage());
        holder.hDescription.setText(listOneData.get(i).getDescription());
        holder.hNumberOfElementsInSection.setText(listOneData.get(i).getNumberOfElementsInSection());
        return view;
    }

    static class ViewHolder {
        ImageView hImage;
        TextView hDescription;
        TextView hNumberOfElementsInSection;
    }
}
