package am.romanbalayan.chatapp.Chat;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import am.romanbalayan.chatapp.R;


public class ChatsFragment extends Fragment {
    public ChatsFragment() {
    }

    private DatabaseReference mChatsDB;
    private FirebaseAuth mAuth;

    private String mCurUser;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chats2, container, false);

        recyclerView = view.findViewById(R.id.chat_list_rec_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        mAuth = FirebaseAuth.getInstance();

        mCurUser = mAuth.getCurrentUser().getUid();

        final ArrayList<MessageObject> list = new ArrayList<>();
        mAdapter = new ChatListAdapter(list);

        recyclerView.setAdapter(mAdapter);


        mChatsDB = FirebaseDatabase.getInstance().getReference().child("Chat").child(mCurUser);

        mChatsDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                MessageObject messageObject = dataSnapshot.child("last").getValue(MessageObject.class);
                if(messageObject.getType().equals("image")) messageObject.setMessage("Image");
                messageObject.setId(dataSnapshot.getKey());
                list.add(0, messageObject);
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

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

}


