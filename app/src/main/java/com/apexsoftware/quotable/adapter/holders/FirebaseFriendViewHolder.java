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
        friendSince = itemView.findViewById(R.id.tv_friend_since);
        profilePhoto = itemView.findViewById(R.id.iv_profile_photo);
    }

    /*public void bindFriend(final Friend friend) {
        final TextView name = view.findViewById(R.id.tv_name);
        final TextView friendSince = view.findViewById(R.id.tv_friend_since);
        final ImageView profilePhoto = view.findViewById(R.id.iv_profile_photo);

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(friend.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                name.setText(user.getName());
                friendSince.setText(friend.getFriendSince());

                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(user.getPictureUrl());
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(context).load(uri).into(profilePhoto);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "--Failed to bind friend: " + databaseError.getMessage());
            }
        });
    }*/
}
