package com.apexsoftware.quotable.main.search.posts;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.adapters.SearchPostsAdapter;
import com.apexsoftware.quotable.enums.PostStatus;
import com.apexsoftware.quotable.main.base.BaseActivity;
import com.apexsoftware.quotable.main.base.BaseFragment;
import com.apexsoftware.quotable.main.postDetails.PostDetailsActivity;
import com.apexsoftware.quotable.main.profile.ProfileActivity;
import com.apexsoftware.quotable.main.search.Searchable;
import com.apexsoftware.quotable.managers.PostManager;
import com.apexsoftware.quotable.managers.listeners.OnObjectExistListener;
import com.apexsoftware.quotable.model.Post;
import com.apexsoftware.quotable.util.AnimationUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import static android.app.Activity.RESULT_OK;

public class SearchPostsFragment extends BaseFragment<SearchPostsView, SearchPostsPresenter>
        implements SearchPostsView, Searchable {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SearchPostsAdapter postsAdapter;
    private TextView emptyListMessageTextView;

    private boolean searchInProgress = false;

    public SearchPostsFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public SearchPostsPresenter createPresenter() {
        if (presenter == null) {
            return new SearchPostsPresenter(getContext());
        }
        return presenter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_results, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recycler_view);
        emptyListMessageTextView = view.findViewById(R.id.emptyListMessageTextView);
        emptyListMessageTextView.setText(getResources().getString(R.string.empty_posts_search_message));

        initRecyclerView();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PostDetailsActivity.UPDATE_POST_REQUEST:
                    if (data != null) {
                        PostStatus postStatus = (PostStatus) data.getSerializableExtra(PostDetailsActivity.POST_STATUS_EXTRA_KEY);
                        if (postStatus.equals(PostStatus.REMOVED)) {
                            postsAdapter.removeSelectedPost();

                        } else if (postStatus.equals(PostStatus.UPDATED)) {
                            postsAdapter.updateSelectedPost();
                        }
                    }
                    break;
            }
        }
    }

    private void initRecyclerView() {
        postsAdapter = new SearchPostsAdapter((BaseActivity) getActivity());
        postsAdapter.setCallBack(new SearchPostsAdapter.CallBack() {
            @Override
            public void onItemClick(Post post, View view) {
                PostManager.getInstance(getActivity().getApplicationContext()).isPostExistSingleValue(post.getId(), new OnObjectExistListener<Post>() {
                    @Override
                    public void onDataChanged(boolean exist) {
                        if (exist) {
                            openPostDetailsActivity(post, view);
                        } else {
                            showSnackBar(R.string.error_post_was_removed);
                        }
                    }
                });
            }

            @Override
            public void onAuthorClick(String authorId, View view) {
                openProfileActivity(authorId, view);
            }

            @Override
            public boolean enableClick() {
                return !searchInProgress;
            }
        });

        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setAdapter(postsAdapter);

        presenter.search();
    }


    @SuppressLint("RestrictedApi")
    public void openProfileActivity(String userId, View view) {
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        intent.putExtra(ProfileActivity.USER_ID_EXTRA_KEY, userId);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && view != null) {

            View authorImageView = view.findViewById(R.id.iv_author_post);

            ActivityOptions options = ActivityOptions.
                    makeSceneTransitionAnimation(getActivity(),
                            new Pair<>(authorImageView, getString(R.string.post_author_image_transition_name)));
            startActivityForResult(intent, ProfileActivity.CREATE_POST_FROM_PROFILE_REQUEST, options.toBundle());
        } else {
            startActivityForResult(intent, ProfileActivity.CREATE_POST_FROM_PROFILE_REQUEST);
        }
    }


    @SuppressLint("RestrictedApi")
    private void openPostDetailsActivity(Post post, View v) {
        Intent intent = new Intent(getActivity(), PostDetailsActivity.class);
        intent.putExtra(PostDetailsActivity.POST_ID_EXTRA_KEY, post.getId());
        intent.putExtra(PostDetailsActivity.AUTHOR_ANIMATION_NEEDED_EXTRA_KEY, true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            //View imageView = v.findViewById(R.id.postImageView);

            /*ActivityOptions options = ActivityOptions.
                    makeSceneTransitionAnimation(getActivity(),
                            new Pair<>(imageView, getString(R.string.post_image_transition_name))
                    );*/
            startActivityForResult(intent, PostDetailsActivity.UPDATE_POST_REQUEST /*options.toBundle()*/);
        } else {
            startActivityForResult(intent, PostDetailsActivity.UPDATE_POST_REQUEST);
        }
    }

    @Override
    public void search(String searchText) {
        presenter.search(searchText);
    }

    @Override
    public void onSearchResultsReady(List<Post> posts) {
        hideLocalProgress();
        emptyListMessageTextView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        postsAdapter.setList(posts);
    }

    @Override
    public void showLocalProgress() {
        searchInProgress = true;
        AnimationUtils.showViewByScaleWithoutDelay(progressBar);
    }

    @Override
    public void hideLocalProgress() {
        searchInProgress = false;
        AnimationUtils.hideViewByScale(progressBar);
    }


    @Override
    public void showEmptyListLayout() {
        hideLocalProgress();
        recyclerView.setVisibility(View.GONE);
        emptyListMessageTextView.setVisibility(View.VISIBLE);
    }
}
