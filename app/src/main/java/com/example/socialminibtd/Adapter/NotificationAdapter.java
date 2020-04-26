package com.example.socialminibtd.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.socialminibtd.Model.ListNotification;
import com.example.socialminibtd.R;
import com.example.socialminibtd.Utils.Const;
import com.example.socialminibtd.Utils.Controller;
import com.example.socialminibtd.View.Activity.PostDetail.PostDetailActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHodelNoti> {


    private ArrayList<ListNotification> arrayList;
    private Context context;

    private FirebaseAuth auth;

    public NotificationAdapter(ArrayList<ListNotification> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
        auth = FirebaseAuth.getInstance();

    }

    @NonNull
    @Override
    public ViewHodelNoti onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_list_notification, parent, false);

        return new ViewHodelNoti(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHodelNoti holder, int position) {

        final ListNotification listNotification = arrayList.get(position);

        String name = listNotification.getsName();
        String notification = listNotification.getNotification();
        String image = listNotification.getsImage();
        final String timestamp = listNotification.getTimestamp();
        String senderUid = listNotification.getsUid();
        final String pIDTime = listNotification.getpId();

        //we will get name, email, image of the user notification from his uid
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
        databaseReference
                .orderByChild("uid")
                .equalTo(senderUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            String name = "" + ds.child("name").getValue();
                            String image = "" + ds.child("image").getValue();
                            String email = "" + ds.child("email").getValue();

                            listNotification.setsName(name);
                            listNotification.setsEmail(email);
                            listNotification.setsImage(image);

                            holder.txt_name_item_notification.setText(name);

                            try {

                                Picasso.get().load(image).placeholder(R.drawable.ic_notifications).into(holder.img_item_notification);

                            } catch (Exception e) {

                                Picasso.get().load(R.drawable.ic_notifications).into(holder.img_item_notification);


                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        Controller.appLogDebug(Const.LOG_DAT, "get name, email, image :" + databaseError.getMessage());

                    }
                });


        //set view
        holder.txt_content_item_notification.setText(notification);
        holder.txt_time_item_notification.setText(Controller.convertDateTime(timestamp));

        //open post detail when click

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //start postdetail
                Intent intentPostDetail = new Intent(context, PostDetailActivity.class);
                intentPostDetail.putExtra("postId", pIDTime);
                context.startActivity(intentPostDetail);

            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                onDialogDeleteNotification(timestamp);

                return false;
            }
        });


    }

    private void onDialogDeleteNotification(final String timestamp2) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Are you want delete this notification ?");

        builder.setNegativeButton(""+context.getResources().getString(R.string.txt_yes)
                , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
                databaseReference.child(auth.getUid())
                        .child("Notifications")
                        .child(timestamp2)
                        .removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Controller.showLongToast("Notification Deleted...", context);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Controller.appLogDebug(Const.LOG_DAT, "DeleteNotification :" + e.toString());

                        Controller.showLongToast(e.toString(), context);

                    }
                });

            }
        });

        builder.setPositiveButton(""+context.getResources().getString(R.string.txt_no)
                , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }
        });

        builder.create().show();

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHodelNoti extends RecyclerView.ViewHolder {

        private CircularImageView img_item_notification;
        private TextView txt_name_item_notification, txt_content_item_notification, txt_time_item_notification;

        public ViewHodelNoti(@NonNull View itemView) {
            super(itemView);

            img_item_notification = itemView.findViewById(R.id.img_item_notification);
            txt_name_item_notification = itemView.findViewById(R.id.txt_name_item_notification);
            txt_content_item_notification = itemView.findViewById(R.id.txt_content_item_notification);
            txt_time_item_notification = itemView.findViewById(R.id.txt_time_item_notification);

        }
    }
}
