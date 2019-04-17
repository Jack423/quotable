package com.apexsoftware.quotable.main.searchTest;
//Created by Jack Butler on 4/2/2019

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.adapters.SearchPostsAdapter;
import com.apexsoftware.quotable.main.base.BaseActivity;
import com.apexsoftware.quotable.managers.PostManager;
import com.apexsoftware.quotable.managers.listeners.OnObjectExistListener;
import com.apexsoftware.quotable.model.Post;
import com.apexsoftware.quotable.util.LogUtil;
import com.google.android.material.navigation.NavigationView;
import com.mancj.materialsearchbar.MaterialSearchBar;

public class SearchTestActivity extends BaseActivity<SearchTestView, SearchTestPresenter> implements NavigationView.OnNavigationItemSelectedListener, MaterialSearchBar.OnSearchActionListener {
    private static final String TAG = SearchTestActivity.class.getSimpleName();

    private MaterialSearchBar searchBar;
    private RecyclerView recyclerView;
    private SearchPostsAdapter postsAdapter;

    Context context;

    @NonNull
    @Override
    public SearchTestPresenter createPresenter() {
        context = this;
        if (presenter == null) {
            return new SearchTestPresenter(this);
        }
        return presenter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_test);

        searchBar = findViewById(R.id.searchBar);
        recyclerView = findViewById(R.id.items_list);
        searchBar.setOnSearchActionListener(this);
        searchBar.inflateMenu(R.menu.search_menu);
        searchBar.setCardViewElevation(10);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                LogUtil.logDebug(TAG, "text changed " + searchBar.getText());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        initRecyclerView();
    }

    private void initRecyclerView() {
        postsAdapter = new SearchPostsAdapter((BaseActivity) getParent());
        postsAdapter.setCallBack(new SearchPostsAdapter.CallBack() {
            @Override
            public void onItemClick(Post post, View view) {
                PostManager.getInstance(getApplicationContext()).isPostExistSingleValue(post.getId(), new OnObjectExistListener<Post>() {
                    @Override
                    public void onDataChanged(boolean exist) {
                        if (exist) {
                            //openPostDetailsActivity(post, view);
                        } else {

                        }
                    }
                });
            }

            @Override
            public void onAuthorClick(String authorId, View view) {

            }

            @Override
            public boolean enableClick() {
                return false;
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {

    }

    @Override
    public void onSearchConfirmed(CharSequence text) {

    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }
}
