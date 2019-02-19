package com.apexsoftware.quotable.adapters.holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apexsoftware.quotable.Constants;
import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.controllers.LikeController;
import com.apexsoftware.quotable.main.base.BaseActivity;
import com.apexsoftware.quotable.managers.PostManager;
import com.apexsoftware.quotable.managers.ProfileManager;
import com.apexsoftware.quotable.managers.listeners.OnObjectChangedListener;
import com.apexsoftware.quotable.managers.listeners.OnObjectChangedListenerSimple;
import com.apexsoftware.quotable.managers.listeners.OnObjectExistListener;
import com.apexsoftware.quotable.model.Like;
import com.apexsoftware.quotable.model.Post;
import com.apexsoftware.quotable.model.Profile;
import com.apexsoftware.quotable.util.FormatterUtil;
import com.apexsoftware.quotable.util.GlideApp;
import com.apexsoftware.quotable.util.ImageUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hootsuite.nachos.NachoTextView;
import com.hootsuite.nachos.terminator.ChipTerminatorHandler;

import java.util.Arrays;
import java.util.List;

public class PostViewHolder extends RecyclerView.ViewHolder {
    public static final String TAG = PostViewHolder.class.getSimpleName();

    protected Context context;
    private TextView nameTextView;
    private TextView handleTextView;
    private TextView quoteTextView;
    private TextView detailsTextView;
    private TextView likesCounterTextView;
    private ImageView likesImageView;
    private TextView commentsCountTextView;
    private TextView watchersCountTextView;
    private TextView dateTextView;
    private ImageView authorImageView;
    private ViewGroup likeViewGroup;
    //private HashtagView tagsEditText;
    private NachoTextView tagsEditText;

    private ProfileManager profileManager;
    protected PostManager postManager;

    private LikeController likeController;
    private BaseActivity baseActivity;

    public PostViewHolder(View view, final OnClickListener onClickListener, BaseActivity activity) {
        this(view, onClickListener, activity, true);
    }

    public PostViewHolder(View view, final OnClickListener onClickListener, BaseActivity activity, boolean isAuthorNeeded) {
        super(view);
        this.context = view.getContext();
        this.baseActivity = activity;

        likesCounterTextView = view.findViewById(R.id.tv_likes_counter);
        likesImageView = view.findViewById(R.id.iv_like);
        commentsCountTextView = view.findViewById(R.id.tv_comments_count);
        watchersCountTextView = view.findViewById(R.id.tv_watchers_count);
        dateTextView = view.findViewById(R.id.tv_date);
        nameTextView = view.findViewById(R.id.tv_name);
        handleTextView = view.findViewById(R.id.tv_handle);
        quoteTextView = view.findViewById(R.id.tv_quote);
        detailsTextView = view.findViewById(R.id.tv_description);
        authorImageView = view.findViewById(R.id.iv_author_post);
        likeViewGroup = view.findViewById(R.id.likesContainer);
        //hashtagView = view.findViewById(R.id.namesView);
        tagsEditText = view.findViewById(R.id.tagsEditText);

        profileManager = ProfileManager.getInstance(context.getApplicationContext());
        postManager = PostManager.getInstance(context.getApplicationContext());

        view.setOnClickListener(v -> {
            int postition = getAdapterPosition();
            if (onClickListener != null && postition != RecyclerView.NO_POSITION) {
                onClickListener.onItemClick(getAdapterPosition(), v);
            }
        });

        likeViewGroup.setOnClickListener(view1 -> {
            int position = getAdapterPosition();
            if (onClickListener != null && position != RecyclerView.NO_POSITION) {
                onClickListener.onLikeClick(likeController, position);
            }
        });

        authorImageView.setOnClickListener(v -> {
            int position = getAdapterPosition();
            if (onClickListener != null && position != RecyclerView.NO_POSITION) {
                onClickListener.onAuthorClick(getAdapterPosition(), v);
            }
        });
    }

    public void bindData(Post post) {
        likeController = new LikeController(context, post, likesCounterTextView, likesImageView, true);

        profileManager.getProfileSingleValue(post.getAuthorId(), new OnObjectChangedListener<Profile>() {
            @Override
            public void onObjectChanged(Profile obj) {
                nameTextView.setText(obj.getUsername());
                handleTextView.setText("@" + obj.getHandle());
            }

            @Override
            public void onError(String errorText) {

            }
        });
        quoteTextView.setText(post.getQuote());
        detailsTextView.setText(post.getDescription());
        likesCounterTextView.setText(String.valueOf(post.getLikesCount()));
        commentsCountTextView.setText(String.valueOf(post.getCommentsCount()));
        watchersCountTextView.setText(String.valueOf(post.getWatchersCount()));

        //Adds names to an array and makes hashtag view with them
        //TODO: Fix this
        List<String> tagsList = Arrays.asList(post.getTags().split(" "));
        //hashtagView.setData(namesList);
        //List<String> tags = post.getTags();
        tagsEditText.setText(tagsList);
        //tagsEditText.addChipTerminator(' ', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL);


        CharSequence date = FormatterUtil.getRelativeTimeSpanStringShort(context, post.getCreatedDate());
        dateTextView.setText(date);

        if (post.getAuthorId() != null) {
            profileManager.getProfileSingleValue(post.getAuthorId(), createProfileChangeListener(authorImageView));
        }

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            postManager.hasCurrentUserLikeSingleValue(post.getId(), firebaseUser.getUid(), createOnLikeObjectExistListener());
        }
    }

    private String removeNewLinesDividers(String text) {
        int decoratedTextLength = text.length() < Constants.Post.MAX_TEXT_LENGTH_IN_LIST ?
                text.length() : Constants.Post.MAX_TEXT_LENGTH_IN_LIST;
        return text.substring(0, decoratedTextLength).replaceAll("\n", " ").trim();
    }

    private OnObjectChangedListener<Profile> createProfileChangeListener(final ImageView authorImageView) {
        return new OnObjectChangedListenerSimple<Profile>() {
            @Override
            public void onObjectChanged(Profile obj) {
                if (obj.getPhotoUrl() != null) {
                    if (!baseActivity.isFinishing() && !baseActivity.isDestroyed()) {
                        ImageUtil.loadImage(GlideApp.with(baseActivity), obj.getPhotoUrl(), authorImageView);
                    }
                }
            }
        };
    }

    private OnObjectExistListener<Like> createOnLikeObjectExistListener() {
        return exist -> likeController.initLike(exist);
    }

    public interface OnClickListener {
        void onItemClick(int position, View view);

        void onLikeClick(LikeController likeController, int position);

        void onAuthorClick(int position, View view);
    }
}
