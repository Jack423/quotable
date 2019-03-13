package com.apexsoftware.quotable.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.adapters.holders.MentionViewHolder;
import com.apexsoftware.quotable.model.Mention;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MentionAdapter extends RecyclerView.Adapter<MentionViewHolder> {
    private List<Mention> list = new ArrayList<>();
    private Callback callback;

    @Override
    public MentionViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mention_item_list_view, parent, false);
        return new MentionViewHolder(view, callback);
    }

    @Override
    public void onBindViewHolder(@NonNull MentionViewHolder mentionViewHolder, int i) {
        mentionViewHolder.bindData(getItemByPosition(i));
    }

    public Mention getItemByPosition(int position) {
        return list.get(position);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface Callback {
        void onAuthorClick(String authorId, View view);
    }
}
