package com.shorbgy.elwhats.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shorbgy.elwhats.R;
import com.shorbgy.elwhats.pojo.Chat;
import com.shorbgy.elwhats.pojo.User;
import com.shorbgy.elwhats.ui.activities.MessagesActivity;
import com.shorbgy.elwhats.utils.ImageDialog;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{

    @SuppressLint("NonConstantResourceId")
    public static class UserViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.user_profile_img)
        CircleImageView profilePic;
        @BindView(R.id.user_username_tv)
        TextView usernameTextView;
        @BindView(R.id.user_last_message_tv)
        TextView lastMessageTextView;
        @BindView(R.id.status_img)
        CircleImageView statusImageView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private String lastMessage = "";

    private final Context context;
    private ArrayList<User> users;
    private final boolean isChat;

    public UserAdapter(Context context, ArrayList<User> users, boolean isChat) {
        this.context = context;
        this.users = users;
        this.isChat = isChat;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public UserAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position) {

        holder.usernameTextView.setText(users.get(position).getUsername());

        if (isChat) {
            getLastMessage(holder.lastMessageTextView, users.get(position).getId());
        }

        if (users.get(position).getImageUrl().equals("Default")){
            holder.profilePic.setImageResource(R.mipmap.ic_person);
        }else {
            Glide.with(context)
                    .load(users.get(position).getImageUrl())
                    .placeholder(R.mipmap.ic_person)
                    .into(holder.profilePic);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MessagesActivity.class);
            intent.putExtra("friend", users.get(position));
            context.startActivity(intent);
        });

        holder.profilePic.setOnClickListener(v ->
                ImageDialog.popupImageDialog(context, users.get(position).getImageUrl()));

        if (users.get(position).getStatus().equals("online")){
            holder.statusImageView.setImageResource(R.drawable.online_bg);
        }else {
            holder.statusImageView.setImageResource(R.drawable.offline_bg);
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    private void getLastMessage(TextView textView, String friendId){

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chat");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);

                    assert chat != null;
                    assert currentUser != null;
                    if ((chat.getSender().equals(currentUser.getUid()) && chat.getReceiver().equals(friendId))
                            || (chat.getReceiver().equals(currentUser.getUid()) && chat.getSender().equals(friendId))){
                        lastMessage = chat.getMessage();
                    }
                }

                textView.setText(lastMessage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
