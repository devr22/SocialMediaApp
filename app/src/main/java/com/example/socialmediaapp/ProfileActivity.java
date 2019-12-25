package com.example.socialmediaapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    //widgets
    BottomNavigationView bottomNavigationView;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //ActionBar and its title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");

        init();

        bottomNavigationView.setOnNavigationItemSelectedListener(selectedListener);

    }

    private void init(){

        firebaseAuth = FirebaseAuth.getInstance();

        bottomNavigationView = findViewById(R.id.bottomNavigation);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()){

                        case R.id.nav_home:
                            //home fragment transaction
                            return true;

                        case R.id.nav_profile:
                            //profile fragment transaction
                            return true;

                        case R.id.nav_users:
                            // users fragment transaction
                            return true;
                    }

                    return false;
                }
            };

    private void checkUserStatus(){

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null){
            // user is already logged in , stay here

        }
        else {
            // go to main activity
            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            finish();
        }

    }

    @Override
    protected void onStart() {

        checkUserStatus();
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //inflating menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_logout){

            // logout from here
            firebaseAuth.signOut();
            checkUserStatus();
        }

        return super.onOptionsItemSelected(item);
    }
}
