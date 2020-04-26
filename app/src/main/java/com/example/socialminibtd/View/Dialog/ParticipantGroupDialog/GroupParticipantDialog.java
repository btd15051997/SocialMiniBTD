package com.example.socialminibtd.View.Dialog.ParticipantGroupDialog;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialminibtd.Adapter.AddParticipantAdapter;
import com.example.socialminibtd.Model.ListUser;
import com.example.socialminibtd.R;
import com.example.socialminibtd.View.Activity.ChatGroup.GroupChatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupParticipantDialog extends DialogFragment implements IGroupParticipantDialog {

    private GroupChatActivity chatActivity;
    private RecyclerView recyc_showlist_participant;

    private String groupId;
    private String myGroupRole2 = "";
    private ArrayList<ListUser> userArrayList;
    private AddParticipantAdapter participantAdapter;
    private TextView txt_your_role_participant;
    private FirebaseAuth auth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        chatActivity = (GroupChatActivity) getActivity();
        auth = FirebaseAuth.getInstance();

        if (getArguments() != null) {

            groupId = getArguments().getString("groupID");

        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Dialog dialog = new Dialog(chatActivity, R.style.DialogThemeforview);
        dialog.setContentView(R.layout.dialog_participant_group);
        dialog.setCancelable(true);

        recyc_showlist_participant = dialog.findViewById(R.id.recyc_showlist_participant);
        txt_your_role_participant = dialog.findViewById(R.id.txt_your_role_participant);

        onLoadInfoGroup();

       // onShowListUserForPar();

        return dialog;
    }

    @Override
    public void onLoadInfoGroup() {

        final DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Groups");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.orderByChild("groupId").equalTo(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    final String groupId2 = String.valueOf(ds.child("groupId").getValue());


                    reference1.child(groupId2).child("Participants")
                            .child(auth.getCurrentUser().getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.exists()){


                                            myGroupRole2 = String.valueOf(dataSnapshot.child("role").getValue());

                                            txt_your_role_participant.setText(myGroupRole2);

                                            onShowListUserForPar(myGroupRole2);


                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {


                                }
                            });

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onShowListUserForPar(final String myGroupRole) {

        userArrayList = new ArrayList<>();
        recyc_showlist_participant.setHasFixedSize(true);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (userArrayList != null) {

                    userArrayList.clear();
                }

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ListUser listUser = ds.getValue(ListUser.class);

                    if(!auth.getCurrentUser().getUid().equals(listUser.getUid())){

                        userArrayList.add(listUser);

                    }

                }

                participantAdapter = new AddParticipantAdapter(chatActivity, userArrayList, groupId, myGroupRole);

                recyc_showlist_participant.setAdapter(participantAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}
