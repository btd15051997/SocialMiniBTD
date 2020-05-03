package com.example.socialminibtd.View.Dialog.ViewProfileDialog;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.HashMap;
import java.util.Map;

public class ViewProfileDialog extends DialogFragment implements IViewProfileDialog {

    private DashboardActivity dashboardActivity;
    private String hisUid = "";
    private String myUid = "";
    private int CountFL = 0;
    private FirebaseAuth auth;
    private boolean mProgressLike = false;

    //view
    private ImageView img_background_hisprofile, img_back_hisprofile;
    private CircularImageView img_avatar_hisprofile;
    private TextView txt_name_hisprofile, txt_email_hisprofile, txt_phone_hisprofile, txt_send_message_hisprofile;
    private RecyclerView recyc_listpost_hisprofile;
    private LinearLayout show_not_have_post;
    private Button btn_click_to_follow;
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
        btn_click_to_follow = dialog_viewprofile.findViewById(R.id.btn_click_to_follow);
        recyc_listpost_hisprofile = dialog_viewprofile.findViewById(R.id.recyc_listpost_hisprofile);
        show_not_have_post = dialog_viewprofile.findViewById(R.id.show_not_have_post);

        onCheckCountAndFollow();

        DatabaseReference counFLtRef = FirebaseDatabase.getInstance()
                .getReference("User")
                .child(hisUid)
                .child("Follow");


        DatabaseReference databaseFollow = FirebaseDatabase.getInstance().getReference("Follow");

        databaseFollow.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(hisUid).hasChild(auth.getUid())) {

                    btn_click_to_follow.setText("Followed");
                    btn_click_to_follow.setBackgroundResource(R.color.black_three);

                } else {

                    btn_click_to_follow.setText("Follow");
                    final int sdk = android.os.Build.VERSION.SDK_INT;
                    if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        btn_click_to_follow.setBackgroundDrawable(ContextCompat.getDrawable(dashboardActivity, R.drawable.btn_custom_signin_up));
                    } else {
                        btn_click_to_follow.setBackground(ContextCompat.getDrawable(dashboardActivity, R.drawable.btn_custom_signin_up));
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        counFLtRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                CountFL = Integer.parseInt("" + dataSnapshot.child("fCountFl").getValue());
                Controller.appLogDebug(Const.LOG_DAT, "fCountFl : " + CountFL);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        img_back_hisprofile.setOnClickListener(v -> {

            if (getDialog().isShowing()) {

                getDialog().dismiss();
            }

        });

        txt_send_message_hisprofile.setOnClickListener(v -> imBlockORNot(hisUid));


        btn_click_to_follow.setOnClickListener(v -> {

            mProgressLike = true;


            databaseFollow.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (mProgressLike) {

                        if (dataSnapshot.child(hisUid).hasChild(auth.getUid())) {

                            Log.d("DAT", "TON TAI");
                            counFLtRef.child("fCountFl").setValue(CountFL-1+"");

                            databaseFollow.child(hisUid).child(auth.getUid()).removeValue();

                            mProgressLike = false;

                        } else {

                            Log.d("DAT", "CHUA TON TAI");

                            counFLtRef.child("fCountFl").setValue(CountFL+1+"");

                            databaseFollow.child(hisUid).child(auth.getUid()).setValue("Followed"); // set any value

                            mProgressLike = false;
                        }

                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


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

                if (!PostArrayList.isEmpty()) {

                    TranslateAnimation animate = new TranslateAnimation(0, show_not_have_post.getWidth(), 0, 0);
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

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

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

                            } catch (Exception e) {

                                img_avatar_hisprofile.setImageResource(R.drawable.icon_logoapp);

                            }


                            try {

                                Picasso.get().load(imagecoverhis).placeholder(R.color.black_two).into(img_background_hisprofile);

                            } catch (Exception e) {

                                img_background_hisprofile.setBackgroundResource(R.color.white_two);

                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    @Override
    public void onCheckCountAndFollow() {

    }
}
