package com.example.lfgapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {
    private static final String TAG = "LOG";
    private FirebaseAuth mAuth;
    private EditText EmailTextView;
    private EditText PasswordTextView;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private EditText UsernameTextView;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            LoginComplete();
        }
        else{
            setContentView(R.layout.activity_login);
            Button Login = (Button) findViewById(R.id.login_button);
            Button Create = (Button) findViewById(R.id.Create_Button);
            UsernameTextView = (EditText) findViewById(R.id.username_text);
            EmailTextView = (EditText) findViewById(R.id.Email_text);
            PasswordTextView=(EditText) findViewById(R.id.Username_edittext);

            Login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (UsernameTextView.getText().toString().length() < 3||UsernameTextView.getText().toString().length() > 13) {
                        ((TextView)findViewById(R.id.warning_text)).setText("Username must be 3-13 chars");
                    }
                    else {
                        mAuth.signInWithEmailAndPassword(EmailTextView.getText().toString(), PasswordTextView.getText().toString())
                                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d(TAG, "signInWithEmail:success");
                                            currentUser = mAuth.getCurrentUser();
                                            LoginComplete();

                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                                            Toast.makeText(Login.this, "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();

                                        }

                                        // ...
                                    }
                                });
                    }
                }
            });

            Create.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (UsernameTextView.getText().toString().length() < 3||UsernameTextView.getText().toString().length() > 13) {
                        ((TextView)findViewById(R.id.warning_text)).setText("Username must be 3-13 chars");
                    }
                   else {
                        mAuth.createUserWithEmailAndPassword(EmailTextView.getText().toString(), PasswordTextView.getText().toString())
                                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d(TAG, "createUserWithEmail:success");
                                            currentUser = mAuth.getCurrentUser();

                                            User user = new User(currentUser.getUid(), currentUser.getEmail(), currentUser.getDisplayName(), currentUser.getPhotoUrl(),UsernameTextView.getText().toString());

                                            db.collection("users").document(user.getUID()).set(user);

                                            LoginComplete();

                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                            Toast.makeText(Login.this, "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();

                                        }

                                        // ...
                                    }
                                });
                    }
                }
            });
        }
    }

    private void LoginComplete() {
        Intent intent = new Intent(this, MainActivity.class);
        Log.d("LOGIN","LOGIN");

        startActivity(intent);
    }
}
