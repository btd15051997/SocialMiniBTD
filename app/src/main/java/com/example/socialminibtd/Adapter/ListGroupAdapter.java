package com.example.socialminibtd.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.socialminibtd.Model.ModelListGroup;
import com.example.socialminibtd.R;
import com.example.socialminibtd.Utils.Const;
import com.example.socialminibtd.Utils.Controller;
import com.example.socialminibtd.View.Activity.ChatGroup.GroupChatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ListGroupAdapter extends RecyclerView.Adapter<ListGroupAdapter.ViewHodelGroup> {

    private ArrayList<ModelListGroup> arrayList;
    private Context context;

    public ListGroupAdapter(ArrayList<ModelListGroup> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHodelGroup onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_group_chats, parent, false);

        return new ViewHodelGroup(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHodelGroup holder, int position) {

        ModelListGroup modelListGroup = arrayList.get(position);

        final String groupId = modelListGroup.getGroupId();
        String groupIcon = modelListGroup.getGroupIcon();
        String groupTitle = modelListGroup.getGroupTitle();
        final String groupTimeCreate = modelListGroup.getTimeStamp();
        String groupDescription = modelListGroup.getGroupDescription();

        //load last message and massage time

        loadLastMessage(modelListGroup, holder);

        holder.txt_titlegroup_groupchat.setText(groupTitle);

        try {

            Picasso.get().load(groupIcon).placeholder(R.drawable.icon_logoapp).into(holder.img_avt_groupchat);

        } catch (Exception e) {

            holder.img_avt_groupchat.setImageResource(R.drawable.icon_logoapp);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent_groupchat = new Intent(context, GroupChatActivity.class);
                intent_groupchat.putExtra("groupID", groupId);
                intent_groupchat.putExtra("groupTime", groupTimeCreate);
                context.startActivity(intent_groupchat);

            }
        });

    }

    private void loadLastMessage(ModelListGroup modelListGroup, final ViewHodelGroup holder) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(modelListGroup.getGroupId()).child("Messages").limitToLast(1) // get last time from that child
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            String message = "" + ds.child("message").getValue();
                            String timestamp = "" + ds.child("timestamp").getValue();
                            String typeMessage = "" + ds.child("type").getValue();

                            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                            cal.setTimeInMillis(Long.parseLong(timestamp));
                            final String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();

                            final String sender = "" + ds.child("sender").getValue();

                            holder.txt_timesend_groupchat.setText(dateTime);

                            if (typeMessage.equals("image")){

                                holder.txt_textmessage_groupchat.setText("Sent Photo");

                            }else {

                                holder.txt_textmessage_groupchat.setText(message);
                            }


                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
                            reference.orderByChild("uid").equalTo(sender)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                                String name = "" + ds.child("name").getValue();

                                                if (sender.equals(ds.child("uid").getValue())) {

                                                    holder.txt_sendername_groupchat.setText(name+" :");

                                                }

                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                            Controller.appLogDebug(Const.LOG_DAT, "loadLastMessage :" + databaseError.getMessage());

                                        }
                                    });

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        Controller.appLogDebug(Const.LOG_DAT, databaseError.getMessage());

                    }
                });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHodelGroup extends RecyclerView.ViewHolder {

        private CircularImageView img_avt_groupchat;
        private TextView txt_titlegroup_groupchat, txt_sendername_groupchat, txt_textmessage_groupchat, txt_timesend_groupchat;

        public ViewHodelGroup(@NonNull View itemView) {
            super(itemView);

            img_avt_groupchat = itemView.findViewById(R.id.img_avt_groupchat);
            txt_titlegroup_groupchat = itemView.findViewById(R.id.txt_titlegroup_groupchat);
            txt_sendername_groupchat = itemView.findViewById(R.id.txt_sendername_groupchat);
            txt_textmessage_groupchat = itemView.findViewById(R.id.txt_textmessage_groupchat);
            txt_timesend_groupchat = itemView.findViewById(R.id.txt_timesend_groupchat);


        }
    }
}
