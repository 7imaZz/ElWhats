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
import com.shorbgy.elwhats.R;
import com.shorbgy.elwhats.pojo.User;
import com.shorbgy.elwhats.ui.activities.MessagesActivity;

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

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private final Context context;
    private ArrayList<User> users;

    public UserAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
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

        if (users.get(position).getImageUrl().equals("Default")){
            holder.profilePic.setImageResource(R.drawable.ic_person);
        }else {
            Glide.with(context)
                    .load(users.get(position).getImageUrl())
                    .placeholder(R.drawable.ic_person)
                    .into(holder.profilePic);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MessagesActivity.class);
            intent.putExtra("friend", users.get(position));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
