package com.example.firebaseauthentication2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import static android.provider.MediaStore.Images.Media.getBitmap;

public class LoggedIn extends AppCompatActivity{

    private static final int CHOOSE_IMAGE = 101;
    EditText displayName;
    ImageView profilePic;
    Uri uriProfileImage;
    ProgressBar imageProgressBar;
    String profileImageUrl;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        displayName = findViewById(R.id.displayNameEditText);
        profilePic = findViewById(R.id.profilePicImageVIew);
        imageProgressBar = findViewById(R.id.imageProgressBar);
        mAuth = FirebaseAuth.getInstance();
        
        loadUserInformation();

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });

        findViewById(R.id.saveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInformation();
            }
        });

    }

    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(LoggedIn.this,MainActivity.class));
        }
    }

    private void loadUserInformation() {
        FirebaseUser user = mAuth.getCurrentUser();

        if(user!=null){
            if(user.getPhotoUrl()!=null){
                Glide.with(LoggedIn.this)
                        .load(user.getPhotoUrl().toString())
                        .into(profilePic);
            }
            if(user.getDisplayName()!=null){
                displayName.setText(user.getDisplayName());
            }
        }

    }

    private void saveUserInformation() {
        String name = displayName.getText().toString().trim();
        if(name.isEmpty()){
            displayName.setError("Display name is required");
            displayName.requestFocus();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null && profileImageUrl!=null){
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .setPhotoUri(Uri.parse(profileImageUrl))
                    .build();

            user.updateProfile(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(LoggedIn.this, "Profile updated", Toast.LENGTH_SHORT).show();
                                displayName.setText("");
                            }
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data!=null && data.getData()!=null){
            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uriProfileImage);
                profilePic.setImageBitmap(bitmap);

                uploadImageToFirebaseStorage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirebaseStorage() {
        final StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("profilepics/"+System.currentTimeMillis()+".jpg");
        if(uriProfileImage!=null){
            imageProgressBar.setVisibility(View.VISIBLE);
            profileImageRef.putFile(uriProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageProgressBar.setVisibility(View.GONE);
                   // profileImageUrl = profileImageRef.getDownloadUrl().toString();
                    profileImageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            profileImageUrl = task.getResult().toString();
                            Log.i("URL",profileImageUrl);
                        }
                    });
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    imageProgressBar.setVisibility(View.GONE);
                    Toast.makeText(LoggedIn.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showImageChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Selet Profile Image"),CHOOSE_IMAGE);
    }

}
