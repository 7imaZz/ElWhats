package com.shorbgy.elwhats.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shorbgy.elwhats.R;
import com.shorbgy.elwhats.adapters.MyFragmentStateAdapter;
import com.shorbgy.elwhats.pojo.User;
import com.shorbgy.elwhats.utils.GlideUtils;
import com.shorbgy.elwhats.utils.ImageDialog;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

@SuppressLint("NonConstantResourceId")
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_toolbar)
    Toolbar toolbar;
    @BindView(R.id.profile_img)
    CircleImageView profileImage;
    @BindView(R.id.username_tv)
    TextView usernameTextView;
    @BindView(R.id.tab)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager2 viewPager2;

    private MyFragmentStateAdapter myFragmentStateAdapter;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());

        initializeTabLayout();

        setupUserData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            assert data != null;
            Uri fileUri = data.getData();
            profileImage.setImageURI(fileUri);
            //You can get File object from intent
            File file = ImagePicker.Companion.getFile(data);

            uploadFile(fileUri);
            assert file != null;
            Log.d("TAG", "onActivityResult: "+file.toString());

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.Companion.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeTabLayout(){
        myFragmentStateAdapter = new MyFragmentStateAdapter(this);
        viewPager2.setAdapter(myFragmentStateAdapter);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            tab.setText(myFragmentStateAdapter.getPageTitle(position));
            viewPager2.setCurrentItem(0);
        });

        tabLayoutMediator.attach();
    }

    private void updateStatus(String status){

        HashMap<String, Object> statusMap = new HashMap<>();
        statusMap.put("status", status);

        reference.updateChildren(statusMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateStatus("offline");
    }

    private void setupUserData(){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                usernameTextView.setText(user.getUsername());

                if (user.getImageUrl().equals("Default")){
                    profileImage.setImageResource(R.mipmap.ic_person);
                }else {
                    if (GlideUtils.isValidContextForGlide(MainActivity.this)){
                        Glide.with(MainActivity.this)
                                .load(user.getImageUrl())
                                .centerCrop()
                                .into(profileImage);
                    }else {
                        Glide.with(getApplicationContext())
                                .load(user.getImageUrl())
                                .centerCrop()
                                .into(profileImage);
                    }
                }

                profileImage.setOnClickListener(v ->
                        ImageDialog.popupImageDialog(MainActivity.this, user.getImageUrl()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.sign_out_menu){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, StartActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    private void uploadFile(Uri filePath) {

        // Code for showing progressDialog while uploading
        ProgressDialog progressDialog
                = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        // Defining the child of storageReference
        StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());

        ref.putFile(filePath).addOnSuccessListener(taskSnapshot -> {
            progressDialog.dismiss();
            Toast.makeText(MainActivity.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
            ref.getDownloadUrl().addOnCompleteListener(task -> {
                Log.d("TAG", "onComplete: "+task.getResult());
                reference.child("imageUrl").setValue(Objects.requireNonNull(task.getResult()).toString());
            });

        }).addOnFailureListener(e -> {
                // Error, Image not uploaded
                progressDialog.dismiss();
                Log.d("TAG", "onFailure: "+e.getMessage());
            });
    }
}