package com.example.socialminibtd.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.socialminibtd.Model.ListUser;
import com.example.socialminibtd.R;
import com.example.socialminibtd.Utils.Const;
import com.example.socialminibtd.Utils.Controller;
import com.example.socialminibtd.Utils.RecyclerLongPressClickListener;
import com.example.socialminibtd.View.Activity.ChatUser.ChatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.sql.Ref;
import java.util.ArrayList;
import java.util.HashMap;

public class ListUserAdapter extends RecyclerView.Adapter<ListUserAdapter.ViewHolder> {

    private ArrayList<ListUser> arrayList;
    private Context mContext;

    private FirebaseAuth firebaseAuth;
    private String myUid;


    public ListUserAdapter(ArrayList<ListUser> arrayList, Context mContext) {
        this.arrayList = arrayList;
        this.mContext = mContext;

        firebaseAuth = FirebaseAuth.getInstance();
        myUid = firebaseAuth.getUid();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_list_user, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.txt_name_listuser.setText(arrayList.get(position).getName());
        holder.txt_email_listuser.setText(arrayList.get(position).getEmail());
        holder.txt_phone_listuser.setText(arrayList.get(position).getPhone());

        final String hisUID = arrayList.get(position).getUid();

        try {

            Picasso.get().load(arrayList.get(position).getImage())
                    .placeholder(R.drawable.ic_account)
                    .into(holder.img_avt_listuser);

        } catch (Exception e) {

            Picasso.get().load(R.drawable.ic_account)
                    .into(holder.img_avt_listuser);
        }


       // holder.img_block_listuser.setImageResource(R.drawable.ic_unblock);
        //check if each user it is bloked or not
        checkIsBlock(hisUID, holder, position);

        holder.relative_item_user.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_in_up));

        holder.img_setting_listuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {

                    PopupMenu popupMenu = new PopupMenu(mContext, holder.img_setting_listuser, Gravity.END);

                    popupMenu.getMenu().add(Menu.NONE, 0, 0, mContext.getResources().getString(R.string.txt_send_message));

                    popupMenu.show();

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()) {

                                case 0:

                                    if (arrayList != null) {

                                        imBlockORNot(hisUID);


                                    }

                                    break;


                            }

                            return false;
                        }
                    });

                }

            }
        });


        holder.img_block_listuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (arrayList.get(position).isBlocked()) {

                    unBlockUser(hisUID, arrayList.get(position).getName());
                    holder.img_block_listuser.setImageResource(R.drawable.ic_unblock);

                } else {

                    BlockUser(hisUID, arrayList.get(position).getName());
                    holder.img_block_listuser.setImageResource(R.drawable.ic_block);
                }

            }
        });

    }

    private void imBlockORNot(final String hisUID) {

        //if other blocked you, you can't send message to that user
        DatabaseReference Ref = FirebaseDatabase.getInstance().getReference("User");
        Ref.child(hisUID).child("BlockedUSers").orderByChild("uid").equalTo(myUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            if (ds.exists()) {

                                Controller.showLongToast("You're blocked by that user, can't send message", mContext);

                                return;
                            }

                        }
                        // intent chat, if unblocked
                        Intent intent = new Intent(mContext, ChatActivity.class);
                        intent.putExtra("hisUid", hisUID);
                        mContext.startActivity(intent);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        Controller.appLogDebug(Const.LOG_DAT, " Failed  BlockUser " + databaseError.toString());

                    }
                });

    }

    private void checkIsBlock(String hisUID, final ViewHolder holder, final int position) {

        DatabaseReference Ref = FirebaseDatabase.getInstance().getReference("User");
        Ref.child(myUid).child("BlockedUSers").orderByChild("uid").equalTo(hisUID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            if (ds.exists()) {

                                holder.img_block_listuser.setImageResource(R.drawable.ic_block);
                                arrayList.get(position).setBlocked(true);

                                return;
                            }

                            holder.img_block_listuser.setImageResource(R.drawable.ic_unblock);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        Controller.appLogDebug(Const.LOG_DAT, " Failed  BlockUser " + databaseError.toString());

                    }
                });


    }

    private void unBlockUser(String hisUid, final String nameUser) {
        // un block user, by remove uid to current user's node "BlockedUers"

        DatabaseReference Ref = FirebaseDatabase.getInstance().getReference("User");
        Ref.child(myUid).child("BlockedUSers").orderByChild("uid").equalTo(hisUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            if (ds.exists()) {

                                ds.getRef()
                                        .removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                Controller.showLongToast("UnBlocked " + nameUser, mContext);

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Controller.showLongToast(e.toString(), mContext);

                                        Controller.appLogDebug(Const.LOG_DAT, " Failed UnBlock " + e.toString());

                                    }
                                });

                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        Controller.appLogDebug(Const.LOG_DAT, " Failed  BlockUser " + databaseError.toString());

                    }
                });

    }

    private void BlockUser(String hisUid, final String nameUser) {
        //block user, by adding uid to current user's node "BlockedUSers"

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", hisUid);

        DatabaseReference Ref = FirebaseDatabase.getInstance().getReference("User");
        Ref.child(myUid).child("BlockedUSers").child(hisUid).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Controller.showLongToast("Blocked " + nameUser, mContext);


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Controller.appLogDebug(Const.LOG_DAT, " BlockUser " + e.toString());

            }
        });
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircularImageView img_avt_listuser;
        TextView txt_name_listuser, txt_email_listuser, txt_phone_listuser;
        RelativeLayout relative_item_user;
        ImageView img_block_listuser, img_setting_listuser;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img_avt_listuser = itemView.findViewById(R.id.img_avt_listuser);
            txt_name_listuser = itemView.findViewById(R.id.txt_name_listuser);
            txt_email_listuser = itemView.findViewById(R.id.txt_email_listuser);
            txt_phone_listuser = itemView.findViewById(R.id.txt_phone_listuser);
            relative_item_user = itemView.findViewById(R.id.relative_item_user);
            img_block_listuser = itemView.findViewById(R.id.img_block_listuser);
            img_setting_listuser = itemView.findViewById(R.id.img_setting_listuser);

        }
    }
}
