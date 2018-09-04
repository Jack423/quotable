package com.apexsoftware.quotable.adapter;
// Created by Jack Butler on 9/2/2018.

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.managers.DatabaseHelper;
import com.apexsoftware.quotable.models.Friend;
import com.apexsoftware.quotable.models.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.RequestsViewHolder> {
    private static final String TAG = RequestsAdapter.class.getSimpleName();

    private List<Friend> requestList;

    @NonNull
    @Override
    public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_friend_request, parent, false);
        return new RequestsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RequestsViewHolder holder, int position) {
        Friend friend = requestList.get(position);

        holder.nameTextView.setText(friend.getName());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("users").child(friend.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                assert user != null;
                holder.bioTextView.setText(user.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(holder.context, "Database call canceled", Toast.LENGTH_SHORT).show();
            }
        });

        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(friend.getProfilePhotoUrl());
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(holder.context).load(uri).into(holder.profilePhoto);
            }
        });

        holder.addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper.getInstance(holder.context);
            }
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class RequestsViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView bioTextView;
        public ImageView profilePhoto;
        public Button addFriendButton;
        public Context context;

        public RequestsViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.tv_name);
            bioTextView = itemView.findViewById(R.id.tv_bio);
            profilePhoto = itemView.findViewById(R.id.iv_profile_photo);
            addFriendButton = itemView.findViewById(R.id.btn_accept_request);

            this.context = itemView.getContext();
        }
    }

    public RequestsAdapter(List<Friend> requestList) {
        this.requestList = requestList;
    }


}
