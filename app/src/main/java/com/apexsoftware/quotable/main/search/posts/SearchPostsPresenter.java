package com.apexsoftware.quotable.main.search.posts;

import android.content.Context;

import com.apexsoftware.quotable.main.base.BasePresenter;
import com.apexsoftware.quotable.managers.PostManager;
import com.apexsoftware.quotable.model.Post;
import com.apexsoftware.quotable.util.LogUtil;

import java.util.List;

public class SearchPostsPresenter extends BasePresenter<SearchPostsView> {
    public static final int LIMIT_POSTS_FILTERED_BY_LIKES = 10;
    private Context context;
    private PostManager postManager;

    public SearchPostsPresenter(Context context) {
        super(context);
        this.context = context;
        postManager = PostManager.getInstance(context);
    }

    public void search() {
        search("");
    }

    public void search(String searchText) {
        if (checkInternetConnection()) {
            if (searchText.isEmpty()) {
                filterByLikes();
            } else {
                ifViewAttached(SearchPostsView::showLocalProgress);
                postManager.searchByQuote(searchText, this::handleSearchResult);
            }

        } else {
            ifViewAttached(SearchPostsView::hideLocalProgress);
        }
    }

    private void filterByLikes() {
        if (checkInternetConnection()) {
            ifViewAttached(SearchPostsView::showLocalProgress);
            postManager.filterByLikes(LIMIT_POSTS_FILTERED_BY_LIKES, this::handleSearchResult);
        } else {
            ifViewAttached(SearchPostsView::hideLocalProgress);
        }
    }

    private void handleSearchResult(List<Post> list) {
        ifViewAttached(view -> {
            view.hideLocalProgress();
            view.onSearchResultsReady(list);

            if (list.isEmpty()) {
                view.showEmptyListLayout();
            }

            LogUtil.logDebug(TAG, "found items count: " + list.size());
        });
    }
}
