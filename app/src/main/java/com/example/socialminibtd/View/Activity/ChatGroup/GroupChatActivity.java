package com.example.socialminibtd.View.Activity.ChatGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.socialminibtd.Adapter.ChatGroupsAdapter;
import com.example.socialminibtd.Model.ModelChatGroup;
import com.example.socialminibtd.R;
import com.example.socialminibtd.Utils.Const;
import com.example.socialminibtd.Utils.Controller;
import com.example.socialminibtd.View.Activity.ChatUser.ChatActivity;
import com.example.socialminibtd.View.Dialog.InfoGroupDialog.InformationGroupDialog;
import com.example.socialminibtd.View.Dialog.ParticipantGroupDialog.GroupParticipantDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class GroupChatActivity extends AppCompatActivity implements IGroupChatActivity, View.OnClickListener {


    private CircularImageView img_avt_chatgroup;
    private RecyclerView recyc_content_chatgroup;
    private ImageView img_attachfile_chatgroup, img_send_text_chatgroup, img_infor_groupchat;
    private TextView txt_name_chatgroup, txt_option_participant;
    private EditText edt_enter_text_chatgroup;

    //permissions constants
    private static final int CAMERA_REQUEST_CODE = 101;
    private static final int STORAGE_REQUEST_CODE = 102;

    private static final int IMAGE_PICK_GALLERY_REQUEST_CODE = 103;
    private static final int IMAGE_PICK_CAMERA_REQUEST_CODE = 104;

    private String[] cameraPermissions;
    private String[] storagePermissions;
    private Uri image_uri = null;

    private String GroupID, myGroupRole;

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;

    private ArrayList<ModelChatGroup> chatGroupArrayList;
    private ChatGroupsAdapter groupsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        onMappingViewGroupChat();

        GroupID = getIntent().getStringExtra("groupID");

        Log.d("TestGroup", GroupID);

        onGetInfoGroupChat();

        onGetChatDataGroupChat();

        onGetMyGroupRole();

    }

    @Override
    public void onMappingViewGroupChat() {

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        img_avt_chatgroup = findViewById(R.id.img_avt_chatgroup);
        recyc_content_chatgroup = findViewById(R.id.recyc_content_chatgroup);
        img_attachfile_chatgroup = findViewById(R.id.img_attachfile_chatgroup);
        img_send_text_chatgroup = findViewById(R.id.img_send_text_chatgroup);
        txt_name_chatgroup = findViewById(R.id.txt_name_chatgroup);
        edt_enter_text_chatgroup = findViewById(R.id.edt_enter_text_chatgroup);
        txt_option_participant = findViewById(R.id.txt_option_participant);
        img_infor_groupchat = findViewById(R.id.img_infor_groupchat);

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        img_send_text_chatgroup.setOnClickListener(this);
        txt_option_participant.setOnClickListener(this);
        img_infor_groupchat.setOnClickListener(this);
        recyc_content_chatgroup.setHasFixedSize(true);
        img_attachfile_chatgroup.setOnClickListener(this);


    }

    @Override
    public String onGetTimeCurrent() {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aaa");
        String datetime = dateformat.format(c.getTime());

        return datetime;
    }


    @Override
    public void onGetInfoGroupChat() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
        databaseReference.orderByChild("groupId").equalTo(GroupID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    if (GroupID.equals(ds.child("groupId").getValue())) {

                        String groupTitle = String.valueOf(ds.child("groupTitle").getValue());
                        String groupDescription = String.valueOf(ds.child("groupDescription").getValue());
                        String groupIcon = String.valueOf(ds.child("groupIcon").getValue());
                        String timeStamp = String.valueOf(ds.child("timeStamp").getValue());
                        String createBy = String.valueOf(ds.child("createBy").getValue());

                        Log.d("TestGroup", groupTitle);

                        //update UI
                        txt_name_chatgroup.setText(groupTitle);

                        try {

                            Picasso.get().load(groupIcon).placeholder(R.drawable.ic_group_add).into(img_avt_chatgroup);

                        } catch (Exception e) {

                            img_avt_chatgroup.setImageResource(R.drawable.ic_group_add);

                        }

                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Controller.appLogDebug(Const.LOG_DAT, "onGetInfoGroupChat :" + databaseError.getMessage().toString());


            }
        });

    }

    @Override
    public void onGetChatDataGroupChat() {

        chatGroupArrayList = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
        databaseReference.child(GroupID).child("Messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (chatGroupArrayList != null) {

                            chatGroupArrayList.clear();
                        }

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            ModelChatGroup modelChatGroup = ds.getValue(ModelChatGroup.class);

                            chatGroupArrayList.add(modelChatGroup);
                        }

                        groupsAdapter = new ChatGroupsAdapter(GroupChatActivity.this, chatGroupArrayList);

                        recyc_content_chatgroup.setAdapter(groupsAdapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {


                    }
                });


    }

    @Override
    public void onGetMyGroupRole() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
        databaseReference.child(GroupID).child("Participants").orderByChild("uid")
                .equalTo(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {

                            if (firebaseUser.getUid().equals(dataSnapshot.child("uid").getValue())) {

                                myGroupRole = String.valueOf(dataSnapshot.child("role").getValue());

                                Controller.appLogDebug(Const.LOG_DAT, "onGetMyGroupRole :" + myGroupRole.toString());
                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        Controller.appLogDebug(Const.LOG_DAT, "onGetMyGroupRole " + databaseError.getMessage());

                    }
                });

    }

    @Override
    public void onSendImageMessage(Uri image_uri) {

        Controller.showProgressDialog(GroupChatActivity.this, "Sending image... ");

        String timeStamp = String.valueOf(System.currentTimeMillis());
        String fileNameAndPath = "ChatImage/" + timeStamp;

        StorageReference reference = FirebaseStorage.getInstance().getReference().child(fileNameAndPath);


//        Bitmap bitmap = null;
//        try {
//
//            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image_uri);
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//        final byte[] data = baos.toByteArray();//convert image to bytes


        reference.putFile(image_uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> p_uriTask = taskSnapshot.getStorage().getDownloadUrl();

                        while (!p_uriTask.isSuccessful()) ;

                        Uri p_downLoadUri = p_uriTask.getResult();

                        if (p_uriTask.isSuccessful()) {

                            //put message data
                            String timeStamp = "" + System.currentTimeMillis();

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("sender", firebaseUser.getUid());
                            hashMap.put("message", "" + p_downLoadUri);
                            hashMap.put("timestamp", timeStamp);
                            hashMap.put("type", "image");//text/image

                            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Groups");
                            reference1.child(GroupID)
                                    .child("Messages")
                                    .child(timeStamp)
                                    .setValue(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            Controller.dimissProgressDialog();
                                            Toast.makeText(GroupChatActivity.this, "YESSSS", Toast.LENGTH_SHORT).show();
                                            edt_enter_text_chatgroup.setText("");

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Controller.appLogDebug(Const.LOG_DAT, e.toString());
                                    Controller.dimissProgressDialog();

                                }
                            });


                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Controller.dimissProgressDialog();
                Controller.appLogDebug(Const.LOG_DAT, "onSendImageMessage :" + e.getMessage());

            }
        });
    }


    @Override
    public void onSendMessageGroupChat(String textMessage) {

        //put message data
        String timeStamp = "" + System.currentTimeMillis();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", firebaseUser.getUid());
        hashMap.put("message", textMessage);
        hashMap.put("timestamp", timeStamp);
        hashMap.put("type", "text");//text/image

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
        databaseReference.child(GroupID)
                .child("Messages")
                .child(timeStamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Controller.dimissProgressDialog();
                        edt_enter_text_chatgroup.setText("");

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Controller.appLogDebug(Const.LOG_DAT, e.toString());
                Controller.dimissProgressDialog();

            }
        });


    }

    private boolean checkCameraPermission() {

        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);

        Controller.appLogDebug("CHECK PERMISSION", result + "");

        return result && result1;
    }

    private void requestCameraPermission() {

        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);

    }

    private boolean checkStoragePermission() {

        //true if enable
        //false if not anable

        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);

        Controller.appLogDebug("CHECK PERMISSION", result + "");

        return result;
    }

    private void requestStoragePermission() {

        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);

    }

    private void pickImageFromGallery() {
        //pick from gallery
        Intent gallery = new Intent(Intent.ACTION_PICK);
        gallery.setType("image/*");
        startActivityForResult(gallery, IMAGE_PICK_GALLERY_REQUEST_CODE);

    }

    private void pickImageFromCamera() {

        //Intent of picking image from device camera
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");

        //put image uri
        image_uri = this.getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //insert to start camera
        Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraintent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraintent, IMAGE_PICK_CAMERA_REQUEST_CODE);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case CAMERA_REQUEST_CODE: {

                // picking from camera
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStogeAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted && writeStogeAccepted) {

                        // camera permission anable
                        pickImageFromCamera();


                    } else {

                        Toast.makeText(this, "Please anable camera and storage permision !", Toast.LENGTH_SHORT).show();

                    }

                }

            }
            break;

            case STORAGE_REQUEST_CODE: {

                // picking from gallery
                if (grantResults.length > 0) {

                    boolean writeStogeAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (writeStogeAccepted) {

                        pickImageFromGallery();

                    } else {

                        Toast.makeText(this, "Please anable storage permision !", Toast.LENGTH_SHORT).show();

                    }

                }

            }

            break;

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK) {

            if (requestCode == IMAGE_PICK_GALLERY_REQUEST_CODE) {

                image_uri = data.getData();

                Controller.appLogDebug(Const.LOG_DAT, "PICK_IMAGE_CHAT " + image_uri.toString());

                if (image_uri != null) {

                    onSendImageMessage(image_uri);

                }

            }

            if (requestCode == IMAGE_PICK_CAMERA_REQUEST_CODE) {

                Controller.appLogDebug(Const.LOG_DAT, "PICK_IMAGE_CHAT " + image_uri.toString());

                onSendImageMessage(image_uri);

            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.img_send_text_chatgroup:

                String TextMessage = edt_enter_text_chatgroup.getText().toString().trim();

                if (TextMessage.isEmpty()) {

                    img_send_text_chatgroup.setClickable(false);
                    edt_enter_text_chatgroup.setFocusable(true);

                } else {

                    img_send_text_chatgroup.setClickable(true);
                    Controller.showProgressDialog(GroupChatActivity.this, "");
                    onSendMessageGroupChat(TextMessage);

                }

                break;

            case R.id.txt_option_participant:

                GroupParticipantDialog participantDialog = new GroupParticipantDialog();
                Bundle bundle = new Bundle();
                bundle.putString("groupID", GroupID);
                participantDialog.setArguments(bundle);
                participantDialog.show(getSupportFragmentManager(), "GroupParticipantDialog");

                break;

            case R.id.img_infor_groupchat:

                InformationGroupDialog informationGroupDialog = new InformationGroupDialog();
                Bundle bundle2 = new Bundle();
                bundle2.putString("groupID", GroupID);
                informationGroupDialog.setArguments(bundle2);
                informationGroupDialog.show(getSupportFragmentManager(), "InformationGroupDialog");


                break;


            case R.id.img_attachfile_chatgroup:

                String options[] = {getResources().getString(R.string.txt_camera)
                        , getResources().getString(R.string.txt_gallery)};

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("Pick Image From");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {

                            case 0:

                                if (!checkCameraPermission()) {

                                    requestCameraPermission();

                                } else {

                                    pickImageFromCamera();

                                }

                                break;
                            case 1:

                                if (!checkStoragePermission()) {

                                    requestStoragePermission();

                                } else {

                                    pickImageFromGallery();

                                }

                                break;

                        }

                    }
                });

                builder.create().show();

                break;

        }

    }
}
