package com.shorbgy.elwhats.ui.activities;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shorbgy.elwhats.R;
import com.shorbgy.elwhats.adapters.MessagesAdapter;
import com.shorbgy.elwhats.pojo.Chat;
import com.shorbgy.elwhats.pojo.User;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

@SuppressLint("NonConstantResourceId")
public class MessagesActivity extends AppCompatActivity {

    @BindView(R.id.message_profile_img)
    CircleImageView profilePic;
    @BindView(R.id.message_username_tv)
    TextView friendNameTextView;
    @BindView(R.id.message_toolbar)
    Toolbar toolbar;
    @BindView(R.id.messages_rv)
    RecyclerView messagesRecyclerView;
    @BindView(R.id.message_et)
    EditText messageEditText;
    @BindView(R.id.send_fab)
    FloatingActionButton sendFab;

    private User friend;

    private MessagesAdapter adapter;
    private final ArrayList<Chat> chats = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        ButterKnife.bind(this);

        friend = getIntent().getParcelableExtra("friend");

        initializeUi();

        adapter = new MessagesAdapter(this, chats, friend.getImageUrl());
        messagesRecyclerView.setHasFixedSize(true);
        messagesRecyclerView.setAdapter(adapter);

        readChat();

        sendFab.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(messageEditText.getText().toString())){
                sendMessage(FirebaseAuth.getInstance().getUid(), friend.getId(), messageEditText.getText().toString());
            }
        });

    }

    private void initializeUi(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        friendNameTextView.setText(friend.getUsername());
        if (!friend.getImageUrl().equals("Default")){
            Glide.with(this)
                    .load(friend.getImageUrl())
                    .into(profilePic);
        }
    }

    private void readChat(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chat");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chats.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);

                    if ((chat.getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            && chat.getReceiver().equals(friend.getId()))
                            || (chat.getReceiver().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            && chat.getSender().equals(friend.getId()))){
                        chats.add(chat);
                    }
                }


                adapter.setChats(chats);
                adapter.notifyDataSetChanged();

                messagesRecyclerView.scrollToPosition(chats.size()-1);
                messageEditText.setText("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage(String sender, String receiver, String message){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, String> messageMap = new HashMap<>();
        messageMap.put("sender", sender);
        messageMap.put("receiver", receiver);
        messageMap.put("message", message);

        reference.child("Chat").push().setValue(messageMap);

        Date currentDate = new Date();
        long currentTime = currentDate.getTime();

        reference.child("Users").child(friend.getId())
                .child("date").setValue(currentTime);
        reference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("date").setValue(currentTime);
    }
}