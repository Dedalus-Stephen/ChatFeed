package am.romanbalayan.chatapp.Friends;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import am.romanbalayan.chatapp.R;
import am.romanbalayan.chatapp.User.UserObject;

public class SearchFriends extends AppCompatActivity {

    private DatabaseReference ref;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private Toolbar toolbar;

    private TextInputEditText inputName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends);

        toolbar = findViewById(R.id.search_friends_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inputName = findViewById(R.id.search_friends_name_edit);

        final ArrayList<UserObject> list = new ArrayList<>();
        initRecView(list);

        ref = FirebaseDatabase.getInstance().getReference();

        inputName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                list.clear();
                mAdapter.notifyDataSetChanged();
                populateResult(editable, list);
            }
        });
    }

    private void initRecView(ArrayList<UserObject> list){
        recyclerView = findViewById(R.id.found_user_list);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new SearchRecViewAdapter(list);
        recyclerView.setAdapter(mAdapter);
    }

    private void populateResult(Editable editable, final ArrayList<UserObject> list) {
        ref.child("Users").orderByChild("name").startAt(editable.toString()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                UserObject user = dataSnapshot.getValue(UserObject.class);
                if(dataSnapshot.getKey() != null){
                    user.setId(dataSnapshot.getKey());
                }
                if(!user.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    list.add(user);
                    mAdapter.notifyDataSetChanged();
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

    }
}


