package com.apexsoftware.quotable.adapter;
// Created by Jack Butler on 7/30/2018.

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.models.Post;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;

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
        //postManager = PostManager.getInstance(context.getApplicationContext());

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

    public void bindData(Post post, SimpleDateFormat dateFormat) {
        String user = post.getUser();
        String quote = post.getText();

        userTextView.setText(user);
        quoteTextView.setText(quote);
        bookmarkCounterTextView.setText(String.valueOf(post.getBookmarkCount()));
        dateTextView.setText(dateFormat.format(post.getCreatedAt()));

        String imageUri = post.getUserImagePath();

        Glide.with(context)
                .load(imageUri).into(authorImageView);
    }

    public interface OnClickListener {
        //void onItemClick(int position, View view);

        //void onLikeClick(LikeController likeController, int position);

        void onAuthorClick(int position, View view);
    }
}