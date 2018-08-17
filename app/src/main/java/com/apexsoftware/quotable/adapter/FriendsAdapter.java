package com.apexsoftware.quotable.adapter;
//Created by Jack Butler on 8/16/2018.

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.models.Friend;

import java.util.ArrayList;
import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> implements View.OnClickListener{

    private ArrayList<Friend> friendsList;
    Context context;
    private int lastPosition = -1;

    private static class ViewHolder {
        TextView name;
        TextView bio;
        ImageView profilePhoto;
    }

    public FriendsAdapter(ArrayList<Friend> data, Context context) {
        super(context, R.layout.row_friend, data);
        this.friendsList = data;
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();

        Object object = getItem(position);
        Friend friend = (Friend) object;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    /*@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get the data item for this position
        Friend friend = getItem(position);
        //check if existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; //view lookup cache stored in tag
        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_friend, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.bio = convertView.findViewById(R.id.tv_bio);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        return convertView;
    }*/
}
