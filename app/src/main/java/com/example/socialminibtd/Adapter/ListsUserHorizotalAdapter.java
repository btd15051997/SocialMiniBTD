package com.example.socialminibtd.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.socialminibtd.Model.ListUser;
import com.example.socialminibtd.R;
import com.example.socialminibtd.Utils.Const;
import com.example.socialminibtd.Utils.Controller;
import com.example.socialminibtd.View.Activity.ChatUser.ChatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import java.util.ArrayList;

public class ListsUserHorizotalAdapter extends RecyclerView.Adapter<ListsUserHorizotalAdapter.ViewHodel> {

    private Context context;
    private ArrayList<ListUser> horizotalArrayList;
    private FirebaseAuth firebaseAuth;

    public ListsUserHorizotalAdapter(Context context, ArrayList<ListUser> horizotalArrayList) {
        this.context = context;
        this.horizotalArrayList = horizotalArrayList;
        firebaseAuth = FirebaseAuth.getInstance();

    }

    @NonNull
    @Override
    public ViewHodel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_user_horizontal, parent, false);

        return new ViewHodel(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHodel holder, int position) {

        ListUser userhorizotal = horizotalArrayList.get(position);

        String uDp = userhorizotal.getImage();

        final String hisUID = horizotalArrayList.get(position).getUid();

        holder.txt_user_name_horizontal.setText(userhorizotal.getName());

        holder.itemView.setOnClickListener(v ->

                imBlockORNot(hisUID)

        );

        try {

            Picasso.get().load(uDp).placeholder(R.drawable.icon_logoapp).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                    holder.img_user_horizontal.setImageBitmap(bitmap);

                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
            Controller.appLogDebug(Const.LOG_DAT, uDp);

        } catch (Exception e) {

            holder.img_user_horizontal.setImageResource(R.drawable.icon_logoapp);

        }

    }

    private void imBlockORNot(final String hisUID) {

        //if other blocked you, you can't send message to that user
        DatabaseReference Ref = FirebaseDatabase.getInstance().getReference("User");
        Ref.child(hisUID).child("BlockedUSers").orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            if (ds.exists()) {

                                Controller.showLongToast("You're blocked by that user, can't send message", context);

                                return;
                            }

                        }
                        // intent chat, if unblocked
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra("hisUid", hisUID);
                        context.startActivity(intent);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        Controller.appLogDebug(Const.LOG_DAT, " Failed  BlockUser " + databaseError.toString());

                    }
                });

    }

    @Override
    public int getItemCount() {
        return horizotalArrayList.size();
    }

    public class ViewHodel extends RecyclerView.ViewHolder {

        private CircularImageView img_user_horizontal;
        private TextView txt_user_name_horizontal;

        public ViewHodel(@NonNull View itemView) {
            super(itemView);

            img_user_horizontal = itemView.findViewById(R.id.img_user_horizontal);
            txt_user_name_horizontal = itemView.findViewById(R.id.txt_user_name_horizontal);
        }
    }

}
