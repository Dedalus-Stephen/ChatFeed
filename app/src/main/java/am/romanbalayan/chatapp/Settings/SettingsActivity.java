package am.romanbalayan.chatapp.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import am.romanbalayan.chatapp.EntryHall.WhoIsActivity;
import am.romanbalayan.chatapp.R;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

// TODO: 21.02.2020
public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "setting activity";
    private DatabaseReference mUserDB;


    private FirebaseUser mUser;

    private Toolbar toolbar;
    private TextView logOut;

    private FirebaseAuth mAuth;
    private StorageReference mStorage;

    private CircleImageView mImage;
    private TextView mName;
    private Button mChangeImage;
    private Button mChangeName;
    private TextInputEditText mChangeNameEdit;
    private ImageButton mChangeNameDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();

        mUser = mAuth.getCurrentUser();

        String uid = mUser.getUid();


        mUserDB = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);


        mImage = findViewById(R.id.avatar);
        mName = findViewById(R.id.settings_name);
        mChangeImage = findViewById(R.id.settings_change_avatar);
        mChangeName = findViewById(R.id.settings_change_name);
        mChangeNameEdit = findViewById(R.id.settings_change_name_edit);
        mChangeNameDone = findViewById(R.id.change_name_done_btn);

        mUserDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try{
                    String name = dataSnapshot.child("name").getValue().toString();
                    mName.setText(name);
                    String image = dataSnapshot.child("image").getValue().toString();
                    if(!image.equals("default")) Picasso.get().load(image).placeholder(R.drawable.u1).into(mImage);
                } catch (NullPointerException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        toolbar = findViewById(R.id.settings_tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        logOut = findViewById(R.id.settings_log_out);

        logOut.setOnClickListener(view -> {
            mAuth.signOut();
            Intent intent = new Intent(SettingsActivity.this, WhoIsActivity.class);
            startActivity(intent);
            finish();
        });

        mChangeImage.setOnClickListener(view -> changeImage());

        mImage.setOnClickListener(view -> changeImage());

        mChangeName.setOnClickListener(view -> {
            mChangeName.setVisibility(View.INVISIBLE);
            mChangeNameEdit.setVisibility(View.VISIBLE);
            mChangeNameDone.setVisibility(View.VISIBLE);
        });

        mChangeNameDone.setOnClickListener(view1 -> {
            if(mChangeNameEdit.getText() != null)
                mUserDB.child("name").setValue(mChangeNameEdit.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mChangeNameDone.setVisibility(View.INVISIBLE);
                        mChangeNameEdit.setVisibility(View.INVISIBLE);
                        mChangeName.setVisibility(View.VISIBLE);
                        mChangeNameEdit.getText().clear();
                        Toast.makeText(SettingsActivity.this, "NAME CHANGED", Toast.LENGTH_SHORT).show();
                    }
                });
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return true;
    }

    private void changeImage() {
        Intent imagePickerIntent = new Intent();
        imagePickerIntent.setType("image/*");
        imagePickerIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(imagePickerIntent, "SELECT IMAGE"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                final Uri resultUri = result.getUri();
                uploadAvatar(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void uploadAvatar(final Uri resultUri) {
        final String[] downloadURL = new String[1];
        final StorageReference path = mStorage.child("avatars").child(mUser.getUid() + ".jpg");
        try {
            Bitmap compressedImageFile = new Compressor(this)
                    .setMaxHeight(500)
                    .setMaxWidth(500)
                    .setQuality(200)
                    .compressToBitmap(new File(resultUri.getPath()));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] arr = baos.toByteArray();

            path.putBytes(arr).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    StorageReference path1 = mStorage.child("avatars").child(mUser.getUid()+".jpg");
                    path1.getDownloadUrl().addOnSuccessListener(uri -> {
                        downloadURL[0] = uri.toString();
                        updateDB(downloadURL);
                        uploadPortrait(resultUri);
                    });
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void uploadPortrait(Uri resultUri) {
        final StorageReference portrait_path = mStorage.child("avatars").child("portraits").child(mUser.getUid()+".jpg");
        File com_file = new File(resultUri.getPath());
        final String[] downloadURL = new String[1];
        try {
            Bitmap compressedImageFile = new Compressor(this).setMaxHeight(200).setMaxWidth(200).setQuality(65).compressToBitmap(com_file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 65, baos);
            byte[] arr = baos.toByteArray();

            UploadTask uploadTask = portrait_path.putBytes(arr);

            uploadTask.addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    portrait_path.getDownloadUrl().addOnSuccessListener(uri -> {
                       downloadURL[0] = uri.toString();
                       updateDB(downloadURL[0]);
                    });
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //to avoid concurrency out of memory error
    private void updateDB(String[] downloadURL){
        mUserDB.child("image").setValue(downloadURL[0]);
    }
    private void updateDB(String downloadURL){
        mUserDB.child("thumb").setValue(downloadURL);
    }

}
