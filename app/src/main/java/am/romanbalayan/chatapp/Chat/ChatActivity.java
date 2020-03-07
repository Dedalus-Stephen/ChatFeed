package am.romanbalayan.chatapp.Chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.BitmapCallback;
import com.zxy.tiny.common.BitmapResult;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import am.romanbalayan.chatapp.R;
import am.romanbalayan.chatapp.User.UserObject;
import am.romanbalayan.chatapp.User.UserProfileActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class ChatActivity extends AppCompatActivity {
    String mCurUser;
    private String id;
    private Toolbar toolbar;

    private DatabaseReference ref;
    private DatabaseReference mUserDB;
    private FirebaseAuth mAuth;
    private DatabaseReference mRetrieveMessageDB;
    private StorageReference mStorage;

    private CircleImageView image;
    private TextView name;

    private EditText message;
    private ImageButton mediaAdd;
    private ImageButton send;

    private ImageView preSent;

    private TextView dateWindow;

    private RecyclerView mMessagesRecView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager layoutManager;
    private String TAG = "ChatActivity";


    private String[] downloadUri = new String[1];

    private String mImgUID = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        id = getIntent().getStringExtra("id");

        ref = FirebaseDatabase.getInstance().getReference();
        mUserDB = FirebaseDatabase.getInstance().getReference().child("Users").child(id);
        mAuth = FirebaseAuth.getInstance();

        mStorage = FirebaseStorage.getInstance().getReference().child("sent");

        mCurUser = mAuth.getCurrentUser().getUid();

        mRetrieveMessageDB = FirebaseDatabase.getInstance().getReference().child("messages").child(mCurUser).child(id);

        toolbar = findViewById(R.id.chat_act_bar);
        setSupportActionBar(toolbar);
        image = findViewById(R.id.chat_tool_bar_thumb);
        name = findViewById(R.id.chat_tool_bar_name);
        preSent = findViewById(R.id.preSentImage);


        dateWindow = findViewById(R.id.date_window);

        message = findViewById(R.id.message_text);
        mediaAdd = findViewById(R.id.media_add_btn);
        send = findViewById(R.id.send_btn);


        mUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserObject user = dataSnapshot.getValue(UserObject.class);
                name.setText(user.getName());
                Picasso.get().load(user.getThumb()).placeholder(R.drawable.u1).into(image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);


        toolbar.setNavigationOnClickListener(view -> {
            clearStorage();
            preSent.setVisibility(View.GONE);
            finish();
        });

        mediaAdd.setOnClickListener(view -> sendImage());

        send.setOnClickListener(view -> {
            String text = message.getText().toString();

            if (!text.isEmpty() || (downloadUri[0] != null && !downloadUri[0].isEmpty())) {
                DatabaseReference pRef = FirebaseDatabase.getInstance().getReference().child("messages")
                        .child(mCurUser).child(id).push();

                String push_id = pRef.getKey();

                HashMap map = new HashMap();
                String curPath = "messages/" + mCurUser + "/" + id;
                String otherPath = "messages/" + id + "/" + mCurUser;
                if(!text.isEmpty()) map.put("message", text);
                if(downloadUri[0] != null && !downloadUri[0].isEmpty()){
                    map.put("media", downloadUri[0]);
                    map.put("type", "mixed");
                } else map.put("type", "text");
                map.put("seen", false);
                Calendar now = Calendar.getInstance();
                String time = now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE);
                map.put("time", time);
                String date = now.get(Calendar.DAY_OF_MONTH) + " " + now.get(Calendar.MONTH);
                map.put("date", date);
                map.put("sender", mCurUser);
                map.put("receiver", id);

                HashMap uMap = new HashMap();
                uMap.put(curPath + "/" + push_id, map);
                uMap.put(otherPath + "/" + push_id, map);

                String curPathChat = "Chat/" + mCurUser + "/" + id + "/" +"last";
                String otherPathChat = "Chat/" + id + "/" + mCurUser + "/" +"last";

                HashMap cMap = new HashMap();

                cMap.put(curPathChat, map);
                cMap.put(otherPathChat, map);

                ref.updateChildren(cMap, (databaseError, databaseReference) -> {
                    if(databaseError == null){
                        Log.d(TAG, "onCreate: chat updated");
                    }
                });

                ref.updateChildren(uMap, (databaseError, databaseReference) -> {
                    if (databaseError == null) {
                        message.getText().clear();
                    }
                });

                preSent.setVisibility(View.GONE);
                preSent.setImageResource(android.R.color.transparent);
                downloadUri[0] = null;
            }
        });

        final ArrayList<MessageObject> list = new ArrayList<>();
        initRecView(list);

        mRetrieveMessageDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                MessageObject message = dataSnapshot.getValue(MessageObject.class);
                list.add(message);
                dateWindow.setText(message.getDate());
                mAdapter.notifyDataSetChanged();
                mMessagesRecView.scrollToPosition(list.size() - 1);
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

    private void sendImage() {
        Intent imagePickerIntent = new Intent();
        imagePickerIntent.setType("image/*");
        imagePickerIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(imagePickerIntent, "SELECT IMAGE"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK){
            try {
                final InputStream imageStream = getContentResolver().openInputStream(data.getData());
                final Bitmap bitmap = BitmapFactory.decodeStream(imageStream);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 15, baos);

                byte[] arr = baos.toByteArray();

                preSent.setImageBitmap(bitmap);
                preSent.setVisibility(View.VISIBLE);

                String imgUID = UUID.randomUUID().toString();
                mStorage.child(imgUID + ".jpg").putBytes(arr).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            mStorage.child(imgUID + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadUri[0] = uri.toString();
                                    mImgUID = imgUID;
                                }
                            });
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void clearStorage(){
        if(mImgUID != null && !mImgUID.isEmpty() && preSent.getVisibility() != View.GONE) {
            mStorage.child(mImgUID).delete();
            preSent.setImageResource(android.R.color.transparent);
            preSent.setVisibility(View.GONE);
        }
    }

    private void initRecView(ArrayList<MessageObject> list) {
        mMessagesRecView = findViewById(R.id.messages_rec_view);

        mMessagesRecView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        mMessagesRecView.setLayoutManager(layoutManager);

        mAdapter = new ChatRecViewAdapter(list);
        mMessagesRecView.setAdapter(mAdapter);
    }
}
