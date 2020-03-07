package am.romanbalayan.chatapp.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import am.romanbalayan.chatapp.Chat.ChatActivity;
import am.romanbalayan.chatapp.R;

public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = "User Profile Activity";
    private TextView mName;
    private ImageView mUserImage;
    private Button mUserAddToFriends;
    private Button mUserDeclineFriend;
    private Button mUserOpenChat;

    private boolean isFriend;
    private boolean isPending = false;

    private DatabaseReference mFriendDB;
    private DatabaseReference mUserDB;
    private DatabaseReference mFriendQueryDB;
    private DatabaseReference mNotificationsDB;
    private FirebaseUser mCurUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        final String id = getIntent().getStringExtra("id");
        mUserDB = FirebaseDatabase.getInstance().getReference().child("Users").child(id);
        mFriendQueryDB = FirebaseDatabase.getInstance().getReference().child("FriendReqDB");
        mFriendDB = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationsDB = FirebaseDatabase.getInstance().getReference().child("notifs");

        mUserAddToFriends = findViewById(R.id.user_profile_send);
        mUserDeclineFriend = findViewById(R.id.user_profile_decline);
        mUserOpenChat = findViewById(R.id.user_profile_chat);
        mUserImage = findViewById(R.id.user_profile_image);
        mName = findViewById(R.id.user_profile_name);
        mUserAddToFriends = findViewById(R.id.user_profile_send);


        mCurUser = FirebaseAuth.getInstance().getCurrentUser();

        isFriend = false;

        mFriendDB.child(mCurUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(id)) {
                    mUserAddToFriends.setVisibility(View.INVISIBLE);
                    mUserDeclineFriend.setVisibility(View.VISIBLE);
                    isFriend = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mFriendQueryDB.child(mCurUser.getUid()).child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Object o = dataSnapshot.child("request type").getValue();
                if (o != null) {
                    String s = o.toString();
                    if (s.equals("sent")) {
                        mUserDeclineFriend.setVisibility(View.VISIBLE);
                    } else {
                        mUserAddToFriends.setVisibility(View.VISIBLE);
                    }
                } else if (!isFriend) {
                    mUserAddToFriends.setVisibility(View.VISIBLE);
                    mUserDeclineFriend.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        populateUser();


        mUserAddToFriends.setOnClickListener(view -> {
            if (!isFriend && !id.equals(mCurUser.getUid()) && !isPending) {
                mFriendQueryDB.child(mCurUser.getUid()).child(id).child("request type").setValue("sent")
                        .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mFriendQueryDB.child(id).child(mCurUser.getUid()).child("request type").setValue("received")
                                .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "onSuccess: request received");
                            HashMap<String, String> map = new HashMap<>();
                            map.put("from", mCurUser.getUid());
                            map.put("type", "request type: ");
                            mNotificationsDB.child(id).push().setValue(map).addOnCompleteListener(task12 -> {
                                if(task12.isSuccessful()){
                                    mUserAddToFriends.setVisibility(View.INVISIBLE);
                                    mUserDeclineFriend.setVisibility(View.VISIBLE);
                                }
                            });
                        });
                    } else {
                        Log.d(TAG, "onComplete: failed adding to friends");
                    }
                });
            }

            if (isPending) {
                final String date = DateFormat.getDateInstance().format(new Date());
                mFriendDB.child(mCurUser.getUid()).child(id).setValue(date)
                        .addOnCompleteListener(task -> mFriendDB.child(id).child(mCurUser.getUid()).setValue(date)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        mFriendQueryDB.child(mCurUser.getUid()).child(id).removeValue().addOnSuccessListener(aVoid -> {
                                            mFriendQueryDB.child(id).child(mCurUser.getUid()).removeValue();
                                            mUserAddToFriends.setVisibility(View.INVISIBLE);
                                            mUserDeclineFriend.setVisibility(View.VISIBLE);
                                            isFriend = true;
                                        });

                                    }
                                }));
            }
        });

        mUserDeclineFriend.setOnClickListener(view -> {
            mFriendQueryDB.child(mCurUser.getUid()).child(id).removeValue()
                    .addOnSuccessListener(aVoid -> mFriendQueryDB.child(id).child(mCurUser.getUid()).removeValue());

            if (isFriend) {
                mFriendDB.child(mCurUser.getUid()).child(id).removeValue()
                        .addOnSuccessListener(aVoid -> mFriendDB.child(id).child(mCurUser.getUid()).removeValue()
                                .addOnSuccessListener(aVoid1 -> {
                                    isFriend = false;
                                    isPending = false;
                                    mUserAddToFriends.setVisibility(View.VISIBLE);
                                    mUserAddToFriends.setText(R.string.add_to_friends);
                                    mUserDeclineFriend.setVisibility(View.INVISIBLE);
                                }));
            }
        });

        mFriendQueryDB.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(mCurUser.getUid())) {
                    if (dataSnapshot.child(mCurUser.getUid()).child("request type").getValue().toString().equals("sent")) {
                        mUserAddToFriends.setVisibility(View.VISIBLE);
                        mUserAddToFriends.setText("Accept Request");
                        isPending = true;
                        mUserDeclineFriend.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mUserOpenChat.setOnClickListener(view -> {
            Intent intent = new Intent(UserProfileActivity.this, ChatActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
            finish();
        });
    }

    private void populateUser() {
        mUserDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Picasso.get().load(dataSnapshot.child("image").getValue().toString()).placeholder(R.drawable.u1).into(mUserImage);
                mName.setText(dataSnapshot.child("name").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
