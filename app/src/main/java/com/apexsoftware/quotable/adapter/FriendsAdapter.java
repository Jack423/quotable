package com.apexsoftware.quotable.adapter;
//Created by Jack Butler on 8/20/2018.

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.activities.BaseActivity;
import com.apexsoftware.quotable.activities.FriendsActivity;
import com.apexsoftware.quotable.managers.DatabaseHelper;
import com.apexsoftware.quotable.models.Friend;
import com.apexsoftware.quotable.models.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder> {
    private static final String TAG = FriendsAdapter.class.getSimpleName();

    private List<Friend> friendsList;
    private FriendsActivity friendsActivity;

    public class FriendsViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView friendSince;
        public ImageView profilePhoto;
        public Context context;

        public FriendsViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.tv_name);
            friendSince = itemView.findViewById(R.id.tv_bio);
            profilePhoto =itemView.findViewById(R.id.iv_profile_photo);

            this.context = itemView.getContext();
        }
    }

    public FriendsAdapter(List<Friend> friendsList) {
        this.friendsList = friendsList;
        friendsActivity = new FriendsActivity();
    }

    @NonNull
    @Override
    public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_friend, parent, false);
        return new FriendsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FriendsViewHolder holder, int position) {
        Friend friend = friendsList.get(position);
        holder.nameTextView.setText(friend.getName());
        holder.friendSince.setText(friend.getFriendSince());

        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(friend.getProfilePhotoUrl());
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(holder.context).load(uri).into(holder.profilePhoto);
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }
}
