package com.example.socialminibtd.Adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialminibtd.Model.ModelChatGroup;
import com.example.socialminibtd.R;
import com.example.socialminibtd.Utils.Const;
import com.example.socialminibtd.Utils.Controller;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ChatGroupsAdapter extends RecyclerView.Adapter<ChatGroupsAdapter.ViewHodel> {


    private FirebaseUser mUser;
    private FirebaseAuth auth;
    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    private Context mContext;
    private ArrayList<ModelChatGroup> arrayList;

    public ChatGroupsAdapter(Context mContext, ArrayList<ModelChatGroup> arrayList) {

        this.mContext = mContext;
        this.arrayList = arrayList;
        auth = FirebaseAuth.getInstance();
        mUser = auth.getCurrentUser();

    }

    @NonNull
    @Override
    public ViewHodel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == MSG_TYPE_RIGHT) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.row_chatgroup_right, parent, false);

            return new ViewHodel(view);

        } else {

            View view = LayoutInflater.from(mContext).inflate(R.layout.row_chatgroup_left, parent, false);

            return new ViewHodel(view);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHodel holder, int position) {

        ModelChatGroup modelChatGroup = arrayList.get(position);

        String message = modelChatGroup.getMessage();
        String time = modelChatGroup.getTimestamp();
        String typeMessage = modelChatGroup.getType();

        if (typeMessage.equals("text")) {

            holder.img_message_chatgroup.setVisibility(View.GONE);
            holder.txt_textmessage_chatgroup.setVisibility(View.VISIBLE);
            holder.txt_textmessage_chatgroup.setText(message);

        } else {

            holder.img_message_chatgroup.setVisibility(View.VISIBLE);
            holder.txt_textmessage_chatgroup.setVisibility(View.GONE);

            try {

                Picasso.get().load(message).placeholder(R.drawable.ic_image).into(holder.img_message_chatgroup);

            } catch (Exception e) {

                holder.img_message_chatgroup.setImageResource(R.drawable.ic_image);

            }


        }


        holder.txt_timemessage_chatgroup.setText(Controller.convertDateTime(time));

        setUserName(modelChatGroup, holder);

    }

    private void setUserName(final ModelChatGroup modelChatGroup, final ViewHodel holder) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
        databaseReference.orderByChild("uid").equalTo(modelChatGroup.getSender())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            if (modelChatGroup.getSender().equals(ds.child("uid").getValue())) {

                                String nameUser = String.valueOf(ds.child("name").getValue());

                                holder.txt_name_chatgroup.setText(nameUser);

                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        Controller.appLogDebug(Const.LOG_DAT, databaseError.getMessage());

                    }
                });

    }

    @Override
    public int getItemViewType(int position) {

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        if (arrayList.get(position).getSender().equals(mUser.getUid())) {

            return MSG_TYPE_RIGHT;

        } else {

            return MSG_TYPE_LEFT;

        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHodel extends RecyclerView.ViewHolder {

        private TextView txt_name_chatgroup, txt_textmessage_chatgroup, txt_timemessage_chatgroup;
        private ImageView img_message_chatgroup;

        public ViewHodel(@NonNull View itemView) {
            super(itemView);

            txt_name_chatgroup = itemView.findViewById(R.id.txt_name_chatgroup);
            txt_textmessage_chatgroup = itemView.findViewById(R.id.txt_textmessage_chatgroup);
            txt_timemessage_chatgroup = itemView.findViewById(R.id.txt_timemessage_chatgroup);
            img_message_chatgroup = itemView.findViewById(R.id.img_message_chatgroup);
        }
    }

}
