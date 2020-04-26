package com.example.socialminibtd.View.Dialog.ViewProfileDialog;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.socialminibtd.Adapter.ListPostAdapter;
import com.example.socialminibtd.Model.ListPost;
import com.example.socialminibtd.Notifications.ModelNotifi.Data;
import com.example.socialminibtd.R;
import com.example.socialminibtd.Utils.Const;
import com.example.socialminibtd.Utils.Controller;
import com.example.socialminibtd.View.Activity.ChatUser.ChatActivity;
import com.example.socialminibtd.View.Activity.Dashbroad.DashboardActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ViewProfileDialog extends DialogFragment implements IViewProfileDialog {

    private DashboardActivity dashboardActivity;
    private String hisUid = "";
    private String myUid = "";

    private FirebaseAuth auth;

    //view
    private ImageView img_background_hisprofile,img_back_hisprofile;
    private CircularImageView img_avatar_hisprofile;
    private TextView txt_name_hisprofile, txt_email_hisprofile, txt_phone_hisprofile,txt_send_message_hisprofile;
    private RecyclerView recyc_listpost_hisprofile;
    private LinearLayout show_not_have_post;
    //listpost
    private ArrayList<ListPost> PostArrayList;
    private ListPostAdapter postAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dashboardActivity = (DashboardActivity) getActivity();

        auth = FirebaseAuth.getInstance();

        myUid = auth.getCurrentUser().getUid();

        if (getArguments() != null) {

            hisUid = getArguments().getString("hisUid");

            Log.d("Testtt", hisUid);

        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Dialog dialog_viewprofile = new Dialog(dashboardActivity, R.style.DialogThemeforview);

        dialog_viewprofile.setContentView(R.layout.dialog_view_profile);

        img_background_hisprofile = dialog_viewprofile.findViewById(R.id.img_background_hisprofile);
        img_avatar_hisprofile = dialog_viewprofile.findViewById(R.id.img_avatar_hisprofile);
        txt_name_hisprofile = dialog_viewprofile.findViewById(R.id.txt_name_hisprofile);
        txt_email_hisprofile = dialog_viewprofile.findViewById(R.id.txt_email_hisprofile);
        txt_phone_hisprofile = dialog_viewprofile.findViewById(R.id.txt_phone_hisprofile);
        txt_send_message_hisprofile = dialog_viewprofile.findViewById(R.id.txt_send_message_hisprofile);
        img_back_hisprofile = dialog_viewprofile.findViewById(R.id.img_back_hisprofile);
        recyc_listpost_hisprofile = dialog_viewprofile.findViewById(R.id.recyc_listpost_hisprofile);
        show_not_have_post = dialog_viewprofile.findViewById(R.id.show_not_have_post);

        img_back_hisprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getDialog().isShowing()){

                    getDialog().dismiss();
                }

            }
        });

        txt_send_message_hisprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                imBlockORNot(hisUid);

                
            }
        });

        onShowInfoHisProfile();

        onShowListPostHisProfile(hisUid);

        return dialog_viewprofile;
    }

    @Override
    public void imBlockORNot(final String hisUid) {

        //if other blocked you, you can't send message to that user
        DatabaseReference Ref = FirebaseDatabase.getInstance().getReference("User");
        Ref.child(hisUid).child("BlockedUSers").orderByChild("uid").equalTo(myUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            if (ds.exists()) {

                                Controller.showLongToast("You're blocked by that user, can't send message", dashboardActivity);

                                return;
                            }

                        }
                        // intent chat, if unblocked
                        Intent intent = new Intent(dashboardActivity, ChatActivity.class);
                        intent.putExtra("hisUid", hisUid);
                        startActivity(intent);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        Controller.appLogDebug(Const.LOG_DAT, " Failed  BlockUser " + databaseError.toString());

                    }
                });

    }

    @Override
    public void onShowListPostHisProfile(final String hisUid) {

        PostArrayList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (PostArrayList != null) {

                    PostArrayList.clear();

                }

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    ListPost listPost = ds.getValue(ListPost.class);

                    if (hisUid.equals(listPost.getUid())) {

                        PostArrayList.add(listPost);

                    }
                }

                postAdapter = new ListPostAdapter(PostArrayList, dashboardActivity);

                recyc_listpost_hisprofile.setAdapter(postAdapter);

                if (!PostArrayList.isEmpty()){

                    TranslateAnimation animate = new TranslateAnimation(0,show_not_have_post.getWidth(),0,0);
                    animate.setDuration(500);
                    animate.setFillAfter(true);
                    show_not_have_post.startAnimation(animate);
                    show_not_have_post.setVisibility(View.GONE);

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Controller.showLongToast(databaseError.toString(), dashboardActivity);

            }
        });

    }

    @Override
    public void onShowInfoHisProfile() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
        reference.orderByChild("uid")
                .equalTo(hisUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    String nameHis = String.valueOf(ds.child("name").getValue());
                    String emailhis = String.valueOf(ds.child("email").getValue());
                    String phonehis = String.valueOf(ds.child("phone").getValue());
                    String imagehis = String.valueOf(ds.child("image").getValue());
                    String imagecoverhis = String.valueOf(ds.child("image_cover").getValue());

                    txt_name_hisprofile.setText(nameHis);
                    txt_email_hisprofile.setText(emailhis);
                    txt_phone_hisprofile.setText(phonehis);


                    try {

                        Picasso.get().load(imagehis).placeholder(R.drawable.ic_account).into(img_avatar_hisprofile);

                    }catch (Exception e){

                        img_avatar_hisprofile.setImageResource(R.drawable.icon_logoapp);

                    }


                    try {

                        Picasso.get().load(imagecoverhis).placeholder(R.color.black_two).into(img_background_hisprofile);

                    }catch (Exception e){

                        img_background_hisprofile.setBackgroundResource(R.color.white_two);

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
