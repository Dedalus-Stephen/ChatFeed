package am.romanbalayan.chatapp.Chat;

import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import am.romanbalayan.chatapp.R;

public class ChatRecViewAdapter extends RecyclerView.Adapter<ChatRecViewAdapter.ViewHolder> {

    private ArrayList<MessageObject> list;

    static FirebaseAuth mAuth = FirebaseAuth.getInstance();


    public ChatRecViewAdapter(ArrayList<MessageObject> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_message_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.text.setText(list.get(position).getMessage());
        if(list.get(position).getSender().equals(mAuth.getCurrentUser().getUid())) {
            holder.text_parent_linear.setGravity(Gravity.END);
        } else holder.text_parent_linear.setGravity(Gravity.START);

        holder.time.setText(list.get(position).getTime());

        if(list.get(position).getMedia() != null
            && !list.get(position).getMedia().isEmpty()
            && list.get(position).getMedia().startsWith("https://f")){
            Picasso.get().load(list.get(position).getMedia()).into(holder.sent_image);
            holder.sent_image.setVisibility(View.VISIBLE);

            holder.sent_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.getContext().startActivity(new Intent(view.getContext(), ChatFullImage.class)
                            .putExtra("src", list.get(position).getMedia()));
                }
            });

        } else holder.sent_image.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView text;
        TextView time;
        RelativeLayout text_parent_linear;
        ImageView sent_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.message_item_text);
            time = itemView.findViewById(R.id.message_item_time);
            text_parent_linear = itemView.findViewById(R.id.simple_message_text_parent);
            sent_image = itemView.findViewById(R.id.sent_image);
        }
    }
}
