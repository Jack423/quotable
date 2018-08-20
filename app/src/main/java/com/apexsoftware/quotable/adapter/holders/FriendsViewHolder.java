package com.apexsoftware.quotable.adapter.holders;
//Created by Jack Butler on 8/20/2018.

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.managers.DatabaseHelper;
import com.apexsoftware.quotable.managers.UserManager;
import com.apexsoftware.quotable.models.Friend;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FriendsViewHolder extends RecyclerView.ViewHolder {
    public static final String TAG = FriendsViewHolder.class.getSimpleName();

    private Context context;
    private TextView name;
    private TextView bio;
    private ImageView profilePhoto;

    private UserManager userManager;
    private DatabaseHelper databaseHelper;

    public FriendsViewHolder (View view, final OnClickListener onClickListener) {
        this(view, onClickListener, true);
    }

    public FriendsViewHolder(View view, final OnClickListener onClickListener, boolean isAuthorNeeded) {
        super(view);
        this.context = view.getContext();

        name = view.findViewById(R.id.tv_name);
        bio = view.findViewById(R.id.tv_bio);
        profilePhoto = view.findViewById(R.id.iv_profile_photo);

        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                if (onClickListener != null && position != RecyclerView.NO_POSITION) {
                    onClickListener.onAuthorClick(getAdapterPosition(), v);
                }
            }
        });
    }

    public void bindData(Friend friend) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public interface OnClickListener {
        void onAuthorClick(int position, View view);
    }
}
