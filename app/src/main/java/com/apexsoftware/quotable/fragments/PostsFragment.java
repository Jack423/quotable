package com.apexsoftware.quotable.fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.activities.CreatePostActivity;
import com.apexsoftware.quotable.activities.UserProfileActivity;
import com.apexsoftware.quotable.adapter.PostsAdapter;
import com.apexsoftware.quotable.managers.ProfileManager;
import com.apexsoftware.quotable.models.Post;
import com.apexsoftware.quotable.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostsFragment extends Fragment {
    private static final String TAG = PostsFragment.class.getSimpleName();
    private static final int CREATE_POST_REQUEST = 1;

    //Adapter and recycler view are member variables
    private PostsAdapter postsAdapter;
    private ProfileManager userManager;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;

    //Firebase references
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = auth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();

    public PostsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_posts, container, false);

        userManager = ProfileManager.getInstance(getContext());
        //ApplicationHelper.initDatabaseHelper();

        initContentView(view);

        return view;
    }

    public void onProfileClick(Post post, View v) {
        openProfileActivity(post.getUserId(), v);
    }

    public void openProfileActivity(String userId, View view) {
        Intent intent = new Intent(getContext(), UserProfileActivity.class);
        intent.putExtra(UserProfileActivity.USER_ID_EXTRA_KEY, userId);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && view != null) {

            View authorImageView = view.findViewById(R.id.iv_profile);

            /*ActivityOptions options = ActivityOptions.
                    makeSceneTransitionAnimation(MainActivity.this,
                            new android.util.Pair<>(authorImageView, getString(R.string.post_author_image_transition_name)));*/
            startActivityForResult(intent, UserProfileActivity.CREATE_POST_FROM_PROFILE_REQUEST);
        } else {
            startActivityForResult(intent, UserProfileActivity.CREATE_POST_FROM_PROFILE_REQUEST);
        }
    }

    private void initContentView(final View view) {
        /*if(recyclerView == null) {
            floatingActionButton = view.findViewById(R.id.fab);

            if(floatingActionButton != null) {
                floatingActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(hasInternetConnection()) {
                            Intent intent = new Intent(getContext(), CreatePostActivity.class);
                            startActivity(intent);
                        } else {
                            //showFloatButtonRelatedSnackBar(R.string.no_internet_connection);
                            Snackbar.make(view, "Not connected to the internet, please try again", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
            }

            final ProgressBar progressBar = view.findViewById(R.id.progressBar);
            SwipeRefreshLayout swipeContainer = view.findViewById(R.id.swipeContainer);
            recyclerView = view.findViewById(R.id.list);
            postsAdapter = new PostsAdapter(getActivity(), swipeContainer);
            postsAdapter.setCallback(new PostsAdapter.Callback() {
                @Override
                public void onItemClick(Post post, View view) {
                    //Implement singular post view later
                }

                @Override
                public void onListLoadingFinished() {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onAuthorClick(String authorId, View view) {
                    openProfileActivity(authorId, view);
                }

                @Override
                public void onCanceled(String message) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                }
            });

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
            recyclerView.setAdapter(postsAdapter);
            postsAdapter.loadFirstPage();
        }*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CREATE_POST_REQUEST) {
            if(resultCode == RESULT_OK) {
                final Calendar c = Calendar.getInstance();
                final String postText = data.getStringExtra(CreatePostActivity.TEXT);
//                String user = getUserAccount();

                //Get the prefs we made earlier
                SharedPreferences prefs = getActivity().getSharedPreferences("Quotable", MODE_PRIVATE);
                //Get our UUID from the store
                String uuid = prefs.getString("uuid", "");

                reference.child("users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                        //Fill in our model
                        User user = dataSnapshot.getValue(User.class);
                        String name = user.getName();

                        //Create our yak with the user data
                        Post post = new Post(name, firebaseUser.getUid(), postText);
                        //post.setUserImagePath(userImage);
                        reference.child("quotes").child(post.getPostId()).setValue(post);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    }
}
