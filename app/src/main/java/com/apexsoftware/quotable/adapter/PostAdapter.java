package com.apexsoftware.quotable.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.apexsoftware.quotable.models.DataPost;
import com.apexsoftware.quotable.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder>{
    List<DataPost> posts;
    SimpleDateFormat dateFormat;

    public PostAdapter() {
        posts = new ArrayList<>();
        dateFormat = new SimpleDateFormat("hh:mm a, MM/dd/yyyy", Locale.getDefault());
    }

    /*
     * This function is used to add a yak to the list.
     * We then notify that the data has been changed on the last
     * element of the list.
     * */
    public void addPost(DataPost post) {
        posts.add(post);
        notifyItemInserted(posts.size() - 1);
    }

    /*
     * In order to get the behavior we wanted we needed to
     * use employ polymorphism and add a location argument
     * to the addPost class so that we can have the yak be added
     * to the top of the list.
     */
    public void addPost(DataPost post, int location)
    {
        posts.add(location, post);
        notifyItemInserted(location);
    }



    public void removePost(int index) {
        posts.remove(index);
        notifyItemRemoved(index);
    }

    public void removePost(DataPost post) {
        int index = posts.indexOf(post);
        removePost(index);
    }


    /*
     * These next few methods are needed whenever you
     * want a custom list like we're doing.
     *
     * If you're interested, you can read a little more at
     * https://www.binpress.com/tutorial/android-l-recyclerview-and-cardview-tutorial/156
     * and
     * http://developer.android.com/training/material/lists-cards.html
     * */

    @Override
    public PostHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.row_post, parent, false);

        return new PostHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PostHolder holder, int position) {
        DataPost post = posts.get(position);
        holder.bind(post, dateFormat);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }


    /*
     * A ViewHolder is each cell or list item.
     * it holds and inflates every element which can
     * be bind to a "Yak"
     *
     * Edit: Part 4
     * Yak has been changed to DataYak
     * */
    public class PostHolder extends RecyclerView.ViewHolder {

        TextView tvQuote, tvUser, tvDate;
        ImageView ivProfile;
        ImageButton bookmark, comment;

        public PostHolder(View itemView) {
            super(itemView);

            tvQuote = itemView.findViewById(R.id.tv_quote);
            tvUser = itemView.findViewById(R.id.tv_user);
            tvDate = itemView.findViewById(R.id.tv_date);

            ivProfile = itemView.findViewById(R.id.iv_profile);

            bookmark= itemView.findViewById(R.id.iv_bookmark);
            comment = itemView.findViewById(R.id.iv_comment);
        }

        public void bind(DataPost post, SimpleDateFormat dateFormat) {
            tvQuote.setText(post.getText());
            tvUser.setText(post.getUser());
            tvDate.setText(dateFormat.format(post.getCreatedAt()));
        }
    }
}
