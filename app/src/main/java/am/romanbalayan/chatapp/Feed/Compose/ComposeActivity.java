package am.romanbalayan.chatapp.Feed.Compose;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import am.romanbalayan.chatapp.R;
import id.zelory.compressor.Compressor;

public class ComposeActivity extends AppCompatActivity {

    private ImageButton mComposeDoneBtn;
    private EditText mText;
    private TextView mComposeHint;
    private ImageView mComposeImage;

    private Toolbar toolbar;

    private DatabaseReference mFeedRootRef;

    private StorageReference mStorage;

    private String mCurUser;

    private Uri imageLocalUri = null;
    private String[] downloadURL = new String[1];

    ReentrantLock lock = new ReentrantLock();

    Calendar now;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        toolbar = findViewById(R.id.compose_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        now = Calendar.getInstance();

        mCurUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mStorage = FirebaseStorage.getInstance().getReference();

        mComposeDoneBtn = findViewById(R.id.compose_done);
        mText = findViewById(R.id.compose_text);
        mComposeHint = findViewById(R.id.compose_hint);
        mComposeImage = findViewById(R.id.compose_image);


        mFeedRootRef = FirebaseDatabase.getInstance().getReference().child("Feed");

        mComposeHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        mComposeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        mComposeDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    upload(imageLocalUri);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "SELECT IMAGE"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            imageLocalUri = uri;
            Picasso.get().load(uri).into(mComposeImage);

            mComposeHint.setVisibility(View.INVISIBLE);
        }
    }


    private void upload(Uri uri) {
        if (uri == null) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("text", mText.getText().toString());
            mFeedRootRef.child(mCurUser).child(now.get(Calendar.YEAR) +
                    ":" + now.get(Calendar.MONTH) +
                    ":" + now.get(Calendar.DAY_OF_MONTH) +
                    ":" + now.get(Calendar.HOUR_OF_DAY) + ":" +
                    now.get(Calendar.MINUTE)).updateChildren(map, (databaseError, databaseReference) -> {
                if (databaseError == null) {
                    Log.d("Compose Activity", "onCreate: chat updated");
                }
            });
            return;
        }

        String imageUID = UUID.randomUUID().toString();
        final StorageReference path = mStorage.child("feedImgs").child(mCurUser).child(imageUID + ".jpg");

        try {
            final InputStream imageStream = getContentResolver().openInputStream(uri);
            final Bitmap bitmap = BitmapFactory.decodeStream(imageStream);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);

            byte[] arr = baos.toByteArray();

            path.putBytes(arr).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    StorageReference path1 = mStorage.child("feedImgs").child(mCurUser).child(imageUID + ".jpg");
                    path1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri1) {
                            downloadURL[0] = uri1.toString();
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("text", mText.getText().toString());
                            map.put("image", downloadURL[0]);
                            mFeedRootRef.child(mCurUser).child(now.get(Calendar.YEAR) +
                                    ":" + now.get(Calendar.MONTH) +
                                    ":" + now.get(Calendar.DAY_OF_MONTH) +
                                    ":" + now.get(Calendar.HOUR_OF_DAY) + ":" +
                                    now.get(Calendar.MINUTE)).updateChildren(map, (databaseError, databaseReference) -> {
                                if (databaseError == null) {
                                    Log.d("Compose Activity", "onCreate: chat updated");
                                }
                            });
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

