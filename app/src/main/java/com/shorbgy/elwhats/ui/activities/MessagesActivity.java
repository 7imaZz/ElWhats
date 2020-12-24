package com.shorbgy.elwhats.ui.activities;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shorbgy.elwhats.R;
import com.shorbgy.elwhats.adapters.MessagesAdapter;
import com.shorbgy.elwhats.notification.Client;
import com.shorbgy.elwhats.notification.NotificationApi;
import com.shorbgy.elwhats.pojo.Chat;
import com.shorbgy.elwhats.pojo.Data;
import com.shorbgy.elwhats.pojo.MyResponse;
import com.shorbgy.elwhats.pojo.Sender;
import com.shorbgy.elwhats.pojo.Token;
import com.shorbgy.elwhats.pojo.User;
import com.shorbgy.elwhats.utils.ImageDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    NotificationApi notificationApi;
    boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        ButterKnife.bind(this);

        friend = getIntent().getParcelableExtra("friend");

        notificationApi = Client.getClient("https://fcm.googleapis.com/").create(NotificationApi.class);

        initializeUi();

        adapter = new MessagesAdapter(this, chats, friend.getImageUrl());
        messagesRecyclerView.setHasFixedSize(true);
        messagesRecyclerView.setAdapter(adapter);

        readChat();

        sendFab.setOnClickListener(v -> {
            notify = true;
            if (!TextUtils.isEmpty(messageEditText.getText().toString())){
                sendMessage(FirebaseAuth.getInstance().getUid(), friend.getId(), messageEditText.getText().toString());
            }
        });

    }

    private void initializeUi(){
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        friendNameTextView.setText(friend.getUsername());
        if (!friend.getImageUrl().equals("Default")){
            Glide.with(this)
                    .load(friend.getImageUrl())
                    .into(profilePic);

            profilePic.setOnClickListener(v ->
                    ImageDialog.popupImageDialog(MessagesActivity.this, friend.getImageUrl()));
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

                    assert chat != null;
                    if ((chat.getSender().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
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

        reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                if (notify) {
                    sendNotification(receiver, user.getUsername(), message);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendNotification(String receiver, String username, String message) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Token token = dataSnapshot.getValue(Token.class);
                    Data data = new Data(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(),
                            username+": "+message, "New Message", friend.getId(), R.mipmap.logo);
                    assert token != null;
                    Sender sender = new Sender(data, token.getToken());
                    notificationApi.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<MyResponse> call, @NonNull Response<MyResponse> response) {
                            if (response.code() == 200){
                                MyResponse myResponse = response.body();
                                assert myResponse != null;
                                if (myResponse.success != 1){
                                    Toast.makeText(MessagesActivity.this, "Failed",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<MyResponse> call, @NonNull Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateStatus(String status){

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseUser.getUid());

        HashMap<String, Object> statusMap = new HashMap<>();
        statusMap.put("status", status);

        reference.updateChildren(statusMap);
    }

    private void saveCurrentUser(String userId){
        SharedPreferences.Editor editor = getSharedPreferences("prefs", Context.MODE_PRIVATE).edit();
        editor.putString("current_user", userId);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatus("online");
        saveCurrentUser(friend.getId());
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateStatus("offline");
        saveCurrentUser("none");
    }
}