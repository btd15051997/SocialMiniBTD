package com.example.socialminibtd.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.socialminibtd.Model.ModelChat;
import com.example.socialminibtd.R;
import com.example.socialminibtd.Utils.Const;
import com.example.socialminibtd.Utils.Controller;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyHolder> {


    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    private Context mContext;
    private ArrayList<ModelChat> mArrayList;
    private String imageUrl;
    private FirebaseUser mUser;

    public ChatAdapter(Context mContext, ArrayList<ModelChat> mArrayList, String imageUrl) {
        this.mContext = mContext;
        this.mArrayList = mArrayList;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate  layout: row_left or right

        if (viewType == MSG_TYPE_RIGHT) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.row_chat_right, parent, false);

            return new MyHolder(view);

        } else {

            View view = LayoutInflater.from(mContext).inflate(R.layout.row_chat_left, parent, false);

            return new MyHolder(view);

        }

    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {

        String timeStamp = mArrayList.get(position).getTimestamp();
        String type = mArrayList.get(position).getType();
        String message = mArrayList.get(position).getMessage();

        Controller.appLogDebug(Const.LOG_DAT, "Send MessageChat Date :" + mArrayList.get(position).getTimestamp());

        holder.txt_chat_date_message_left.setText(timeStamp);


        if (type.equals("text")) {

            holder.txt_text_message_left.setVisibility(View.VISIBLE);
            holder.txt_text_message_left.setText(mArrayList.get(position).getMessage());
            holder.img_message_chat.setVisibility(View.GONE);

        } else {

            holder.txt_text_message_left.setVisibility(View.GONE);
            holder.img_message_chat.setVisibility(View.VISIBLE);

            try {

                Picasso.get().load(message).placeholder(R.drawable.ic_image).into(holder.img_message_chat);

            } catch (Exception e) {

                Picasso.get().load(R.drawable.ic_image).into(holder.img_message_chat);

            }

        }

        try {

            Picasso.get().load(imageUrl).into(holder.img_row_left);

        } catch (Exception e) {

            Picasso.get().load(R.drawable.ic_account).into(holder.img_row_left);

        }

        //set seen/delivered status of message
        //  if (position == mArrayList.size() -1){
        Controller.appLogDebug(Const.LOG_DAT, "Send MessageChat isSeen :" + mArrayList.get(position).isIsseen());

        if (mArrayList.get(position).isIsseen() == true) {

            holder.txt_dilever_left.setText(mContext.getResources().getString(R.string.txt_seen));

        } else {

            holder.txt_dilever_left.setText(mContext.getResources().getString(R.string.txt_delivered));
        }


        // delete text chat
        holder.linear_list_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder dialogDelete = new AlertDialog.Builder(mContext);

                dialogDelete.setTitle(mContext.getResources().getString(R.string.txt_delete));
                dialogDelete.setMessage(mContext.getResources().getString(R.string.txt_text_delete));

                dialogDelete.setPositiveButton(mContext.getResources().getString(R.string.txt_delete)
                        , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                deleteMessage(position);

                            }
                        });

                dialogDelete.setNegativeButton(mContext.getResources().getString(R.string.txt_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                });

                dialogDelete.create().show();

            }
        });

    }

    private void deleteMessage(int position) {

        final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        final String timestamp = mArrayList.get(position).getTimestamp();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        Query query = databaseReference.orderByChild("timestamp").equalTo(timestamp);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    if (ds.child("sender").getValue().equals(myUid)
                            && ds.child("timestamp").getValue().equals(timestamp)) {

                        // update with myUid current

                        //   ds.getRef().removeValue();

                        HashMap<String, Object> hashMap = new HashMap<>();

                        hashMap.put("message", mContext.getResources().getString(R.string.txt_was_delete));

                        ds.getRef().updateChildren(hashMap);

                        Controller.showLongToast(mContext.getResources().getString(R.string.txt_was_delete), mContext);

                        Controller.appLogDebug(Const.LOG_DAT, " deleteMessage :" + hashMap);

                    } else {


                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mArrayList.get(position).getSender().equals(mUser.getUid())) {

            return MSG_TYPE_RIGHT;

        } else {

            return MSG_TYPE_LEFT;

        }

    }

    public class MyHolder extends RecyclerView.ViewHolder {

        private CircularImageView img_row_left;
        private TextView txt_text_message_left, txt_chat_date_message_left, txt_dilever_left;
        private LinearLayout linear_list_chat;
        private ImageView img_message_chat;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            img_row_left = itemView.findViewById(R.id.img_row_left);
            txt_chat_date_message_left = itemView.findViewById(R.id.txt_chat_date_message_left);
            txt_text_message_left = itemView.findViewById(R.id.txt_text_message_left);
            txt_dilever_left = itemView.findViewById(R.id.txt_dilever_left);
            linear_list_chat = itemView.findViewById(R.id.linear_list_chat);
            img_message_chat = itemView.findViewById(R.id.img_message_chat);

        }
    }
}
