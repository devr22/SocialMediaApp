package com.example.socialmediaapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.model.ModelChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder> {

    private static final int MESSAGE_TYPE_RECEIVER = 0;
    private static final int MESSAGE_TYPE_SENDER = 1;

    Context context;
    List<ModelChat> chatList;
    String imageUrl;

    FirebaseUser firebaseUser;

    public AdapterChat(Context context, List<ModelChat> chatList, String imageUrl) {
        this.context = context;
        this.chatList = chatList;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == MESSAGE_TYPE_RECEIVER){
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_receiver, parent, false);
            return new MyHolder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_sender, parent, false);
            return new MyHolder(view);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        // get data
        String messsage = chatList.get(position).getMessage();
        String timeStamp = chatList.get(position).getTimeStamp();

        // convert timestamp to dd/mm/yyyy 00:00AM/PM
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(timeStamp));
        String dateTime = DateFormat.format("dd/mm/yyyy hh:mm aa", calendar).toString();

        //set data
        holder.messageTV.setText(messsage);
        holder.timeTV.setText(dateTime);
        try {
            Picasso.get().load(imageUrl).into(holder.profileIV);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        // set seen/delivered ststus
        if (position == chatList.size() - 1){
            if (chatList.get(position).isSeen()){
                holder.isSeenTV.setText("Seen");
            }
            else {
                holder.isSeenTV.setText("Delivered");
            }
        }
        else {
            holder.isSeenTV.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder{

        // views
        ImageView profileIV;
        TextView messageTV, timeTV, isSeenTV;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            profileIV = itemView.findViewById(R.id.profileIV);
            messageTV = itemView.findViewById(R.id.messageTV);
            timeTV = itemView.findViewById(R.id.timeTV);
            isSeenTV = itemView.findViewById(R.id.isSeenTV);
        }


    }

    @Override
    public int getItemViewType(int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (chatList.get(position).getSender().equals(firebaseUser)){
            return MESSAGE_TYPE_SENDER;
        }
        else {
            return MESSAGE_TYPE_RECEIVER;
        }
    }
}




