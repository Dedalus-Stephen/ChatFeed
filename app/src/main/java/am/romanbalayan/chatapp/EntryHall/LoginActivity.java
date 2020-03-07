package am.romanbalayan.chatapp.EntryHall;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;


import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import am.romanbalayan.chatapp.MainActivity;
import am.romanbalayan.chatapp.R;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference ref;

    private static final String TAG = "login activity";
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Button mButton;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mToolbar = findViewById(R.id.log_tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference().child("Users");

        mEmail = findViewById(R.id.log_email);
        mPassword = findViewById(R.id.log_password);
        mButton = findViewById(R.id.log_create_button);

        mButton.setOnClickListener(view -> {
            String email = mEmail.getEditText().getText().toString();
            String password = mPassword.getEditText().getText().toString();

            if(!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){
                loginUser(email, password);
            }
        });

        mToolbar.setNavigationOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, WhoIsActivity.class));
            finish();
        });


    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                ref.child(mAuth.getCurrentUser().getUid()).child("device token").setValue(FirebaseInstanceId.getInstance().getToken());
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Log.d(TAG, "on fail: failed logging in");
            }
        });
    }
}
