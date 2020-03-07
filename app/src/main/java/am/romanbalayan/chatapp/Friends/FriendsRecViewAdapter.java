package am.romanbalayan.chatapp.Friends;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import am.romanbalayan.chatapp.R;
import am.romanbalayan.chatapp.User.UserObject;
import am.romanbalayan.chatapp.User.UserProfileActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsRecViewAdapter extends RecyclerView.Adapter<FriendsRecViewAdapter.ViewHolder> {

    private ArrayList<UserObject> list;

    private DatabaseReference mFriendDB;
    private FirebaseUser mCurUser;
    private DatabaseReference mFriendQueryDB;

    public FriendsRecViewAdapter(ArrayList<UserObject> list) {
        this.list = list;
        mFriendDB = FirebaseDatabase.getInstance().getReference().child("Friends");
        mCurUser = FirebaseAuth.getInstance().getCurrentUser();
        mFriendQueryDB = FirebaseDatabase.getInstance().getReference().child("FriendReqDB");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_friend_user_item, parent, false);
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

        final String id = list.get(position).getId();
        if(list.get(position).isPending()){
            holder.request.setText("ACCEPT REQUEST");
            holder.request.setOnClickListener(view -> {
                final String date = DateFormat.getDateInstance().format(new Date());
                mFriendDB.child(mCurUser.getUid()).child(id).setValue(date)
                        .addOnCompleteListener(task -> mFriendDB.child(id).child(mCurUser.getUid()).setValue(date)
                                .addOnCompleteListener(task1 -> {
                                    if(task1.isSuccessful()) {
                                        mFriendQueryDB.child(mCurUser.getUid()).child(id).removeValue()
                                                .addOnSuccessListener(aVoid -> {
                                                    mFriendQueryDB.child(id).child(mCurUser.getUid()).removeValue();
                                                    holder.request.setText(R.string.decline_request);
                                                    list.get(position).setPending(false);
                                                });

                                    }
                                }));
            });
        }

        if(!list.get(position).isPending()){
            holder.request.setOnClickListener(view -> mFriendDB.child(mCurUser.getUid()).child(id).removeValue()
                    .addOnSuccessListener(aVoid -> mFriendDB.child(id).child(mCurUser.getUid()).removeValue()
                            .addOnSuccessListener(aVoid1 -> {
                                Toast toast = Toast.makeText(view.getContext(),
                                        "FRIEND DELETED", Toast.LENGTH_SHORT);
                                toast.show();
                            })));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView image;
        TextView userName;
        Button request;
        RelativeLayout parent;
        public ViewHolder(View view){
            super(view);
            parent = view.findViewById(R.id.friend_item_parent);
            image = view.findViewById(R.id.portrait_friend);
            userName = view.findViewById(R.id.user_name_friend);
            request = view.findViewById(R.id.request_friend);
        }
    }
}
