package com.example.socialminibtd.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.socialminibtd.Model.ListUser;
import com.example.socialminibtd.R;
import com.example.socialminibtd.Utils.Const;
import com.example.socialminibtd.Utils.Controller;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class AddParticipantAdapter extends RecyclerView.Adapter<AddParticipantAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ListUser> arrayList;
    private String groupId, myGroupRole; // creator//admin

    public AddParticipantAdapter(Context context, ArrayList<ListUser> arrayList, String groupId, String myGroupRole) {
        this.context = context;
        this.arrayList = arrayList;
        this.groupId = groupId;
        this.myGroupRole = myGroupRole;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_participant_add, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final ListUser listUser = arrayList.get(position);

        String name = listUser.getName();
        String img = listUser.getImage();
        String email = listUser.getEmail();
        final String uid = listUser.getUid();

        holder.txt_hisname_addparticipant.setText(name);
        holder.txt_email_addparticipant.setText(email);

        try {

            Picasso.get().load(img).placeholder(R.drawable.ic_account).into(holder.img_account_addparticipant);

        } catch (Exception e) {

            Picasso.get().load(R.drawable.ic_account).into(holder.img_account_addparticipant);

        }


        checkIfAlreadyExists(listUser, holder);

        //handle click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*If added: show remove-participants/make-admin/remove-admin option (Admin will able change role of create)
                 * If not added: show option */

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
                databaseReference.child(groupId).child("Participants").child(uid)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()) {

                                    // user exists/ participants
                                    String hisPreRole = String.valueOf(dataSnapshot.child("role").getValue());

                                    String[] option;
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Choose Option");

                                    if (myGroupRole.equals("creator")) {

                                        if (hisPreRole.equals("admin")) {


                                            //im creator, he is admin
                                            option = new String[]{"Remove Admin", "Remove User"};

                                            builder.setItems(option, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    if (which == 0) {

                                                        //remove admin
                                                        removeAdmin(listUser);

                                                    } else {

                                                        //remove user
                                                        removeParticipant(listUser);

                                                    }

                                                }
                                            }).show();

                                        } else if (hisPreRole.equals("participant")) {


                                            //im creator, he is participant
                                            option = new String[]{"Make Admin", "Remove User"};
                                            builder.setItems(option, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    if (which == 0) {

                                                        //remove admin
                                                        makeAdmin(listUser);

                                                    } else {

                                                        //remove user
                                                        removeParticipant(listUser);

                                                    }

                                                }
                                            }).show();

                                        }


                                    } else if (myGroupRole.equals("admin")) {

                                        if (hisPreRole.equals("creator")) {


                                            //im admin, he is creator
                                            Toast.makeText(context, "Creator of Group..." + listUser.getName(), Toast.LENGTH_SHORT).show();

                                        } else if (hisPreRole.equals("admin")) {


                                            //im admin, he is admin too
                                            option = new String[]{"Remove Admin", "Remove User"};

                                            builder.setItems(option, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    if (which == 0) {
                                                        //remove admin
                                                        removeAdmin(listUser);

                                                    } else {
                                                        //remove user

                                                        removeParticipant(listUser);


                                                    }

                                                }
                                            }).show();

                                        } else if (hisPreRole.equals("participant")) {


                                            //im admin, he is participant
                                            option = new String[]{"Make Admin", "Remove User"};
                                            builder.setItems(option, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    if (which == 0) {
                                                        //remove admin
                                                        makeAdmin(listUser);

                                                    } else {
                                                        //remove user

                                                        removeParticipant(listUser);


                                                    }

                                                }
                                            }).show();

                                        }

                                    }


                                } else {

                                    //user doesn't exist/not- participant:add
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Add Participant")
                                            .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    //add user
                                                    Controller.showProgressDialog(context, "Add Participant...");
                                                    addParticipants(listUser);

                                                }
                                            }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            dialog.dismiss();
                                        }
                                    });

                                    builder.create().show();

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                Controller.appLogDebug(Const.LOG_DAT, "checkIfAlreadyExists :" + databaseError.getMessage());
                            }
                        });

            }
        });


    }

    private void addParticipants(ListUser listUser) {

        //setup user data - add user in group
        String timeStamp = String.valueOf(System.currentTimeMillis());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", listUser.getUid());
        hashMap.put("timeStamp", timeStamp);
        hashMap.put("role", "participant");

        //add that user in Group>groupId>Participants

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
        databaseReference.child(groupId)
                .child("Participants")
                .child(listUser.getUid())
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        //add successfully
                        Controller.dimissProgressDialog();
                        Toast.makeText(context, "Add successfully...", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Controller.dimissProgressDialog();
                Controller.appLogDebug(Const.LOG_DAT, "addParticipants :" + e.toString());
                Toast.makeText(context, "Add Participants Failed", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void makeAdmin(ListUser listUser) {

        //setup user data - makeadmin user in group
        HashMap<String, Object> map = new HashMap<>();
        map.put("role", "admin");// roles are: participant/admin/creator

        //update node ""role" to admin
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupId)
                .child("Participants")
                .child(listUser.getUid())
                .updateChildren(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(context, "The user is now admin...", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Controller.appLogDebug(Const.LOG_DAT, "makeAdmin :" + e.toString());
                Toast.makeText(context, "Make Admin Failed", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void removeParticipant(ListUser listUser) {

        //setup user data - remove participant in group
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupId)
                .child("Parcitipants")
                .child(listUser.getUid())
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        //removed successfully
                        Toast.makeText(context, "Removed successfully...", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Controller.appLogDebug(Const.LOG_DAT, "removeParticipant :" + e.toString());
                Toast.makeText(context, "Remove Participant Failed", Toast.LENGTH_SHORT).show();


            }
        });

    }

    private void removeAdmin(ListUser listUser) {

        //setup user data - remove admin - just change to participant
        HashMap<String, Object> map = new HashMap<>();
        map.put("role", "participant");// roles are: participant/admin/creator

        //update node ""role" to admin
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupId)
                .child("Participants")
                .child(listUser.getUid())
                .updateChildren(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(context, "The user is no longer amdin...", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Controller.appLogDebug(Const.LOG_DAT, "removeAdmin :" + e.toString());
                Toast.makeText(context, "Remove Admin Failed", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void checkIfAlreadyExists(ListUser listUser, final ViewHolder holder) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
        databaseReference.child(groupId)
                .child("Participants")
                .child(listUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {

                            String role = String.valueOf(dataSnapshot.child("role").getValue());

                            holder.txt_status_addparticipant.setText(role);


                        } else {
                            //not exists

                            holder.txt_status_addparticipant.setText("");

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        Controller.appLogDebug(Const.LOG_DAT, "checkIfAlreadyExists :" + databaseError.getMessage());

                    }
                });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircularImageView img_account_addparticipant;
        private TextView txt_hisname_addparticipant, txt_email_addparticipant, txt_status_addparticipant;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img_account_addparticipant = itemView.findViewById(R.id.img_account_addparticipant);
            txt_hisname_addparticipant = itemView.findViewById(R.id.txt_hisname_addparticipant);
            txt_email_addparticipant = itemView.findViewById(R.id.txt_email_addparticipant);
            txt_status_addparticipant = itemView.findViewById(R.id.txt_status_addparticipant);
        }
    }
}
