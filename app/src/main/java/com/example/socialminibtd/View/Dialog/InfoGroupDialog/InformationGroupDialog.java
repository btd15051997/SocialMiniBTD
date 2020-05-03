package com.example.socialminibtd.View.Dialog.InfoGroupDialog;


import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.socialminibtd.Adapter.AddParticipantAdapter;
import com.example.socialminibtd.Model.ListUser;
import com.example.socialminibtd.R;
import com.example.socialminibtd.Utils.Const;
import com.example.socialminibtd.Utils.Controller;
import com.example.socialminibtd.View.Activity.ChatGroup.GroupChatActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class InformationGroupDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    private GroupChatActivity groupChatActivity;
    private FirebaseAuth auth;
    private String groupID, myRole;
    private View view;

    private RecyclerView recyc_participant_inforgroup;
    private ArrayList<ListUser> userArrayList;
    private AddParticipantAdapter participantAdapter;

    private TextView txt_description_inforgroup, txt_namecreator_inforgroup, txt_editgroup_inforgroup, txt_leavegroup_inforgroup, txt_participant_n_inforgroup,
            txt_title_inforgroup;

    private CircularImageView img_imgmain_group_infor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        groupChatActivity = (GroupChatActivity) getActivity();

        auth = FirebaseAuth.getInstance();

        if (getArguments() != null) {

            groupID = getArguments().getString("groupID");

            Log.d("TESST", groupID);

        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.dialog_information_group, container, false);

        txt_description_inforgroup = view.findViewById(R.id.txt_description_inforgroup);
        txt_namecreator_inforgroup = view.findViewById(R.id.txt_namecreator_inforgroup);
        img_imgmain_group_infor = view.findViewById(R.id.img_imgmain_group_infor);
        txt_title_inforgroup = view.findViewById(R.id.txt_title_inforgroup);

        txt_editgroup_inforgroup = view.findViewById(R.id.txt_editgroup_inforgroup);
        txt_leavegroup_inforgroup = view.findViewById(R.id.txt_leavegroup_inforgroup);
        txt_participant_n_inforgroup = view.findViewById(R.id.txt_participant_n_inforgroup);
        recyc_participant_inforgroup = view.findViewById(R.id.recyc_participant_inforgroup);

        recyc_participant_inforgroup.setHasFixedSize(true);
        txt_leavegroup_inforgroup.setOnClickListener(this);
        txt_editgroup_inforgroup.setOnClickListener(this);

        onLoadGroupInfo(groupID);

        onLoadMyGroupRole();

        return view;
    }

//    @NonNull
//    @Override
//    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//
//        Dialog dialog = new Dialog(groupChatActivity, R.style.DialogThemeforview);
//        dialog.setContentView(R.layout.dialog_information_group);
//        dialog.setCancelable(true);
//
//
//
//
//        return dialog;
//
//
//    }

    private void onLoadMyGroupRole() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
        databaseReference.child(groupID)
                .child("Participants")
                .orderByChild("uid")
                .equalTo(auth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            myRole = String.valueOf(ds.child("role").getValue());

                            if (myRole.equals("participant")) {

                                txt_editgroup_inforgroup.setVisibility(View.GONE);
                                txt_leavegroup_inforgroup.setText("Leave Group");

                            } else if (myRole.equals("admin")) {

                                txt_editgroup_inforgroup.setVisibility(View.GONE);
                                txt_leavegroup_inforgroup.setText("Leave Group");


                            } else if (myRole.equals("creator")) {

                                txt_editgroup_inforgroup.setVisibility(View.VISIBLE);
                                txt_leavegroup_inforgroup.setText("Delete Group");


                            }

                        }

                        onLoadParticipant();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void onLoadParticipant() {

        userArrayList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupID).child("Participants")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (userArrayList != null) {

                            userArrayList.clear();

                        }

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            String uid = String.valueOf(ds.child("uid").getValue());

                            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("User");

                            reference1.orderByChild("uid").equalTo(uid)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                                ListUser listUser = ds.getValue(ListUser.class);

                                                userArrayList.add(listUser);
                                            }

                                            participantAdapter = new AddParticipantAdapter(groupChatActivity, userArrayList, groupID, myRole);

                                            txt_participant_n_inforgroup.setText("Participants :" + userArrayList.size());

                                            recyc_participant_inforgroup.setAdapter(participantAdapter);

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        Controller.appLogDebug(Const.LOG_DAT, "onLoadParticipant :" + databaseError.getMessage());

                    }
                });

    }

    private void onLoadGroupInfo(String groupID) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.orderByChild("groupId").equalTo(groupID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                            String createby = String.valueOf(dataSnapshot1.child("createBy").getValue());
                            String groupId = String.valueOf(dataSnapshot1.child("groupId").getValue());
                            String groupTitle = String.valueOf(dataSnapshot1.child("groupTitle").getValue());
                            String groupIcon = String.valueOf(dataSnapshot1.child("groupIcon").getValue());

                            String groupTimeCreate = String.valueOf(dataSnapshot1.child("timeStamp").getValue());
                            String groupDescription = String.valueOf(dataSnapshot1.child("groupDescription").getValue());

                            txt_title_inforgroup.setText("Title Group :" + groupTitle);
                            txt_description_inforgroup.setText(groupDescription);


                            String timeCreate = Controller.convertDateTime(groupTimeCreate);

                            onLoadinforCreator(timeCreate, createby);

                            try {

                                Picasso.get().load(groupIcon).placeholder(R.drawable.icon_logoapp).into(img_imgmain_group_infor);


                            } catch (Exception e) {

                                img_imgmain_group_infor.setImageResource(R.drawable.icon_logoapp);

                            }


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        Controller.appLogDebug(Const.LOG_DAT, "onLoadGroupInfo :" + databaseError.getMessage());

                    }
                });

    }

    private void onLoadinforCreator(final String groupTimeCreate, String createby) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
        reference.orderByChild("uid").equalTo(createby)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {


                            String nameCreator = String.valueOf(ds.child("name").getValue());

                            txt_namecreator_inforgroup.setText("Create By :" + nameCreator
                                    + " On :" + groupTimeCreate);

                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        Controller.appLogDebug(Const.LOG_DAT, "onLoadinforCreator :" + databaseError.getMessage());

                    }
                });

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {


            case R.id.txt_editgroup_inforgroup:

                onShowDialogUpdateGroup();

                break;

            case R.id.txt_leavegroup_inforgroup:

                if (myRole.equals("creator")) {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(groupChatActivity);

                    alertDialog.setTitle("Do you want to delete the groups ?");

                    alertDialog.setNegativeButton("Yes", (dialog, which) -> {

                        Query query = FirebaseDatabase.getInstance()
                                .getReference("Groups")
                                .orderByChild("groupId").equalTo(groupID);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                    ds.getRef().removeValue(); // remove values from firebase when pid matches

                                }
                                Controller.showLongToast("Deleted successfully", groupChatActivity);

                                getActivity().finish();


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                Controller.dimissProgressDialog();
                                Controller.appLogDebug(Const.LOG_DAT, databaseError.getMessage());
                                Controller.showLongToast(databaseError.getMessage().toString(), groupChatActivity);

                            }
                        });


                    });

                    alertDialog.setPositiveButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();

                        }
                    });

                    AlertDialog dialog = alertDialog.create();
                    dialog.show();


                }

                break;


        }

    }

    private void onShowDialogUpdateGroup() {

        String[] array = {"Edit Title", "Edit Description"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(null);
        builder.setItems(array, (dialog, which) -> {

            switch (which) {

                case 0:

                    onUpdateTitleDescriptionGroup("title");


                    break;

                case 1:

                    onUpdateTitleDescriptionGroup("description");

                    break;

            }

        });

        builder.create().show();

    }

    private void onUpdateTitleDescriptionGroup(String key) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getActivity().getResources().getString(R.string.txt_update) + " " + key);

        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10, 10, 10, 10);

        final EditText editTextUpdate = new EditText(getActivity());
        editTextUpdate.setHint(getActivity().getResources().getString(R.string.txt_enter) + " " + key);
        linearLayout.addView(editTextUpdate);

        builder.setView(linearLayout);

        builder.setPositiveButton(getActivity().getResources().getString(R.string.txt_update), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                final String value = editTextUpdate.getText().toString().trim();

                if (editTextUpdate.getText().toString().isEmpty()) {

                    Controller.showLongToast("Please Enter " + key, getActivity());

                } else {

                    Controller.showProgressDialog(getActivity(), getActivity().getResources().getString(R.string.txt_loading));

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
                    databaseReference.child(groupID).orderByChild("groupId").equalTo(groupID)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    Controller.dimissProgressDialog();

                                    if (key.equals("title")) {

                                        dataSnapshot.getRef().child("groupTitle").setValue(value);

                                    } else if (key.equals("description")) {

                                        dataSnapshot.getRef().child("groupDescription").setValue(value);

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                    Controller.dimissProgressDialog();
                                    Controller.appLogDebug(Const.LOG_DAT, "Update title group :" + databaseError.getMessage());

                                }
                            });


                }

            }
        });

        builder.setNegativeButton(getActivity().getResources().getString(R.string.txt_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }
        });


        builder.create().show();

    }


}
