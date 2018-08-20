package com.apexsoftware.quotable.adapter;
//Created by Jack Butler on 8/20/2018.

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.apexsoftware.quotable.activities.BaseActivity;

public class FriendsAdapter extends BasePostAdapter {

    public FriendsAdapter(BaseActivity activity) {
        super(activity);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }
}
