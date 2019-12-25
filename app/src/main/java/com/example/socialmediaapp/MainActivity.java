package com.example.socialmediaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    //widgets
    private Button register_btn;
    private Button login_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        register_btn = findViewById(R.id.main_register_btn);
        login_btn = findViewById(R.id.main_login_btn);

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callRegisterActivity();
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callLoginActivity();
            }
        });

    }

    private void callRegisterActivity(){

        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    private void callLoginActivity(){
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }



}
