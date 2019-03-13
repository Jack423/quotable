package com.apexsoftware.quotable.adapters.holders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.adapters.MentionAdapter;
import com.apexsoftware.quotable.managers.ProfileManager;
import com.apexsoftware.quotable.managers.listeners.OnObjectChangedListener;
import com.apexsoftware.quotable.model.Mention;
import com.apexsoftware.quotable.model.Profile;
import com.apexsoftware.quotable.util.GlideApp;
import com.apexsoftware.quotable.util.ImageUtil;
import com.apexsoftware.quotable.util.LogUtil;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MentionViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = MentionViewHolder.class.getSimpleName();

    private final ProfileManager profileManager;
    private final ImageView avatarImageView;
    private final TextView nameTextView;
    private final TextView handleTextView;
    private Context context;

    public MentionViewHolder(@NonNull View itemView, final MentionAdapter.Callback callback) {
        super(itemView);

        this.context = itemView.getContext();
        profileManager = ProfileManager.getInstance(itemView.getContext());

        avatarImageView = itemView.findViewById(R.id.avatarImageView);
        nameTextView = itemView.findViewById(R.id.nameTextView);
        handleTextView = itemView.findViewById(R.id.handleEditText);
    }

    public void bindData(Mention mention) {
        final String authorId = mention.getAuthorId();

        if (authorId != null) {
            profileManager.getProfileSingleValue(authorId, createOnProfileChangedListen(avatarImageView, nameTextView, handleTextView));
        }
    }

    private OnObjectChangedListener<Profile> createOnProfileChangedListen(final ImageView avatarImageView,
                                                                          final TextView userName,
                                                                          final TextView handle) {
        return new OnObjectChangedListener<Profile>() {
            @Override
            public void onObjectChanged(Profile obj) {
                String userName = obj.getUsername();
                String handle = obj.getHandle();
                fillMention(userName, handle, nameTextView, handleTextView);

                if (obj.getPhotoUrl() != null) {
                    ImageUtil.loadImage(GlideApp.with(context), obj.getPhotoUrl(), avatarImageView);
                }
            }

            @Override
            public void onError(String errorText) {
                LogUtil.logDebug(TAG, errorText);
            }
        };
    }

    private void fillMention(String userName, String handle, TextView nameTextView, TextView handleTextView) {
        nameTextView.setText(userName);
        handleTextView.setText(handle);
    }
}
