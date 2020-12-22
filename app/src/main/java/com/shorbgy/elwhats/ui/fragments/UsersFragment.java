package com.shorbgy.elwhats.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
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
import com.shorbgy.elwhats.R;
import com.shorbgy.elwhats.adapters.UserAdapter;
import com.shorbgy.elwhats.pojo.User;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("NonConstantResourceId")
public class UsersFragment extends Fragment {

    private static final String TAG = "UsersFragment";


    @BindView(R.id.users_rv)
    RecyclerView usersRecyclerView;
    @BindView(R.id.search_sv)
    SearchView searchView;

    private UserAdapter adapter;
    ArrayList<User> users = new ArrayList<>();


    public UsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        adapter = new UserAdapter(requireContext(), users);

        return inflater.inflate(R.layout.fragment_users, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        expandSearchView();

        usersRecyclerView.setHasFixedSize(true);
        usersRecyclerView.setItemAnimator(new DefaultItemAnimator());
        usersRecyclerView.addItemDecoration(new DividerItemDecoration(requireContext(),
                DividerItemDecoration.VERTICAL));
        usersRecyclerView.setAdapter(adapter);

        readUsers();
        filterUsers();
    }

    private void readUsers(){


        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);

                    assert user != null;
                    Log.d(TAG, "onDataChange: "+user.getUsername());
                    assert firebaseUser != null;
                    if (!user.getId().equals(firebaseUser.getUid())){
                        users.add(user);
                        Log.d(TAG, "onDataChange: "+user.getId());
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

    private void filterUsers(){

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<User> filteredUsers = new ArrayList<>();

                if (!users.isEmpty()) {
                    for (User user : users) {
                        if (user.getUsername().toLowerCase().contains(newText.toLowerCase())) {
                            filteredUsers.add(user);
                        }
                    }
                    adapter.setUsers(filteredUsers);
                    adapter.notifyDataSetChanged();
                }
                return true;
            }
        });
    }

    private void expandSearchView(){
        searchView.setOnClickListener(v -> searchView.setIconified(false));
    }
}