package am.romanbalayan.chatapp.EntryHall;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import am.romanbalayan.chatapp.R;


public class WhoIsActivity extends AppCompatActivity {

    private Button mLogInButton;
    private Button mSignUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_who_is);

        mLogInButton = findViewById(R.id.who_log_in);
        mSignUpButton = findViewById(R.id.who_sign_up);

        mLogInButton.setOnClickListener(view -> {
            Intent intent = new Intent(WhoIsActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        mSignUpButton.setOnClickListener(view -> {
            Intent intent = new Intent(WhoIsActivity.this, RegistrationActivity.class);
            startActivity(intent);
            finish();
        });
    }

}
