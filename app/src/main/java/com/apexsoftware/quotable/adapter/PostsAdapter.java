package com.apexsoftware.quotable.adapter;
// Created by Jack Butler on 7/30/2018.

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.activities.MainActivity;
import com.apexsoftware.quotable.adapter.holders.LoadViewHolder;
import com.apexsoftware.quotable.adapter.holders.PostViewHolder;
import com.apexsoftware.quotable.enums.ItemType;
import com.apexsoftware.quotable.managers.PostManager;
import com.apexsoftware.quotable.managers.listeners.OnPostListChangedListener;
import com.apexsoftware.quotable.models.Post;
import com.apexsoftware.quotable.models.PostListResult;
import com.apexsoftware.quotable.util.PreferencesUtil;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PostsAdapter extends BasePostAdapter {
    public static final String TAG = PostsAdapter.class.getSimpleName();

    private Callback callback;
    private boolean isLoading = false;
    private boolean isMoreDataAvailable = true;
    private long lastLoadedItemCreatedDate;
    private SwipeRefreshLayout swipeContainer;
    private MainActivity mainActivity;
    private SimpleDateFormat dateFormat;

    public PostsAdapter(final MainActivity activity, SwipeRefreshLayout swipeContainer) {
        super(activity);
        this.mainActivity = activity;
        this.swipeContainer = swipeContainer;
        setHasStableIds(true);
        initRefreshLayout();
        dateFormat = new SimpleDateFormat("hh:mm a, MM/dd/yyyy", Locale.getDefault());
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    private void initRefreshLayout() {
        if(swipeContainer != null) {
            swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    onRefreshAction();
                }
            });
        }
    }

    private void onRefreshAction() {
        if (activity.hasInternetConnection()) {
            loadFirstPage();
            Log.d(TAG, "--------Loaded first page");
            cleanSelectedPostInformation();
            Log.d(TAG, "--------Cleaned selected post information");
            swipeContainer.setRefreshing(false);
            Log.d(TAG, "--------Set refreshing to false");
        } else {
            swipeContainer.setRefreshing(false);
            mainActivity.showFloatButtonRelatedSnackBar(R.string.internet_connection_failed);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == ItemType.ITEM.getTypeCode()) {
            return new PostViewHolder(inflater.inflate(R.layout.row_post, parent, false),
                    createOnClickListener());
        } else {
            return new LoadViewHolder(inflater.inflate(R.layout.loading_view, parent, false));
        }
    }

    private PostViewHolder.OnClickListener createOnClickListener() {
        return new PostViewHolder.OnClickListener() {
            /*@Override
            public void onItemClick(int position, View view) {
                if (callback != null) {
                    selectedPostPosition = position;
                    callback.onItemClick(getItemByPosition(position), view);
                }
            }

            @Override
            public void onLikeClick(LikeController likeController, int position) {
                Post post = getItemByPosition(position);
                likeController.handleLikeClickAction(mainActivity, post);
            }*/

            @Override
            public void onAuthorClick(int position, View view) {
                if (callback != null) {
                    callback.onAuthorClick(getItemByPosition(position).getUserId(), view);
                }
            }
        };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position >= getItemCount() - 1 && isMoreDataAvailable && !isLoading) {
            android.os.Handler mHandler = mainActivity.getWindow().getDecorView().getHandler();
            mHandler.post(new Runnable() {
                public void run() {
                    //change adapter contents
                    if (mainActivity.hasInternetConnection()) {
                        isLoading = true;
                        postList.add(new Post());
                        notifyItemInserted(postList.size());
                        loadNext(lastLoadedItemCreatedDate - 1);
                    } else {
                        mainActivity.showFloatButtonRelatedSnackBar(R.string.internet_connection_failed);
                    }
                }
            });


        }

        if (getItemViewType(position) != ItemType.LOAD.getTypeCode()) {
            ((PostViewHolder) holder).bindData(postList.get(position));
        }
    }

    private void addList(List<Post> list) {
        this.postList.addAll(list);
        notifyDataSetChanged();
        isLoading = false;
    }

    public void loadFirstPage() {
        loadNext(0);
        //PostManager.getInstance(mainActivity.getApplicationContext()).clearNewPostsCounter();
    }

    private void loadNext(final long nextItemCreatedDate) {

        if (!PreferencesUtil.isPostWasLoadedAtLeastOnce(mainActivity) && !mainActivity.hasInternetConnection()) {
            mainActivity.showFloatButtonRelatedSnackBar(R.string.internet_connection_failed);
            hideProgress();
            callback.onListLoadingFinished();
            return;
        }

        OnPostListChangedListener<Post> onPostsDataChangedListener = new OnPostListChangedListener<Post>() {
            @Override
            public void onListChanged(PostListResult result) {
                lastLoadedItemCreatedDate = result.getLastItemCreatedDate();
                isMoreDataAvailable = result.isMoreDataAvailable();
                List<Post> list = result.getPosts();

                if (nextItemCreatedDate == 0) {
                    postList.clear();
                    notifyDataSetChanged();
                    //swipeContainer.setRefreshing(false);
                }

                hideProgress();

                if (!list.isEmpty()) {
                    addList(list);

                    if (!PreferencesUtil.isPostWasLoadedAtLeastOnce(mainActivity)) {
                        PreferencesUtil.setPostWasLoadedAtLeastOnce(mainActivity, true);
                    }
                } else {
                    isLoading = false;
                }

                callback.onListLoadingFinished();
            }

            @Override
            public void onCanceled(String message) {
                callback.onCanceled(message);
            }
        };

        PostManager.getInstance(mainActivity).getPostsList(onPostsDataChangedListener, nextItemCreatedDate);
    }

    public void removeSelectedPost() {
        postList.remove(selectedPostPosition);
        notifyItemRemoved(selectedPostPosition);
    }

    private void hideProgress() {
        if (!postList.isEmpty() && getItemViewType(postList.size() - 1) == ItemType.LOAD.getTypeCode()) {
            postList.remove(postList.size() - 1);
            notifyItemRemoved(postList.size() - 1);
        }
    }

    public interface Callback {
        void onItemClick(Post post, View view);

        void onListLoadingFinished();

        void onAuthorClick(String authorId, View view);

        void onCanceled(String message);
    }
}
