package com.apexsoftware.quotable.adapter;
//Created by Jack Butler on 8/17/2018.

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

import org.w3c.dom.Text;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder>{
    private static final String TAG = FriendsAdapter.class.getSimpleName();

    private ArrayList<Friend> friends = new ArrayList<>();
    private Context context;

    public FriendsAdapter(Context context, ArrayList<Friend> friendsList) {
        this.context = context;
        friends = friendsList;
    }

    @NonNull
    @Override
    public FriendsAdapter.FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_friend, parent, false);
        FriendsViewHolder viewHolder = new FriendsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FriendsAdapter.FriendsViewHolder holder, int position) {
        holder.bindFriend(friends.get(position));
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public void addFriend(Friend friend, int location) {
        friends.add(friend);
        notifyItemInserted(location);
    }

    public class FriendsViewHolder extends RecyclerView.ViewHolder {
        private ImageView profilePhoto;
        private TextView name;
        private TextView bio;
        private Context context;

        public FriendsViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            context = view.getContext();

            profilePhoto = view.findViewById(R.id.iv_profile_photo);
            name = view.findViewById(R.id.tv_name);
            bio = view.findViewById(R.id.tv_bio);
        }

        public void bindFriend(Friend friend) {
            name.setText(friend.getName());
            bio.setText(friend.getBio());
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            Log.d(TAG, "---USER ID:" + firebaseUser.getUid());
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid()).child("friends").child(friend.getUserId());
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(user.getPictureUrl());
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Glide.with(context).load(uri).into(profilePhoto);
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
