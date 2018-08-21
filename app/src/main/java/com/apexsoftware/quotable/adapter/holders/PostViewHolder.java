package com.apexsoftware.quotable.adapter.holders;
// Created by Jack Butler on 7/30/2018.

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.activities.MainActivity;
import com.apexsoftware.quotable.controllers.LikeController;
import com.apexsoftware.quotable.managers.PostManager;
import com.apexsoftware.quotable.managers.UserManager;
import com.apexsoftware.quotable.managers.listeners.OnObjectChangedListener;
import com.apexsoftware.quotable.models.Post;
import com.apexsoftware.quotable.models.User;
import com.apexsoftware.quotable.util.FormatterUtil;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PostViewHolder extends RecyclerView.ViewHolder {
    public static final String TAG = PostViewHolder.class.getSimpleName();

    private Context context;
    private TextView userTextView;
    private TextView quoteTextView;
    private TextView commentsCountTextView;
    private TextView bookmarkCounterTextView;
    private TextView dateTextView;
    private ImageView authorImageView;
    private ImageView bookmarkImageView;

    private PostManager postManager;
    private UserManager userManager;

    public PostViewHolder(View view, final OnClickListener onClickListener) {
        this(view, onClickListener, true);
    }

    public PostViewHolder(View view, final OnClickListener onClickListener, boolean isAuthorNeeded) {
        super(view);
        this.context = view.getContext();

        bookmarkCounterTextView = (TextView) view.findViewById(R.id.tv_bookmark_count);
        bookmarkImageView = (ImageView) view.findViewById(R.id.iv_bookmark);
        dateTextView = (TextView) view.findViewById(R.id.tv_date);
        userTextView = (TextView) view.findViewById(R.id.tv_user);
        quoteTextView = (TextView) view.findViewById(R.id.tv_quote);
        authorImageView = (ImageView) view.findViewById(R.id.iv_profile);

        authorImageView.setVisibility(isAuthorNeeded ? View.VISIBLE : View.GONE);

        //profileManager = ProfileManager.getInstance(context.getApplicationContext());
        postManager = PostManager.getInstance(context.getApplicationContext());
        userManager = UserManager.getInstance(context.getApplicationContext());

        /*view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                if (onClickListener != null && position != RecyclerView.NO_POSITION) {
                    onClickListener.onItemClick(getAdapterPosition(), v);
                }
            }
        });*/

        /*bookmarkImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                if (onClickListener != null && position != RecyclerView.NO_POSITION) {
                    onClickListener.onLikeClick(likeController, position);
                }
            }
        });*/

        authorImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                if (onClickListener != null && position != RecyclerView.NO_POSITION) {
                    onClickListener.onAuthorClick(getAdapterPosition(), v);
                }
            }
        });
    }

    public void bindData(Post post) {
        final String displayName;
        String quote = post.getText();

        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("users").child(post.getUserId());
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    userTextView.setText(user.getName());
                } else {
                    Log.d(TAG, "--USER IS NULL, LOG IN");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //userTextView.setText(user);
        quoteTextView.setText(quote);
        bookmarkCounterTextView.setText(String.valueOf(post.getLikesCount()));

        CharSequence date = FormatterUtil.getRelativeTimeSpanStringShort(context, post.getCreatedAt());
        dateTextView.setText(date);

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference reference = firebaseStorage.getReferenceFromUrl(post.getUserImagePath());

        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).into(authorImageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Glide.with(context).load(R.drawable.ic_stub).into(authorImageView);
            }
        });

        /*FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            postManager.hasCurrentUserLikeSingleValue(post.getId(), firebaseUser.getUid(), createOnLikeObjectExistListener());
        }*/
        if(post.getUserId() != null) {
            userManager.getProfileSingleValue(post.getUserId(), createProfileChangeListener(authorImageView));
        }
    }

    private OnObjectChangedListener<User> createProfileChangeListener(final ImageView authorImageView) {
        return new OnObjectChangedListener<User>() {
            @Override
            public void onObjectChanged(final User obj) {
                if (obj.getPictureUrl() != null) {

                    Glide.with(context)
                            .load(obj.getPictureUrl())
                            .into(authorImageView);
                }
            }
        };
    }

    public interface OnClickListener {
        //void onItemClick(int position, View view);

        void onLikeClick(LikeController likeController, int position);

        void onAuthorClick(int position, View view);
    }
}
