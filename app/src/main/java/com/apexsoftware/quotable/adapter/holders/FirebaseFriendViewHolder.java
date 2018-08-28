package com.apexsoftware.quotable.adapter.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.apexsoftware.quotable.R;

//Created by Jack Butler on 8/27/2018.

public class FirebaseFriendViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = FirebaseFriendViewHolder.class.getSimpleName();

    public TextView name;
    public TextView friendSince;
    public ImageView profilePhoto;

    public FirebaseFriendViewHolder(View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.tv_name);
        friendSince = itemView.findViewById(R.id.tv_friends_since);
        profilePhoto = itemView.findViewById(R.id.iv_profile_photo);
    }
}
