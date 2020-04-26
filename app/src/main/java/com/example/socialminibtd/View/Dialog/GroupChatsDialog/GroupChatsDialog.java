package com.example.socialminibtd.View.Dialog.GroupChatsDialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialminibtd.Adapter.ListGroupAdapter;
import com.example.socialminibtd.Model.ModelListGroup;
import com.example.socialminibtd.R;
import com.example.socialminibtd.Utils.Const;
import com.example.socialminibtd.Utils.Controller;
import com.example.socialminibtd.View.Activity.Dashbroad.DashboardActivity;
import com.example.socialminibtd.View.Dialog.GroupCreate.GroupCreateDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupChatsDialog extends DialogFragment implements IGroupChatsView {

    private Dialog dialog_group;
    private DashboardActivity dashboardActivity;
    private FirebaseAuth auth;

    //view
    private RecyclerView recyc_list_group;

    //array,adapter

    private ArrayList<ModelListGroup> arrayList;
    private ListGroupAdapter groupAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dashboardActivity = (DashboardActivity) getActivity();
        auth = FirebaseAuth.getInstance();

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        dialog_group = new Dialog(dashboardActivity, R.style.DialogThemeforview);

        dialog_group.setContentView(R.layout.dialog_group_chats);

        onMappingViewGroupChats();

        onGetListGroupChats();

        return dialog_group;

    }

    @Override
    public void onMappingViewGroupChats() {

        recyc_list_group = dialog_group.findViewById(R.id.recyc_list_group);

    }

    @Override
    public void onGetListGroupChats() {

        arrayList = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (arrayList != null) {

                    arrayList.clear();
                }

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //if current user's uid exists in participants list of group then show that group
                    if (ds.child("Participants").child(auth.getCurrentUser().getUid()).exists()) {

                        ModelListGroup listGroup = ds.getValue(ModelListGroup.class);
                        arrayList.add(listGroup);


                    }

                    groupAdapter = new ListGroupAdapter(arrayList, dashboardActivity);
                    recyc_list_group.setAdapter(groupAdapter);


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Controller.appLogDebug(Const.LOG_DAT, databaseError.getMessage());

            }
        });

    }
}
