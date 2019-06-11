package com.example.firebaseauthentication2;

import android.content.Intent;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;

    EditText username , password;

    ProgressBar progressbar;


    public void loginUser(View view){

        String email = username.getText().toString().trim();
        String psk = password.getText().toString().trim();

        if(email.isEmpty()) {
            username.setError("Email/Username is required");
            username.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            username.setError("Please enter valid email address");
            username.requestFocus();
            return;
        }
        if(psk.isEmpty()){
            password.setError("Password is required");
            password.requestFocus();
            return;
        }
        if(password.length() < 6){
            password.setError(" Minimum password length is 6");
            password.requestFocus();
            return;
        }

        progressbar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, psk)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            finish();
                            progressbar.setVisibility(View.GONE);
                            // Sign in success, update UI with the signed-in user's information
                            Log.i("Sign in status"," Successful");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this,user.getEmail()+" has been authenticated ",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this,LoggedIn.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            progressbar.setVisibility(View.GONE);
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.usernameEditText);
        password = findViewById(R.id.passwordEditText);
        progressbar = findViewById(R.id.loginProgressBar);
        mAuth = FirebaseAuth.getInstance();


        findViewById(R.id.loginButton).setOnClickListener(this);
        findViewById(R.id.goToSignUpButton).setOnClickListener(this);

    }

    public void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(MainActivity.this,LoggedIn.class));
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.loginButton :
                loginUser(v);
                break;

            case R.id.goToSignUpButton :
                finish();
                startActivity(new Intent(MainActivity.this,SignInActivity.class));
                break;

        }
    }
}
