package am.romanbalayan.chatapp.Friends;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import am.romanbalayan.chatapp.R;
import am.romanbalayan.chatapp.User.UserObject;
import am.romanbalayan.chatapp.User.UserProfileActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class SearchRecViewAdapter extends RecyclerView.Adapter<SearchRecViewAdapter.ViewHolder> {

    private ArrayList<UserObject> list;

    public SearchRecViewAdapter(ArrayList<UserObject> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Picasso.get().load(list.get(position).getThumb()).placeholder(R.drawable.u1).into(holder.image);
        holder.userName.setText(list.get(position).getName());
        holder.parent.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), UserProfileActivity.class);
            intent.putExtra("id", list.get(position).getId());
            view.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView image;
        TextView userName;
        RelativeLayout parent;
        public ViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.portrait);
            userName = view.findViewById(R.id.user_name);
            parent = view.findViewById(R.id.user_item_parent);
        }
    }
}
