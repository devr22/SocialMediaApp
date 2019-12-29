package com.example.socialmediaapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    DatabaseReference user_databaseReference;

    // views
    Toolbar toolbar;
    ImageView profilePhotoIv;
    TextView receiverName, receiverStatus;
    RecyclerView recyclerView;
    EditText chatMessage;
    ImageButton sendButton;

    String receiverUID, myUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");

        recyclerView = findViewById(R.id.chat_recyclerView);
        profilePhotoIv = findViewById(R.id.chat_profilePhoto);
        receiverName = findViewById(R.id.chat_receiverName);
        receiverStatus = findViewById(R.id.chat_receiverStatus);
        chatMessage = findViewById(R.id.chat_message);
        sendButton = findViewById(R.id.chat_sendButton);

        Intent intent = getIntent();
        receiverUID = intent.getStringExtra("receiverUid");

        firebaseAuth = FirebaseAuth.getInstance();
        user_databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        searchUser();    // receiver

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = chatMessage.getText().toString().trim();

                if (TextUtils.isEmpty(message)){
                    Toast.makeText(ChatActivity.this, "can't send empty message...", Toast.LENGTH_SHORT).show();
                }
                else {
                    sendMessage(message);
                }
            }
        });
    }

    private void searchUser(){

        Query userQuery = user_databaseReference.orderByChild("uid").equalTo(receiverUID);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    String name = "" + ds.child("name").getValue();
                    String profilePhoto = "" + ds.child("profilePhoto").getValue();

                    receiverName.setText(name);
                    try {
                        Picasso.get().load(profilePhoto).placeholder(R.drawable.ic_default_profile_white).into(profilePhotoIv);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.ic_default_profile_white).into(profilePhotoIv);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendMessage(String message){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", myUID);
        hashMap.put("receiver", receiverUID);
        hashMap.put("message", message);

        databaseReference.child("Chats").push().setValue(hashMap);

        chatMessage.setText("");
    }

    private void checkUserStatus(){

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null){
            // user is already logged in , stay here
            myUID = user.getUid();
        }
        else {
            // go to main activity
            startActivity(new Intent(ChatActivity.this, MainActivity.class));
            finish();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // hide searchView
        menu.findItem(R.id.action_search).setVisible(false);

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

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }
}



