package com.example.socialmediaapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.model.model_user;
import com.squareup.picasso.Picasso;

import java.util.List;

public class adapter_user extends RecyclerView.Adapter<adapter_user.myHolder> {

    Context context;
    List<model_user> userList;

    public adapter_user(Context context, List<model_user> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public myHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // inflate layout row_users.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_users, parent, false);

        return new myHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myHolder holder, int position) {

        // get data
        String userProfilePhoto = userList.get(position).getProfilePhoto();
        final String userName = userList.get(position).getName();
        final String userEmail = userList.get(position).getEmail();

        // set data
        holder.nameTv.setText(userName);
        holder.emailTv.setText(userEmail);
        try{
            Picasso.get().load(userProfilePhoto)
                    .placeholder(R.drawable.ic_default_image)
                    .into(holder.profilePhoto);
        }
        catch (Exception e){
            Log.d("adapter_user", "onBindViewHolder: failed to load image..." + e.getMessage());
        }

        //handle item click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(context, "" + userName, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    // view holder class
    class myHolder extends RecyclerView.ViewHolder{

        ImageView profilePhoto;
        TextView nameTv, emailTv;

        public myHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            profilePhoto = itemView.findViewById(R.id.row_users_profilePhoto);
            nameTv = itemView.findViewById(R.id.row_users_usernameTv);
            emailTv = itemView.findViewById(R.id.row_users_userEmailTv);
        }
    }

}





