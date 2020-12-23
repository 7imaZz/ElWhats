package com.shorbgy.elwhats.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.shorbgy.elwhats.R;
import com.shorbgy.elwhats.adapters.UserAdapter;
import com.shorbgy.elwhats.pojo.Chat;
import com.shorbgy.elwhats.pojo.Token;
import com.shorbgy.elwhats.pojo.User;
import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("NonConstantResourceId")
public class ChatFragment extends Fragment {

    private static final String TAG = "UsersFragment";


    @BindView(R.id.chat_users_rv)
    RecyclerView usersRecyclerView;

    private UserAdapter adapter;
    private final ArrayList<User> users = new ArrayList<>();
    private final ArrayList<String> chattedUsers = new ArrayList<>();

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        adapter = new UserAdapter(requireContext(), users, true);

        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);


        usersRecyclerView.setHasFixedSize(true);
        usersRecyclerView.setItemAnimator(new DefaultItemAnimator());
        usersRecyclerView.addItemDecoration(new DividerItemDecoration(requireContext(),
                DividerItemDecoration.VERTICAL));
        usersRecyclerView.setAdapter(adapter);

        findChattedUsers();

        updateToken(FirebaseInstanceId.getInstance().getToken());
    }

    public void findChattedUsers(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chat");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    assert chat != null && firebaseUser!=null;
                    if (chat.getSender().equals(firebaseUser.getUid())){
                        if (!chattedUsers.contains(chat.getReceiver())) {
                            Log.d(TAG, "Chatted: "+chat.getReceiver());
                            chattedUsers.add(chat.getReceiver());
                        }
                    }

                    if (chat.getReceiver().equals(firebaseUser.getUid())){
                        if (!chattedUsers.contains(chat.getSender())) {
                            Log.d(TAG, "Chatted: "+chat.getSender());
                            chattedUsers.add(chat.getSender());
                        }
                    }
                }

                readUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void readUsers(){


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);

                    assert user != null;
                    if (chattedUsers.contains(user.getId())){
                        users.add(user);
                    }
                }

                adapter.setUsers(users);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateToken(String token){

        DatabaseReference tokenReference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        tokenReference.child(Objects.requireNonNull(
                FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(token1);
    }
}