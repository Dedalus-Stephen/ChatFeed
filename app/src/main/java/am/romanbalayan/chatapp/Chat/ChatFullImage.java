package am.romanbalayan.chatapp.Chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import am.romanbalayan.chatapp.R;

public class ChatFullImage extends AppCompatActivity {
    private ImageView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_full_image);

        mImage = findViewById(R.id.chat_full_image);
        Picasso.get().load(getIntent().getStringExtra("src")).into(mImage);
    }
}
