package com.apexsoftware.quotable.adapters;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.apexsoftware.quotable.R;

public class ProfileAdapter  {
    private Context context;
    private String query;

    static class ProfileViewHolder extends RecyclerView.ViewHolder {
        private TextView handle;
        private ImageView profilePhoto;

        ProfileViewHolder(View itemView) {
            super(itemView);
            handle = itemView.findViewById(R.id.handle);
            profilePhoto = itemView.findViewById(R.id.profilePhoto);
        }
    }
}
