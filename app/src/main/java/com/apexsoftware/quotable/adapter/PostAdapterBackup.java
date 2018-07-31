package com.apexsoftware.quotable.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.activities.MainActivity;
import com.apexsoftware.quotable.models.Post;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PostAdapterBackup extends RecyclerView.Adapter<PostAdapterBackup.PostViewHolder>{
    List<Post> posts;
    SimpleDateFormat dateFormat;

    public PostAdapterBackup() {
        posts = new ArrayList<>();
        dateFormat = new SimpleDateFormat("hh:mm a, MM/dd/yyyy", Locale.getDefault());
    }

    public void addPost(Post post) {
        posts.add(post);
        notifyItemInserted(posts.size() - 1);
    }

    public void addPost(Post post, int location)
    {
        posts.add(location, post);
        notifyItemInserted(location);
    }

    public void removePost(int index) {
        posts.remove(index);
        notifyItemRemoved(index);
    }

    public void removePost(Post post) {
        int index = posts.indexOf(post);
        removePost(index);
    }

    public Post getPost(int position) {
        return posts.get(position);
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.row_post, parent, false);

        return new PostViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, final int position) {
        Post post = posts.get(position);
        holder.bind(post, dateFormat);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        TextView tvQuote, tvUser, tvDate;
        ImageView ivProfile;
        ImageButton ibBookmark, ibComment;
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        MainActivity mainActivity;


        Context context;

        public PostViewHolder(View itemView) {
            super(itemView);

            tvQuote = itemView.findViewById(R.id.tv_quote);
            tvUser = itemView.findViewById(R.id.tv_user);
            tvDate = itemView.findViewById(R.id.tv_date);

            ivProfile = itemView.findViewById(R.id.iv_profile);

            ibBookmark = itemView.findViewById(R.id.iv_bookmark);
            ibComment = itemView.findViewById(R.id.iv_comment);

            ivProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PostAdapterBackup postAdapter = new PostAdapterBackup();
                    int position = getAdapterPosition();
                    mainActivity.onProfileClick(postAdapter.getPost(position), v);
                }
            });
        }

        public void bind(Post post, SimpleDateFormat dateFormat) {
            tvQuote.setText(post.getText());
            tvUser.setText(post.getUser());
            tvDate.setText(dateFormat.format(post.getCreatedAt()));
            String userImagePath = post.getUserImagePath();

            //Glide.with(context).load(userImagePath).into(ivProfile);
        }

        public ImageView getIvProfile() {
            return ivProfile;
        }
    }
}
