package am.romanbalayan.chatapp.Feed;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.Inet4Address;
import java.util.ArrayList;

import am.romanbalayan.chatapp.Feed.Compose.ComposeActivity;
import am.romanbalayan.chatapp.R;

public class FeedFragment extends Fragment {
    public FeedFragment() { }

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;

    private ImageButton compose;

    private DatabaseReference mFriendsDB;
    private DatabaseReference mFeedDB;
    final ArrayList<FeedObject> list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        compose = view.findViewById(R.id.composeFeed);

        compose.setOnClickListener(view1 -> {
            Intent intent = new Intent(view1.getContext(), ComposeActivity.class);
            startActivity(intent);
        });

        recyclerView = view.findViewById(R.id.feed_rec_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        String curUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mFriendsDB = FirebaseDatabase.getInstance().getReference().child("Friends").child(curUser);
        try {
            mFeedDB = FirebaseDatabase.getInstance().getReference().child("Feed");
        } catch (Exception e){ }

        mAdapter = new FeedRecViewAdapter(list);

        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                compose.setVisibility(View.VISIBLE);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                 compose.setVisibility(View.INVISIBLE);
            }
        });


        mFriendsDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot1, @Nullable String s) {
                String posterId = dataSnapshot1.getKey();
                feedDBQuery(posterId);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String posterId = dataSnapshot.getKey();
                feedDBQuery(posterId);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void feedDBQuery(String posterId) {
        mFeedDB.child(posterId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                FeedObject feedObject = dataSnapshot.getValue(FeedObject.class);
                feedObject.setPosterId(posterId);
                list.add(0, feedObject);
                mAdapter.notifyItemInserted(0);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
