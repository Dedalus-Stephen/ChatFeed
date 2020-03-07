package am.romanbalayan.chatapp.Feed;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.sql.SQLOutput;
import java.util.ArrayList;

import am.romanbalayan.chatapp.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class FeedRecViewAdapter extends RecyclerView.Adapter<FeedRecViewAdapter.ViewHolder> {

    private ArrayList<FeedObject> list;

    private DatabaseReference mUsersDB;
    private Picasso picasso;
    public FeedRecViewAdapter(ArrayList<FeedObject> list) {
        this.list = list;
        mUsersDB = FirebaseDatabase.getInstance().getReference().child("Users");
        picasso = Picasso.get();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_cardview_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        mUsersDB.child(list.get(position).getPosterId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    picasso.load(dataSnapshot.child("thumb").getValue().toString()).placeholder(R.drawable.u1).into(holder.feedThumb);
                    holder.feedPosterName.setText(dataSnapshot.child("name").getValue().toString());
                } catch (NullPointerException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        picasso.load(list.get(position).getImage()).into(holder.feedImage);
        holder.feedText.setText(list.get(position).getText());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView feedImage;
        TextView feedText;
        CircleImageView feedThumb;
        TextView feedPosterName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            feedImage = itemView.findViewById(R.id.feed_card_image);
            feedText = itemView.findViewById(R.id.feed_text);
            feedThumb = itemView.findViewById(R.id.feed_thumb);
            feedPosterName = itemView.findViewById(R.id.feed_poster_name);
        }
    }
}
