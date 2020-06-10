package com.example.socialminibtd.View.Fragment.ListGroupChatFragment;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.socialminibtd.Adapter.ListGroupAdapter;
import com.example.socialminibtd.Model.ModelListGroup;
import com.example.socialminibtd.R;
import com.example.socialminibtd.Utils.Const;
import com.example.socialminibtd.Utils.Controller;
import com.example.socialminibtd.View.Activity.Dashbroad.DashboardActivity;
import com.example.socialminibtd.View.Dialog.GroupChatsDialog.GroupChatsDialog;
import com.example.socialminibtd.View.Dialog.GroupCreate.GroupCreateDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListGroupChatFragment extends Fragment implements IListChatGroupView {

    private View view;
    private DashboardActivity dashboardActivity;
    private FirebaseAuth auth;

    //view
    private RecyclerView recyc_list_group;
    private LinearLayout show_not_join_any_groups;

    //array,adapter
    private ArrayList<ModelListGroup> arrayList;
    private ListGroupAdapter groupAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dashboardActivity = (DashboardActivity) getActivity();
        auth = FirebaseAuth.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_list_group_chat, container, false);

        onMappingViewGroupChats();

        onGetListGroupChats();

        return view;
    }

    @Override
    public void onMappingViewGroupChats() {

        recyc_list_group = view.findViewById(R.id.recyc_list_group);
        show_not_join_any_groups = view.findViewById(R.id.show_not_join_any_groups);

        show_not_join_any_groups.setOnClickListener(v -> {

            onIntentCreateGroup();

        });

    }

    private void onIntentCreateGroup() {

        GroupCreateDialog dialog_group = new GroupCreateDialog();
        dialog_group.setCancelable(true);
        dialog_group.show(getFragmentManager(), "GroupCreateDialog");
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

                if (!arrayList.isEmpty()) {

                    TranslateAnimation animate = new TranslateAnimation(0, show_not_join_any_groups.getWidth(), 0, 0);
                    animate.setDuration(500);
                    animate.setFillAfter(true);
                    show_not_join_any_groups.startAnimation(animate);
                    show_not_join_any_groups.setVisibility(View.GONE);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Controller.appLogDebug(Const.LOG_DAT, databaseError.getMessage());

            }
        });


    }
}
