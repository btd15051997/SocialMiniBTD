package com.example.socialminibtd.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.socialminibtd.Model.ListUser;
import com.example.socialminibtd.R;
import com.example.socialminibtd.View.Activity.ChatUser.ChatActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class ListChatAdapter extends RecyclerView.Adapter<ListChatAdapter.ViewHolder> {


    private ArrayList<ListUser> arrayList;
    private Context context;
    private HashMap<String, String> lastMessageMap;

    public ListChatAdapter(ArrayList<ListUser> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
        this.lastMessageMap = new HashMap<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_chatlist, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final String hisUid = arrayList.get(position).getUid();
        String userImage = arrayList.get(position).getImage();
        String userName = arrayList.get(position).getName();
        String lastMessage = lastMessageMap.get(hisUid);

        holder.txt_hisname_listchat.setText(userName);

        if (lastMessage == null || lastMessage.equals("default")) {

            holder.txt_lastmessage_listchat.setVisibility(View.GONE);

        } else {

            holder.txt_lastmessage_listchat.setVisibility(View.VISIBLE);
            holder.txt_lastmessage_listchat.setText(lastMessage);

        }

        try {

            Picasso.get().load(userImage).placeholder(R.drawable.ic_account).into(holder.img_account_listchat);

        } catch (Exception e) {

            Picasso.get().load(R.drawable.ic_account).into(holder.img_account_listchat);

        }

        if (arrayList.get(position).getOnlineStatus().equals("Online")) {

           holder.img_onlinestatus_listchat.setImageResource(R.drawable.circle_online);

        } else {

            holder.img_onlinestatus_listchat.setImageResource(R.drawable.circle_offline);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("hisUid", hisUid);
                context.startActivity(intent);

            }
        });

    }

    public void setLastMessageMap(String userId, String lastMessage1) {

        lastMessageMap.put(userId, String.valueOf(lastMessage1));

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircularImageView img_account_listchat;
        private ImageView img_onlinestatus_listchat;
        private TextView txt_hisname_listchat, txt_lastmessage_listchat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img_account_listchat = itemView.findViewById(R.id.img_account_listchat);
            img_onlinestatus_listchat = itemView.findViewById(R.id.img_onlinestatus_listchat);
            txt_hisname_listchat = itemView.findViewById(R.id.txt_hisname_listchat);
            txt_lastmessage_listchat = itemView.findViewById(R.id.txt_lastmessage_listchat);
        }
    }
}
