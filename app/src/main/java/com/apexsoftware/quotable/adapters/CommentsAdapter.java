package com.apexsoftware.quotable.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.adapters.holders.CommentViewHolder;
import com.apexsoftware.quotable.model.Comment;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class CommentsAdapter extends RecyclerView.Adapter<CommentViewHolder> {
    private List<Comment> list = new ArrayList<>();
    private Callback callback;

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_list_item, parent, false);
        return new CommentViewHolder(view, callback);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        holder.itemView.setLongClickable(true);
        holder.bindData(getItemByPosition(position));
    }

    public Comment getItemByPosition(int position) {
        return list.get(position);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void setList(List<Comment> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface Callback {
        void onLongItemClick(View view, int position);

        void onAuthorClick(String authorId, View view);
    }
}
