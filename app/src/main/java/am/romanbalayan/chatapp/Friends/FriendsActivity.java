package am.romanbalayan.chatapp.Friends;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import am.romanbalayan.chatapp.MainActivity;
import am.romanbalayan.chatapp.R;
import am.romanbalayan.chatapp.User.UserObject;

public class FriendsActivity extends AppCompatActivity {

    private Spinner spinner;
    private Toolbar mToolbar;
    private Button search;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    DatabaseReference mFriendList;
    DatabaseReference mUsers;
    FirebaseUser mCurUser;
    DatabaseReference mReqDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);


        mToolbar = findViewById(R.id.friends_tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        initSpinner();

        final ArrayList<UserObject> list = new ArrayList<>();

        // KEEP IN MIND THAT HASHMAP'S CONTAINS KEY IS O(1)
        // SO THIS APPROACH DOESN'T BURDEN THE PROCESS
        final HashMap<String, Byte> map_ind = new HashMap<>();


        initRecyclerView(list);

        mCurUser = FirebaseAuth.getInstance().getCurrentUser();
        mFriendList = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurUser.getUid());
        mUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mReqDb = FirebaseDatabase.getInstance().getReference().child("FriendReqDB").child(mCurUser.getUid());

        search = findViewById(R.id.search_btn);

        search.setOnClickListener(view -> {
            Intent intent = new Intent(FriendsActivity.this, SearchFriends.class);
            startActivity(intent);
        });

        mReqDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.child("request type").getValue().equals("received")) {
                    String id = dataSnapshot.getKey();
                    if(!map_ind.containsKey(id)) {
                        mUsers.child(id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                map_ind.put(id, (byte) 1);
                                UserObject user = dataSnapshot.getValue(UserObject.class);
                                user.setId(id);
                                user.setPending(true);
                                list.add(user);
                                mAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
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

        mFriendList.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final String id = dataSnapshot.getKey();
                if (!map_ind.containsKey(id)) {
                        mUsers.child(id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                try {
                                    map_ind.put(id, (byte) 1);
                                    UserObject user = dataSnapshot.getValue(UserObject.class);
                                    user.setId(id);
                                    user.setPending(false);
                                    list.add(user);
                                    mAdapter.notifyDataSetChanged();
                                } catch (NullPointerException npe){

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                final String id = dataSnapshot.getKey();
                mUsers.child(id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterator<UserObject> iterator = list.iterator();
                        while (iterator.hasNext()) {
                            if (iterator.next().getId().equals(id)) {
                                iterator.remove();
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }


    private void initRecyclerView(ArrayList<UserObject> list) {
        recyclerView = findViewById(R.id.friends_rec_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new FriendsRecViewAdapter(list);
        recyclerView.setAdapter(mAdapter);
    }

    private void initSpinner() {
        spinner = findViewById(R.id.spinner);

        List<String> options = new ArrayList<>();
        options.add("Friends");
        options.add("Main Activity");


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.custom_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 1) {
                    Intent intent = new Intent(FriendsActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
