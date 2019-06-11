package com.example.firebaseauthentication2;

import android.content.Intent;
import android.icu.text.UnicodeSetSpanner;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener{

    EditText editTextUsername , editTextPassword;
    private FirebaseAuth mAuth;
    ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        editTextUsername = findViewById(R.id.username);
        editTextPassword = findViewById(R.id.password);
        editTextPassword.setText(""); // don't know why but it was being coming filled at start by itself

        findViewById(R.id.signUpButton).setOnClickListener(this);
        findViewById(R.id.goToLoginButton).setOnClickListener(this);
        progressbar = findViewById(R.id.progressbar);

        mAuth = FirebaseAuth.getInstance();
    }

    private void registerUser(){
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

             if(username.isEmpty()) {
                 editTextUsername.setError("Email/Username is required");
                 editTextUsername.requestFocus();
                 return;
             }
             if(!Patterns.EMAIL_ADDRESS.matcher(username).matches()){
                 editTextUsername.setError("Please enter valid email address");
                 editTextUsername.requestFocus();
                 return;
             }
             if(password.isEmpty()){
                 editTextPassword.setError("Password is required");
                 editTextPassword.requestFocus();
                 return;
             }
             if(password.length() < 6){
                 editTextPassword.setError(" Minimum password length is 6");
                 editTextPassword.requestFocus();
                 return;
             }

             progressbar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            finish();
                            // Sign in success, update UI with the signed-in user's information
                            progressbar.setVisibility(View.GONE);
                            Log.i("User registration status => ","Successful");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(SignInActivity.this,user.getEmail()+" registered successfully ",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignInActivity.this,LoggedIn.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            progressbar.setVisibility(View.GONE);
                            if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                Toast.makeText(SignInActivity.this," This email is already registered ",Toast.LENGTH_SHORT).show();
                            }else {
                                Log.i("User registration status => ", task.getException() + "");
                                Toast.makeText(SignInActivity.this, "Unsuccessful registration => "+task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        // ...
                    }
                }) ;

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.signUpButton :
                registerUser();
                break;
            case R.id.goToLoginButton :
                finish();
                break;
        }
    }
}
