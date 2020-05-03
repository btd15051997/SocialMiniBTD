package com.example.socialminibtd.View.Dialog.FollowedBy;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialminibtd.Adapter.ListUserAdapter;
import com.example.socialminibtd.Model.ListUser;
import com.example.socialminibtd.R;
import com.example.socialminibtd.Utils.Const;
import com.example.socialminibtd.Utils.Controller;
import com.example.socialminibtd.View.Activity.Dashbroad.DashboardActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Ref;
import java.util.ArrayList;

public class BottomDialogFragment extends BottomSheetDialogFragment {

    private DashboardActivity mDashboardActivity;
    private FirebaseAuth firebaseAuth;
    private View view;
    private RecyclerView recyc_listuser_followed;
    private ArrayList<ListUser> arrayList;
    private ListUserAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDashboardActivity = (DashboardActivity) getActivity();
        firebaseAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.dialog_followby, container, false);

        recyc_listuser_followed = view.findViewById(R.id.recyc_listuser_followed);
        recyc_listuser_followed.setHasFixedSize(true);

        onGetListUserFollowedBy();

        return view;
    }

    private void onGetListUserFollowedBy() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Follow");
        databaseReference.child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    String hisUserFollowed = ds.getRef().getKey();

                    Controller.appLogDebug(Const.LOG_DAT, "hisUserFollowed :"+hisUserFollowed);

                    onGetUserFollowed(hisUserFollowed);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Controller.appLogDebug(Const.LOG_DAT, databaseError.getMessage());

            }
        });



    }

    private void onGetUserFollowed(String hisUidLiked) {

        arrayList = new ArrayList<>();

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

                adapter = new ListUserAdapter(arrayList, mDashboardActivity);

                recyc_listuser_followed.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Controller.appLogDebug(Const.LOG_DAT, databaseError.getMessage());

            }
        });

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
