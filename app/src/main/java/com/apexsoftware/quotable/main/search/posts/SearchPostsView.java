package com.apexsoftware.quotable.main.search.posts;

import com.apexsoftware.quotable.main.base.BaseFragmentView;
import com.apexsoftware.quotable.model.Post;

import java.util.List;

public interface SearchPostsView extends BaseFragmentView {
    void onSearchResultsReady(List<Post> posts);
    void showLocalProgress();
    void hideLocalProgress();
    void showEmptyListLayout();
}
