package com.apexsoftware.quotable.main.mentionTest;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.main.base.BaseActivity;
import com.apexsoftware.quotable.managers.DatabaseHelper;
import com.apexsoftware.quotable.managers.ProfileManager;
import com.apexsoftware.quotable.managers.listeners.OnObjectChangedListener;
import com.apexsoftware.quotable.model.Profile;
import com.apexsoftware.quotable.util.GlideApp;
import com.apexsoftware.quotable.util.LogUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.linkedin.android.spyglass.suggestions.SuggestionsResult;
import com.linkedin.android.spyglass.tokenization.QueryToken;
import com.linkedin.android.spyglass.tokenization.interfaces.QueryTokenReceiver;
import com.linkedin.android.spyglass.ui.RichEditorView;
import com.teamwork.autocomplete.MultiAutoComplete;
import com.teamwork.autocomplete.adapter.AutoCompleteTypeAdapter;
import com.teamwork.autocomplete.adapter.OnTokensChangedListener;
import com.teamwork.autocomplete.filter.HandleTokenFilter;
import com.teamwork.autocomplete.tokenizer.PrefixTokenizer;
import com.teamwork.autocomplete.util.SpannableUtils;
import com.teamwork.autocomplete.view.AutoCompleteViewBinder;
import com.teamwork.autocomplete.view.AutoCompleteViewHolder;
import com.teamwork.autocomplete.view.MultiAutoCompleteEditText;
import com.teamwork.autocomplete.view.SimpleItemViewBinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MentionActivity extends BaseActivity<MentionView, MentionPresenter> implements OnTokensChangedListener<Profile> {

    private static final String TAG = MentionActivity.class.getSimpleName();
    private static final String BUCKET = "handle";
    private ProfileManager profileManager = ProfileManager.getInstance(this);
    private MultiAutoComplete customMultiAutoComplete;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mention);
        MultiAutoCompleteEditText editText = findViewById(R.id.editText);

        initContentView();

        customMultiAutoComplete.onViewAttached(editText);
    }

    @Override
    protected void onDestroy() {
        customMultiAutoComplete.onViewDetached();
        super.onDestroy();
    }

    private void initContentView() {
        List<Profile> profileList = getProfileList();

        AutoCompleteTypeAdapter<Profile> handleTypeAdapter =
                AutoCompleteTypeAdapter.Build.from(new ProfileViewBinder(), new ProfileTokenFilter());

        handleTypeAdapter.setItems(profileList);

        customMultiAutoComplete = new MultiAutoComplete.Builder()
                .tokenizer(new PrefixTokenizer('@'))
                .addTypeAdapter(handleTypeAdapter)
                .build();
    }

    public List<Profile> getProfileList() {
        List<Profile> profileList = new ArrayList<>();

        DatabaseHelper.getInstance(this).getDatabaseReference().child(DatabaseHelper.PROFILES_DB_KEY)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for ( DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Profile profile = snapshot.getValue(Profile.class);
                            profileList.add(profile);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        return profileList;
    }

    @Override
    public void onTokenAdded(@NonNull CharSequence token, @NonNull Profile added) {

    }

    @Override
    public void onTokenRemoved(@NonNull CharSequence token, @NonNull Profile removed) {

    }

    @NonNull
    @Override
    public MentionPresenter createPresenter() {
        if (presenter == null) {
            return new MentionPresenter(this);
        }
        return presenter;
    }

    private static class ProfileViewBinder implements AutoCompleteViewBinder<Profile> {

        @Override
        public long getItemId(@NonNull Profile item) {
            return item.getId().hashCode();
        }

        @Override
        public int getItemLayoutId() {
            return R.layout.item_list_account;
        }

        @NonNull
        @Override
        public AutoCompleteViewHolder getViewHolder(@NonNull View view) {
            return new SimpleItemViewBinder.SimpleItemViewHolder(view);
        }

        @Override
        public void bindData(@NonNull AutoCompleteViewHolder viewHolder, @NonNull Profile item, @Nullable CharSequence constraint) {
            SimpleItemViewBinder.SimpleItemViewHolder itemViewHolder = (SimpleItemViewBinder.SimpleItemViewHolder) viewHolder;
            CharSequence handleLabel;
            if (constraint == null) {
                handleLabel = item.getHandle();
            } else {
                handleLabel = new SpannableStringBuilder()
                        .append(SpannableUtils.setBoldSubText(item.getHandle(), constraint))
                        .append(" (");
            }

            itemViewHolder.textView.setText(handleLabel);
            itemViewHolder.imageView.setImageResource(R.drawable.unnamed);
        }
    }

    private static class ProfileTokenFilter extends HandleTokenFilter<Profile> {

        public ProfileTokenFilter() {
            super('@');
        }

        @Override
        protected boolean matchesConstraint(@NonNull Profile item, @NonNull CharSequence constraint) {
            return item.getHandle().toLowerCase().contains(constraint.toString().toLowerCase());
        }

        @NonNull
        @Override
        public CharSequence toTokenString(@NonNull Profile item) {
            return super.toTokenString(item);
        }
    }
}
