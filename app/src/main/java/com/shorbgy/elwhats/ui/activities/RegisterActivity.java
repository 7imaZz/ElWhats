package com.shorbgy.elwhats.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.shorbgy.elwhats.R;

import java.util.HashMap;
import java.util.Objects;
import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("NonConstantResourceId")
public class RegisterActivity extends AppCompatActivity {


    @BindView(R.id.username_et)
    MaterialEditText usernameEditText;
    @BindView(R.id.email_et)
    MaterialEditText emailEditText;
    @BindView(R.id.password_et)
    MaterialEditText passwordEditText;
    @BindView(R.id.reg_reg_btn)
    Button regButton;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private FirebaseAuth auth;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();

        regButton.setOnClickListener(v -> {
            String username = Objects.requireNonNull(usernameEditText.getText()).toString();
            String email = Objects.requireNonNull(emailEditText.getText()).toString();
            String password = Objects.requireNonNull(passwordEditText.getText()).toString();

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                Toast.makeText(this, "Please Fill All Fields", Toast.LENGTH_SHORT).show();
            }else if (password.length()<6){
                Toast.makeText(this, "Password Must Be > 6 Digits", Toast.LENGTH_SHORT).show();
            }else {
                registerWithEmailAndPassword(username, email, password);
            }
        });
    }

    public void registerWithEmailAndPassword(String username, String email, String password){

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                FirebaseUser firebaseUser = auth.getCurrentUser();
                assert firebaseUser != null;
                String userId = firebaseUser.getUid();

                database = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                HashMap<String, String> userMap = new HashMap<>();
                userMap.put("id", userId);
                userMap.put("username", username);
                userMap.put("imageUrl", "Default");

                database.setValue(userMap).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()){
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
            }else {
                Toast.makeText(this, "Cannot Login With This Email An Password",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}