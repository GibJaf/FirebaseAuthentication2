package com.example.firebaseauthentication2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

    public void registerUser(View view){
        String email = username.getText().toString().trim();
        String psk = password.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(email, psk)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i("User creation status => ", "createUserWithEmail:success");
                            //FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this ,"User registered successfully",Toast.LENGTH_SHORT).show();
                        } else {

                            if(task.getException() instanceof FirebaseAuthUserCollisionException)
                                Toast.makeText(MainActivity.this,"This username is already registered",Toast.LENGTH_SHORT).show();
                            else {
                                // If sign in fails, display a message to the user.
                                Log.i("User creation status => ", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(MainActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }


                    }
                });
    }

    public void loginUser(View view){

        String email = username.getText().toString().trim();
        String psk = password.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(email, psk)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i("Sign in status"," Successful");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this,user.getEmail()+" has been authenticated ",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this,LoggedIn.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
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
        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.loginButton).setOnClickListener(this);

        startActivity(new Intent(MainActivity.this,SignInActivity.class));
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.loginButton :
                loginUser(v);
                break;
        }
    }
}
