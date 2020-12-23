package com.shorbgy.elwhats.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.shorbgy.elwhats.R;

import java.util.Objects;
import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("NonConstantResourceId")
public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.email_login_et)
    MaterialEditText emailEditText;
    @BindView(R.id.password_login_et)
    MaterialEditText passwordEditText;
    @BindView(R.id.login_btn)
    Button loginButton;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.forgot_tv)
    TextView forgotTextView;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(v -> {
            String email = Objects.requireNonNull(emailEditText.getText()).toString();
            String password = Objects.requireNonNull(passwordEditText.getText()).toString();
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                Toast.makeText(this, "Please Fill All Fields", Toast.LENGTH_SHORT).show();
            }else {
                login(email, password);
            }
        });

        forgotTextView.setOnClickListener(v -> setupForgotAlert());
    }

    public void login(String email, String password){
        ProgressDialog progressDialog
                = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait...");
        progressDialog.show();
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }else {
                Toast.makeText(LoginActivity.this, "Invalid Login", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        });
    }

    public void setupForgotAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Forgot Password?");
        builder.setMessage("Enter Your Email");

        EditText editText = new EditText(this);
        editText.setHint("Email Address");

        builder.setView(editText);
        builder.setPositiveButton("Confirm", (dialog, which) -> {
            if (editText.getText().toString().isEmpty()){
                Toast.makeText(LoginActivity.this,
                        "Please Enter Your Email Address", Toast.LENGTH_LONG).show();
            }else{

                ProgressDialog progressDialog
                        = new ProgressDialog(LoginActivity.this);
                progressDialog.setTitle("Please Wait...");
                progressDialog.show();

                auth.sendPasswordResetEmail(editText.getText().toString()).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Toast.makeText(LoginActivity.this,
                                "Please Check Your Email Address", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }else {
                        Toast.makeText(LoginActivity.this,
                                "Failed", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                });
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.create().show();
    }


}