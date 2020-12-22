package com.shorbgy.elwhats.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.shorbgy.elwhats.R;
import com.shorbgy.elwhats.pojo.Chat;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder> {

    public static final int SENDER = 0;
    public static final int RECEIVER = 1;

    @SuppressLint("NonConstantResourceId")
    public static class MessagesViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.message_img)
        CircleImageView profilePic;
        @BindView(R.id.message_tv)
        TextView messageTextView;

        public MessagesViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private final Context context;
    private ArrayList<Chat> chats;
    private final String imageUrl;

    public MessagesAdapter(Context context, ArrayList<Chat> chats, String imageUrl) {
        this.context = context;
        this.chats = chats;
        this.imageUrl = imageUrl;
    }

    public void setChats(ArrayList<Chat> chats) {
        this.chats = chats;
    }

    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == SENDER){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sender_item, parent, false);
        }else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.receiver_item, parent, false);
        }
        return new MessagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesViewHolder holder, int position) {

        holder.messageTextView.setText(chats.get(position).getMessage());

        if (imageUrl.equals("Default")){
            holder.profilePic.setImageResource(R.drawable.ic_person);
        }else {
            Glide.with(context)
                    .load(imageUrl)
                    .into(holder.profilePic);
        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(chats.get(position).getSender())){
            return SENDER;
        }else {
            return RECEIVER;
        }
    }
}
