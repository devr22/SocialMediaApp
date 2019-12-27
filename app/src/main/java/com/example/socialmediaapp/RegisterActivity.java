package com.example.socialmediaapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    //Widgets
    private EditText emailEt;
    private EditText passwordEt;
    private EditText confirmPasswordEt;
    private Button registerBtn;
    private TextView haveAccountTextView;

    ProgressDialog progressDialog;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //ActionBar and its title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Create Account");

        //enable back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        init();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });

        haveAccountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

    }

    private void init(){

        emailEt = findViewById(R.id.emailText);
        passwordEt = findViewById(R.id.passwordText);
        confirmPasswordEt  = findViewById(R.id.confirmPasswordText);
        registerBtn = findViewById(R.id.register_register_btn);
        haveAccountTextView = findViewById(R.id.have_accountTextView);

        mAuth = FirebaseAuth.getInstance();

        //Progressbar
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering User...");
    }

    @Override
    public boolean onSupportNavigateUp() {

        onBackPressed();    //go to previous activity
        return super.onSupportNavigateUp();
    }

    private void createAccount(){

        String password = passwordEt.getText().toString().trim();
        String confirmPassword = confirmPasswordEt.getText().toString().trim();

        //password matching
        if (password.equals(confirmPassword)){
            beginRegisteration();
        }
        else{
            //set Error and focus to confirmPasswordEditText

            confirmPasswordEt.setError("Entered text does not matches password field");
            confirmPasswordEt.setFocusable(true);
        }

    }

    private void beginRegisteration(){

        String email = emailEt.getText().toString().trim();
        String password = passwordEt.getText().toString().trim();

        //validate
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

            //set Error and focus to emailEditText
            emailEt.setError("Invalid Email");
            emailEt.setFocusable(true);
        }
        else if (password.length() < 6){

            //set Error and focus to passwordEditText
            passwordEt.setError("Password length can't be less than 6 characters");
            passwordEt.setFocusable(true);
        }
        else {
            registerUser(email, password);
        }

    }

    private void registerUser(String email, String password) {

        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            // Sign in success, dismiss dialog
                            Log.d(TAG, "createUserWithEmail:success");
                            progressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();

                            storeUserInfo(user);

                            Toast.makeText(RegisterActivity.this, "Registered" + user.getEmail(), Toast.LENGTH_SHORT).show();

                            // start profile activity
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();

                        }
                        else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {

                //dismiss progress dialog and get user and show the error message
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void storeUserInfo(FirebaseUser user){

        // get user email and uid
        String email = user.getEmail();
        String uid = user.getUid();

        //when user is registered store user info in FireBase realtime database using HashMap
        HashMap<Object, String> hashMap = new HashMap<>();

        //put info in HashMap
        hashMap.put("email", email);
        hashMap.put("uid", uid);
        hashMap.put("name", "");      // will be added later using edit profile
        hashMap.put("phone", "");
        hashMap.put("image", "");
        hashMap.put("coverPhoto", "");

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //path to store user data
        DatabaseReference reference = database.getReference("Users");

        reference.child(uid).setValue(hashMap);

    }

}










