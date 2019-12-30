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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.adapter.AdapterChat;
import com.example.socialmediaapp.model.ModelChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    DatabaseReference user_databaseReference;

    //for checking if user has seen message or not
    ValueEventListener seenListener;
    DatabaseReference userRefForSeen;

    List<ModelChat> chatList;
    AdapterChat adapterChat;


    // views
    Toolbar toolbar;
    ImageView profilePhotoIv;
    TextView receiverName, receiverStatus;
    RecyclerView recyclerView;
    EditText chatMessage;
    ImageButton sendButton;

    String receiverUID, myUID;
    String recieverImage;

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

        // layout for recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

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

        readMessage();
        seenMessage();

    }

    private void seenMessage(){

        userRefForSeen = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = userRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    ModelChat chat = ds.getValue(ModelChat.class);

                    if (chat.getReceiver().equals(myUID) && chat.getSender().equals(receiverUID)){

                        HashMap<String ,Object> hasSeenHashMap = new HashMap<>();
                        hasSeenHashMap.put("isSeen", true);
                        ds.getRef().updateChildren(hasSeenHashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void readMessage(){

        chatList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    ModelChat chat = ds.getValue(ModelChat.class);
                    if (chat.getReceiver().equals(myUID) && chat.getSender().equals(receiverUID)
                        || (chat.getReceiver().equals(receiverUID) && chat.getSender().equals(myUID))){

                        chatList.add(chat);
                    }

                    adapterChat = new AdapterChat(ChatActivity.this, chatList, recieverImage);
                    adapterChat.notifyDataSetChanged();
                    recyclerView.setAdapter(adapterChat);   // set adapter to recycler


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendMessage(String message){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        String timestamp = String.valueOf(System.currentTimeMillis());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", myUID);
        hashMap.put("receiver", receiverUID);
        hashMap.put("message", message);
        hashMap.put("timestamp", timestamp);
        hashMap.put("isSeen", false);

        databaseReference.child("Chats").push().setValue(hashMap);

        chatMessage.setText("");
    }

    private void searchUser(){

        Query userQuery = user_databaseReference.orderByChild("uid").equalTo(receiverUID);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    String name = "" + ds.child("name").getValue();
                    recieverImage = "" + ds.child("profilePhoto").getValue();

                    receiverName.setText(name);
                    try {
                        Picasso.get().load(recieverImage).placeholder(R.drawable.ic_default_profile_white).into(profilePhotoIv);
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

    @Override
    protected void onPause() {
        super.onPause();
        userRefForSeen.removeEventListener(seenListener);
    }
}



