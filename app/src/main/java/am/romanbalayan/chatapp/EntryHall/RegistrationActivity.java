package am.romanbalayan.chatapp.EntryHall;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

import am.romanbalayan.chatapp.MainActivity;
import am.romanbalayan.chatapp.R;

public class RegistrationActivity extends AppCompatActivity {
    private static final String TAG = "User Auth";
    private FirebaseAuth mAuth;

    private TextInputLayout mName;
    private TextInputLayout mEMail;
    private TextInputLayout mPassword;

    private Toolbar mToolBar;

    private Button createButton;

    private ProgressDialog mProgress;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();

        mName = findViewById(R.id.reg_display_name);
        mEMail = findViewById(R.id.reg_email);
        mPassword = findViewById(R.id.reg_password);

        mToolBar = findViewById(R.id.reg_tool_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgress = new ProgressDialog(this);

        createButton = findViewById(R.id.reg_create_button);

        createButton.setOnClickListener(view -> {
            String displayName = mName.getEditText().getText().toString();
            String eMail = mEMail.getEditText().getText().toString();
            String password = mPassword.getEditText().getText().toString();

            if(!TextUtils.isEmpty(displayName) || !TextUtils.isEmpty(eMail) || !TextUtils.isEmpty(password)) {
                mProgress.setTitle("Register user");
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.show();
                registerUser(displayName, eMail, password);
            }
        });

        mToolBar.setNavigationOnClickListener(view -> {
            startActivity(new Intent(RegistrationActivity.this, WhoIsActivity.class));
            finish();
        });

    }

    private void registerUser(final String name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmail:success");

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


                        if (user != null) {
                            String uid = user.getUid();

                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                            HashMap<String, String> map = new HashMap<>();

                            map.put("name", name);
                            map.put("image", "default");
                            map.put("thumb", "default");
                            map.put("device token", FirebaseInstanceId.getInstance().getToken());

                            mDatabase.setValue(map).addOnCompleteListener(task1 -> {
                                if(task1.isSuccessful()) {
                                    mProgress.dismiss();
                                    Intent mainIntent = new Intent(RegistrationActivity.this, MainActivity.class);
                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mainIntent);
                                    finish();
                                }
                            });
                        } else {
                            Log.d(TAG, "onComplete: user is null");
                        }

                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        mProgress.hide();
                        Toast.makeText(RegistrationActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }

                });
    }
}
