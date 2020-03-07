package am.romanbalayan.chatapp.Chat;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.net.Inet4Address;
import java.util.ArrayList;

import am.romanbalayan.chatapp.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    private ArrayList<MessageObject> list;

    private DatabaseReference userRef;

    String mCurUser;


    public ChatListAdapter(ArrayList<MessageObject> list){
        this.list = list;
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mCurUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_chat_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.parent.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), ChatActivity.class);
            intent.putExtra("id", list.get(position).getId());
            view.getContext().startActivity(intent);
        });

        if(list.get(position).getType().equals("mixed")){
            holder.msg.setTextColor(holder.itemView.getResources().getColor(android.R.color.darker_gray));
            holder.msg.setText("MEDIA");
        } else holder.msg.setText(list.get(position).getMessage());

        userRef.child(list.get(position).getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    Picasso.get().load(dataSnapshot.child("thumb").getValue().toString()).placeholder(R.drawable.u1).into(holder.thumb);
                    holder.name.setText(dataSnapshot.child("name").getValue().toString());
                } catch (NullPointerException e){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private RelativeLayout parent;
        private CircleImageView thumb;
        private TextView name;
        private TextView msg;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.chat_item_parent);
            thumb = itemView.findViewById(R.id.chat_item_thumb);
            name = itemView.findViewById(R.id.chat_item_name);
            msg = itemView.findViewById(R.id.chat_item_msg);
        }
    }
}
