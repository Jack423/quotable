package com.apexsoftware.quotable.activities;
//Created by Jack Butler on 7/30/2018.

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.adapter.PostsByUserAdapter;
import com.apexsoftware.quotable.managers.DatabaseHelper;
import com.apexsoftware.quotable.managers.FriendManager;
import com.apexsoftware.quotable.managers.PostManager;
import com.apexsoftware.quotable.managers.UserManager;
import com.apexsoftware.quotable.managers.listeners.OnFriendChangedListener;
import com.apexsoftware.quotable.managers.listeners.OnObjectChangedListener;
import com.apexsoftware.quotable.managers.listeners.OnObjectExistListener;
import com.apexsoftware.quotable.models.FriendRequest;
import com.apexsoftware.quotable.models.Post;
import com.apexsoftware.quotable.models.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserProfileActivity extends BaseActivity {
    private static final String TAG = UserProfileActivity.class.getSimpleName();
    private static final int CREATE_POST_REQUEST = 1;
    public static final String USER_ID_EXTRA_KEY = "UserProfileActivity.USER_ID_EXTRA_KEY";
    public static final int CREATE_POST_FROM_PROFILE_REQUEST = 22;

    RecyclerView recyclerView;
    Context context;

    TextView name, quotes, followers, following, bio;
    Button follow;
    ImageView profilePicture;
    FloatingActionButton fab;

    private String currentUserId;
    private String user_id;
    private String currentState;

    private PostsByUserAdapter postsAdapter;
    private SwipeRefreshLayout swipeContainer;
    private UserManager userManager;
    private FriendManager friendManager;
    private ProgressBar progressBar;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog progressDialog;

    private DatabaseReference mUsersDatabase;
    private DatabaseReference notificationReference;
    private DatabaseReference mFriendReqDatabase;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mNotificationDatabase;

    private DatabaseReference mRootRef;

    private boolean isCurrentUser = false;

    //Firebase shite
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Toolbar toolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);

        user_id = getIntent().getStringExtra(USER_ID_EXTRA_KEY);
        currentUserId = firebaseUser.getUid();

        mRootRef = FirebaseDatabase.getInstance().getReference();

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");

        auth = FirebaseAuth.getInstance();

        name = findViewById(R.id.text_name);
        bio = findViewById(R.id.text_user_details);
        quotes = findViewById(R.id.text_quotes);
        followers = findViewById(R.id.text_followers);
        following = findViewById(R.id.text_following);
        follow = findViewById(R.id.btnFollow);
        fab = findViewById(R.id.btnCreate);
        profilePicture = findViewById(R.id.image_user_profile);
        progressBar = findViewById(R.id.postsProgressBar);

        context = this;

        currentState = "not_friends";

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading user data");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        swipeContainer = findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onRefreshAction();
            }
        });

        friendManager = FriendManager.getInstance(this);

        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.default_avatar).into(mProfileImage);

                if(currentUserId.equals(user_id)){
                    follow.setEnabled(false);
                    follow.setVisibility(View.INVISIBLE);
                }

                //--------------- FRIENDS LIST / REQUEST FEATURE -----

                mFriendReqDatabase.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(user_id)){
                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();

                            if(req_type.equals("received")){
                                currentState = "req_received";
                                follow.setText("Accept Friend Request");
                            } else if(req_type.equals("sent")) {
                                currentState = "req_sent";
                                follow.setText("Cancel Friend Request");
                            }

                            progressDialog.dismiss();

                        } else {
                            mFriendDatabase.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(user_id)){
                                        currentState = "friends";
                                        follow.setText("Unfriend this Person");
                                    }
                                    progressDialog.dismiss();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        loadPostsList();
        supportPostponeEnterTransition();
    }

    @Override
    public void onStart() {
        super.onStart();
        loadProfile();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        userManager.closeListeners(this);

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage(this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(resultCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CreatePostActivity.CREATE_NEW_POST_REQUEST:
                    postsAdapter.loadPosts();
                    showSnackBar(R.string.message_post_was_created);
                    setResult(RESULT_OK);
                    break;
            }
        }
    }

    private void onRefreshAction() {
        postsAdapter.loadPosts();
    }

    private void loadProfile() {
        userManager = UserManager.getInstance(this);
        userManager.getProfileValue(UserProfileActivity.this, user_id, createOnProfileChangedListener());
    }

    private OnObjectChangedListener<User> createOnProfileChangedListener() {
        return new OnObjectChangedListener<User>() {
            @Override
            public void onObjectChanged(User obj) {
                fillUIFields(obj);
            }
        };
    }

    private void fillUIFields(final User user) {
        if (user != null) {
            name.setText(user.getName());
            bio.setText(user.getBio());

            if (user.getId().equals(currentUserId)) {
                follow.setVisibility(View.GONE);
            }

            if (user.getPictureUrl() != null) {
                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                StorageReference reference = firebaseStorage.getReferenceFromUrl(user.getPictureUrl());

                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(context).load(uri).into(profilePicture);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Glide.with(context).load(R.drawable.ic_stub).into(profilePicture);
                    }
                });
            }

            follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    follow.setEnabled(false);

                    // --------------- NOT FRIENDS STATE ------------

                    if (currentState.equals("not_friends")) {

                        DatabaseReference newNotificationref = mRootRef.child("notifications").child(user_id).push();
                        String newNotificationId = newNotificationref.getKey();

                        HashMap<String, String> notificationData = new HashMap<>();
                        notificationData.put("from", currentUserId);
                        notificationData.put("type", "request");

                        FriendRequest friendRequest = new FriendRequest(user_id, "received");
                        Map requestMap = new HashMap();
                        requestMap.put("Friend_req/" + currentUserId + "/" + user_id + "/request_type", "sent");
                        //requestMap.put("Friend_req/" + user_id + "/" + currentUserId + "/request_type", "received");
                        requestMap.put("notifications/" + user_id + "/" + newNotificationId, notificationData);

                        mRootRef.child("Friend_req").child(user_id).child(currentUserId).setValue(friendRequest);

                        mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null) {
                                    Toast.makeText(UserProfileActivity.this, "There was some error in sending request", Toast.LENGTH_SHORT).show();
                                } else {
                                    currentState = "req_sent";
                                    follow.setText("Cancel Friend Request");
                                }

                                follow.setEnabled(true);
                            }
                        });
                    }

                    // - -------------- CANCEL REQUEST STATE ------------

                    if (currentState.equals("req_sent")) {
                        mFriendReqDatabase.child(currentUserId).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mFriendReqDatabase.child(user_id).child(currentUserId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        follow.setEnabled(true);
                                        currentState = "not_friends";
                                        follow.setText("Send Friend Request");
                                    }
                                });
                            }
                        });
                    }

                    // ------------ REQ RECEIVED STATE ----------

                    if (currentState.equals("req_received")) {

                        final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                        Map friendsMap = new HashMap();
                        friendsMap.put("friends/" + currentUserId + "/" + user_id + "/date", currentDate);
                        friendsMap.put("friends/" + user_id + "/" + currentUserId + "/date", currentDate);

                        friendsMap.put("friend_req/" + currentUserId + "/" + user_id, null);
                        friendsMap.put("friend_req/" + user_id + "/" + currentUserId, null);

                        mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError == null) {
                                    follow.setEnabled(true);
                                    currentState = "friends";
                                    follow.setText("Unfriend this Person");
                                } else {
                                    String error = databaseError.getMessage();
                                    Toast.makeText(UserProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    // ------------ UNFRIENDS ---------

                    if (currentState.equals("friends")) {

                        Map unfriendMap = new HashMap();
                        unfriendMap.put("friends/" + currentUserId + "/" + user_id, null);
                        unfriendMap.put("friends/" + user_id + "/" + currentUserId, null);

                        mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError == null) {
                                    currentState = "not_friends";
                                    follow.setText("Send Friend Request");
                                } else {
                                    String error = databaseError.getMessage();
                                    Toast.makeText(UserProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                                }

                                follow.setEnabled(true);
                            }
                        });
                    }
                }
            });
        } else {
            progressBar.setVisibility(View.GONE);
            profilePicture.setImageResource(R.drawable.ic_stub);
        }
    }

    private void loadPostsList() {
        if (recyclerView == null) {
            recyclerView = findViewById(R.id.list);
            postsAdapter = new PostsByUserAdapter(this, user_id);
            postsAdapter.setCallBack(new PostsByUserAdapter.CallBack() {
                @Override
                public void onItemClick(Post post, View view) {
                    PostManager.getInstance(UserProfileActivity.this).isPostExistSingleValue(post.getPostId(), new OnObjectExistListener<Post>() {
                        @Override
                        public void onDataChanged(boolean exist) {
                            if (exist) {
                                //openPostDetailActivity(post, view);
                            } else {
                                showSnackBar(R.string.post_was_removed);
                            }
                        }
                    });
                }

                @Override
                public void onPostsListChanged(int postsCount) {
                    DatabaseReference databaseReference = firebaseDatabase.getReference("users").child(firebaseUser.getUid());
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            if (user != null) {
                                quotes.setText(Integer.toString(user.getPostCount()));
                                followers.setText(Integer.toString(user.getFollowers()));
                                following.setText(Integer.toString(user.getFollowing()));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    swipeContainer.setRefreshing(false);
                    hideLoadingPostsProgressBar();
                }

                @Override
                public void onPostLoadingCanceled() {
                    swipeContainer.setRefreshing(false);
                    hideLoadingPostsProgressBar();
                }
            });

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            //((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
            recyclerView.setAdapter(postsAdapter);
            postsAdapter.loadPosts();
        }
    }

    private void hideLoadingPostsProgressBar() {
        if (progressBar.getVisibility() != View.GONE) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private Spannable buildCounterSpannable(int value) {
        SpannableStringBuilder contentString = new SpannableStringBuilder();
        contentString.append(String.valueOf(value));
        int start = contentString.length();
        contentString.setSpan(new TextAppearanceSpan(this, R.style.AppTheme_Text_ProfileCounter), start, contentString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return contentString;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_block) {
            //TODO Implement blocking mechanism
        } else if (id == R.id.action_report) {
            //TODO Implement reporting mechanism
        }

        return super.onOptionsItemSelected(item);
    }
}
