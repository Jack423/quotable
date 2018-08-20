package com.apexsoftware.quotable.activities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ArrayAdapter;

import com.apexsoftware.quotable.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FriendsActivity extends AppCompatActivity {
    public static final String TAG = FriendsActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private ArrayList friends = new ArrayList<>();

    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        recyclerView = findViewById(R.id.friends_list);

        initContent();
    }

    private void initContent() {

    }

    private class FriendsAdapter extends ArrayAdapter<String> {
        HashMap<String, Integer> idMap = new HashMap<String, Integer>();

        public FriendsAdapter(Context context, int textViewResourceId, List<String> objects) {
            super(context, textViewResourceId, objects);

            for(int i = 0; i < objects.size(); i++) {
                idMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return idMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
