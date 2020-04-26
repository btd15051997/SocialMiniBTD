package com.example.socialminibtd.View.Dialog.PostLikedBy;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialminibtd.Adapter.ListUserAdapter;
import com.example.socialminibtd.Model.ListUser;
import com.example.socialminibtd.Notifications.ModelNotifi.Data;
import com.example.socialminibtd.R;
import com.example.socialminibtd.Utils.Const;
import com.example.socialminibtd.Utils.Controller;
import com.example.socialminibtd.View.Activity.Dashbroad.DashboardActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PostLikedByDialog extends DialogFragment implements IPostLikedByDialogView {

    private DashboardActivity dashboardActivity;
    private RecyclerView recyc_postlikedby;
    private String postID;
    private ArrayList<ListUser> arrayList;
    private ListUserAdapter adapter;
    private FirebaseAuth auth;
    private TextView txt_myemail_postliked;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dashboardActivity = (DashboardActivity) getActivity();

        if (getArguments() != null) {

            postID = getArguments().getString("postID");

            Log.d(Const.LOG_DAT, postID + " TEST");

        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Dialog dialog_postliked = new Dialog(dashboardActivity, R.style.DialogThemeforview);
        dialog_postliked.setContentView(R.layout.dialog_postliked_by);
        recyc_postlikedby = dialog_postliked.findViewById(R.id.recyc_postlikedby);
        txt_myemail_postliked = dialog_postliked.findViewById(R.id.txt_myemail_postliked);

        auth = FirebaseAuth.getInstance();

        txt_myemail_postliked.setText(auth.getCurrentUser().getEmail().toString());

        onGetUserLikedPost();

        return dialog_postliked;
    }

    @Override
    public void onGetUserLikedPost() {

        arrayList = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Likes");
        databaseReference.child(postID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    String hisUidLiked = ds.getRef().getKey();

                    onGetUserLiked(hisUidLiked);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Controller.appLogDebug(Const.LOG_DAT, databaseError.getMessage());

            }
        });


    }

    private void onGetUserLiked(String hisUidLiked) {

        DatabaseReference Ref = FirebaseDatabase.getInstance().getReference("User");
        Ref.orderByChild("uid").equalTo(hisUidLiked).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (arrayList != null) {

                    arrayList.clear();
                }


                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    ListUser listUser = ds.getValue(ListUser.class);

                    arrayList.add(listUser);

                }

                adapter = new ListUserAdapter(arrayList, dashboardActivity);

                recyc_postlikedby.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Controller.appLogDebug(Const.LOG_DAT, databaseError.getMessage());

            }
        });


    }
}
