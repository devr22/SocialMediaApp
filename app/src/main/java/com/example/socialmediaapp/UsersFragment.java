package com.example.socialmediaapp;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.socialmediaapp.adapter.adapter_user;
import com.example.socialmediaapp.model.model_user;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;


/**
 * A simple {@link Fragment} subclass.
 */
public class UsersFragment extends Fragment {

    private RecyclerView recyclerView;

    adapter_user madapter_user;
    List<model_user> userList;
    private View view;

    FirebaseAuth firebaseAuth;

    public UsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_users, container, false);

        recyclerView = view.findViewById(R.id.users_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // init userlist
        userList = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();

        getAllUsers();

        return view;
    }

    private void getAllUsers(){

        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // get all data from Users path
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                userList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    model_user modelUser = ds.getValue(model_user.class);

                    //get all users except currently signed in user
                    if (!modelUser.getUid().equals(currentUser.getUid()))
                    {
                        userList.add(modelUser);
                    }

                    // adapter
                    madapter_user  = new adapter_user(getActivity(), userList);

                    // set adapter to recycler view
                    recyclerView.setAdapter(madapter_user);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void searchUser(final String query){

        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // get all data from Users path
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                userList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    model_user modelUser = ds.getValue(model_user.class);

                    //get all searched users except currently signed in user
                    if (!modelUser.getUid().equals(currentUser.getUid()))
                    {
                        if (modelUser.getName().toLowerCase().contains(query.toLowerCase())
                            || modelUser.getEmail().toLowerCase().contains(query.toLowerCase()))
                        {
                            userList.add(modelUser);
                        }
                    }

                    // adapter
                    madapter_user  = new adapter_user(getActivity(), userList);
                    //refresh adapter
                    madapter_user.notifyDataSetChanged();
                    // set adapter to recycler view
                    recyclerView.setAdapter(madapter_user);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    // inflate option menu
    @Override
    public void onCreateOptionsMenu(Menu menu, final MenuInflater inflater) {

        inflater.inflate(R.menu.menu_main, menu);

        // search view
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        // search listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                // called when user press search button from keyboard
                if (!TextUtils.isEmpty(s.trim())){
                    searchUser(s);
                }
                else {
                    getAllUsers();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // called when user press any single key from keyboard
                if (!TextUtils.isEmpty(s.trim())){
                    searchUser(s);
                }
                else {
                    getAllUsers();
                }

                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    // handle menu i tem click
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

    private void checkUserStatus(){

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null){
            // user is already logged in , stay here

        }
        else {
            // go to main activity
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }

    }

}








