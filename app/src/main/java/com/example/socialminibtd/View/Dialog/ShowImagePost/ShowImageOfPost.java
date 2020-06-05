package com.example.socialminibtd.View.Dialog.ShowImagePost;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.socialminibtd.R;
import com.example.socialminibtd.Utils.Const;
import com.example.socialminibtd.Utils.Controller;
import com.example.socialminibtd.View.Activity.Dashbroad.DashboardActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class ShowImageOfPost extends DialogFragment {

    private DashboardActivity dashboardActivity;
    private String postId;
    /*View*/

    private TextView txt_sumlike_showimage, txt_sumcomment_showimage, txt_date_showimage;
    private ImageView img_showimage_post;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dashboardActivity = (DashboardActivity) getActivity();

        if (getArguments() != null) {

            postId = getArguments().getString("postID");

            Controller.appLogDebug("" + Const.LOG_DAT, "" + postId);

        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Dialog dialog_setting = new Dialog(dashboardActivity, R.style.DialogThemeforview);
        dialog_setting.setContentView(R.layout.dialog_show_image_ofpost);
        dialog_setting.setCancelable(true);

        txt_sumlike_showimage = dialog_setting.findViewById(R.id.txt_sumlike_showimage);
        txt_sumcomment_showimage = dialog_setting.findViewById(R.id.txt_sumcomment_showimage);
        img_showimage_post = dialog_setting.findViewById(R.id.img_showimage_post);
        txt_date_showimage = dialog_setting.findViewById(R.id.txt_date_showimage);

        onGetInfoPost();

        return dialog_setting;
    }

    private void onGetInfoPost() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");

        Query query = databaseReference.orderByChild("pIDTime").equalTo(postId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    String pTimeStamp = "" + ds.child("pTime").getValue();
                    String imagePost = "" + ds.child("pImage").getValue();
                    String pCommentCount = "" + ds.child("pComments").getValue();
                    String postLikes = "" + ds.child("pLikes").getValue();

                    txt_sumlike_showimage.setText(postLikes + " Likes");

                    txt_sumcomment_showimage.setText(pCommentCount + " Comments");

                    txt_date_showimage.setText(Controller.convertDateTime(pTimeStamp));

                    Picasso.get().load(imagePost).placeholder(R.drawable.icon_logoapp).into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                            img_showimage_post.setImageBitmap(bitmap);

                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Controller.appLogDebug(Const.LOG_DAT, databaseError.getMessage().toString());

            }
        });
    }
}
