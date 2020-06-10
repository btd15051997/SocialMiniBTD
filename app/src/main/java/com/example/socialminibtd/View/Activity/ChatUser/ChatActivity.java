package com.example.socialminibtd.View.Activity.ChatUser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.socialminibtd.Adapter.ChatAdapter;
import com.example.socialminibtd.Model.ListUser;
import com.example.socialminibtd.Model.ModelChat;
import com.example.socialminibtd.Notifications.ModelNotifi.Data;
import com.example.socialminibtd.Notifications.ModelNotifi.Sender;
import com.example.socialminibtd.Notifications.ModelNotifi.Token;
import com.example.socialminibtd.Notifications.Retrofit.APIService;
import com.example.socialminibtd.Notifications.Retrofit.Client;
import com.example.socialminibtd.R;
import com.example.socialminibtd.Utils.Const;
import com.example.socialminibtd.Utils.Controller;
import com.example.socialminibtd.View.Activity.Login.LoginActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ChatActivity extends AppCompatActivity implements IChatActivityView, View.OnClickListener {

    private static final int REQUEST_CALL = 1;
    private static final int REQUESTCODE_VOICE = 20;
    private ImageView img_send_text_chat, img_attachfile_chat;
    private CircularImageView img_user_chatting;
    private EditText edt_enter_text_chat;
    private RecyclerView recyc_content_chat;
    private TextView txt_name_chatting, txt_status_chatting, txt_call_phone, txt_voice_chat;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    private String hisUid;
    private String myUid;
    private String hisImage;
    private String phoneNumber;
    //permissions constants
    private static final int CAMERA_REQUEST_CODE = 101;
    private static final int STORAGE_REQUEST_CODE = 102;

    private static final int IMAGE_PICK_GALLERY_REQUEST_CODE = 103;
    private static final int IMAGE_PICK_CAMERA_REQUEST_CODE = 104;

    private String[] cameraPermissions;
    private String[] storagePermissions;
    private Uri image_uri = null;


    // for checking if use has seen message or not
    private ValueEventListener seenListener;
    private DatabaseReference userRefForSeen;

    private ArrayList<ModelChat> arrayListChat;
    private ChatAdapter chatAdapter;

    private APIService apiService;
    private boolean notify = false;

    //http volley

    private RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_bottom);
        setContentView(R.layout.activity_chat);

        onMappingView();

        if (getIntent() != null) {

            hisUid = getIntent().getStringExtra("hisUid");

            Controller.appLogDebug(Const.LOG_DAT, "get UID chat" + hisUid.toString());

            onFindUidUser(hisUid);

            onReadAndShowlistChat();

            //create api service
            //  apiService = Client.getRetrofit(Const.URL_FCM).create(APIService.class);

            onSeenMessagetChat();

        }

    }

    private void onVoiceSearch() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi Speak Something");

        try {

            startActivityForResult(intent, REQUESTCODE_VOICE);

        } catch (ActivityNotFoundException e) {

            Toast.makeText(this, "" + e.getMessage().toString(), Toast.LENGTH_SHORT).show();

        }

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.txt_call_phone:

                onMakeCallPhone();

                break;


            case R.id.txt_voice_chat:

                onVoiceSearch();

                break;

            case R.id.img_send_text_chat:

                notify = true;

                if (edt_enter_text_chat.getText().toString().isEmpty()) {

                    img_send_text_chat.setClickable(false);

                } else {

                    onSendMessageChat(edt_enter_text_chat.getText().toString());

                }

                edt_enter_text_chat.setText("");

                break;

            case R.id.img_attachfile_chat:

                onShowPickupDialog();

                break;
        }

    }

    private void onMakeCallPhone() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);

            Log.d("Check", "Check 1");

        } else {

            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.fromParts("tel", phoneNumber, null));
            startActivity(callIntent);

        }


    }

    @Override
    public void onMappingView() {

        img_send_text_chat = findViewById(R.id.img_send_text_chat);
        img_user_chatting = findViewById(R.id.img_user_chatting);
        edt_enter_text_chat = findViewById(R.id.edt_enter_text_chat);
        recyc_content_chat = findViewById(R.id.recyc_content_chat);
        txt_name_chatting = findViewById(R.id.txt_name_chatting);
        txt_status_chatting = findViewById(R.id.txt_status_chatting);
        img_attachfile_chat = findViewById(R.id.img_attachfile_chat);
        txt_call_phone = findViewById(R.id.txt_call_phone);
        txt_voice_chat = findViewById(R.id.txt_voice_chat);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        img_send_text_chat.setOnClickListener(this);
        img_attachfile_chat.setOnClickListener(this);
        txt_call_phone.setOnClickListener(this);
        txt_voice_chat.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null){

            startActivity(new Intent(ChatActivity.this, LoginActivity.class));
            finish();

        }else {

            mFirebaseUser = mAuth.getCurrentUser();
            myUid = mFirebaseUser.getUid();

        }

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("User");

        edt_enter_text_chat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().trim().length() == 0) {

                    // no typing so txt_status.setText(online or timestamp)
                    onCheckTypingTo("onOne");

                } else {

                    onCheckTypingTo(hisUid);

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    @Override
    public void onFindUidUser(String uid) {

        //query with Uid Current
        Query query = reference.orderByChild("uid").equalTo(uid);
        //get image and name
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    String name = String.valueOf(dataSnapshot1.child("name").getValue());
                    hisImage = String.valueOf(dataSnapshot1.child("image").getValue());
                    phoneNumber = String.valueOf(dataSnapshot1.child("phone").getValue());

                    Log.d("Check", phoneNumber);
                    //get status
                    String onlineStatus = "" + dataSnapshot1.child(Const.Params.ONLINE_STATUS).getValue();
                    String TypingTo = "" + dataSnapshot1.child(Const.Params.TYPING_TO).getValue();

                    if (TypingTo.equals(myUid)) {

                        txt_status_chatting.setText(getResources().getString(R.string.txt_typing));

                    } else {

                        if (onlineStatus.equals(Const.Params.ONLINE)) {

                            txt_status_chatting.setText(onlineStatus);

                        }
                        if (!onlineStatus.equals(Const.Params.ONLINE)) {

                            txt_status_chatting.setText(Controller.convertDateTime(onlineStatus));

                        }
                    }

                    txt_name_chatting.setText(name);

                    try {

                        Picasso.get().load(hisImage).into(img_user_chatting);

                    } catch (Exception e) {

                        Picasso.get().load(R.drawable.ic_account).into(img_user_chatting);

                        Controller.appLogDebug(Const.LOG_DAT, "get Name chat : " + e.toString());
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onSendMessageChat(final String message) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("sender", myUid);
        hashMap.put("receiver", hisUid);
        hashMap.put("message", message);
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
        hashMap.put("isseen", false);
        hashMap.put("type", "text");

        databaseReference.child("Chats").push().setValue(hashMap);

        Controller.appLogDebug(Const.LOG_DAT, "Send MessageChat :" + hashMap.toString());

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("User").child(myUid);
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ListUser user = dataSnapshot.getValue(ListUser.class);

                if (notify) {

                    sendNotification(hisUid, user.getName(), message);

                } else {


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });

        //create node chatlist on database
        final DatabaseReference Chatreference1 = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(myUid)
                .child(hisUid);
        Chatreference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {

                    Chatreference1.child("id").setValue(hisUid);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        final DatabaseReference Chatreference2 = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(hisUid)
                .child(myUid);
        Chatreference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {

                    Chatreference2.child("id").setValue(myUid);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public void onSendImageMessage(Uri image_uri) {

        notify = true;
        Controller.showProgressDialog(ChatActivity.this, "Sending image... ");

        final String timeStamp = String.valueOf(System.currentTimeMillis());
        String fileNameAndPath = "ChatImage/" + "post_" + timeStamp;


        //get bitmap from image uri
        try {

            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image_uri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            final byte[] data = baos.toByteArray();//convert image to bytes

            StorageReference ref = FirebaseStorage.getInstance().getReference().child(fileNameAndPath);
            ref.putBytes(data)
                    .addOnSuccessListener(taskSnapshot -> {

                        //image uploaded

                        //get url image
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                        while (!uriTask.isSuccessful()) ;

                        String downloadUri = uriTask.getResult().toString();

                        if (uriTask.isSuccessful()) {
                            //add image and other info to database

                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                            //set up required data
                            HashMap<String, Object> hashMap = new HashMap<>();

                            hashMap.put("sender", myUid);
                            hashMap.put("receiver", hisUid);
                            hashMap.put("message", downloadUri);
                            hashMap.put("timestamp", timeStamp);
                            hashMap.put("type", "image");
                            hashMap.put("isseen", false);

                            //put data to firebase

                            databaseReference.child("Chats").push().setValue(hashMap);

                            //send notification
                            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("User").child(myUid);
                            databaseReference1.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    ListUser user = dataSnapshot.getValue(ListUser.class);
                                    Controller.dimissProgressDialog();

                                    if (notify) {

                                        sendNotification(hisUid, user.getName(), "Send you a photo");

                                    }

                                    notify = false;

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                    Controller.dimissProgressDialog();
                                    Controller.appLogDebug(Const.LOG_DAT, "send image to chat :" + databaseError.getMessage());

                                }
                            });

                            //create node chatlist on database
                            final DatabaseReference Chatreference1 = FirebaseDatabase.getInstance().getReference("Chatlist")
                                    .child(myUid)
                                    .child(hisUid);
                            Chatreference1.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if (!dataSnapshot.exists()) {

                                        Chatreference1.child("id").setValue(hisUid);

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                            final DatabaseReference Chatreference2 = FirebaseDatabase.getInstance().getReference("Chatlist")
                                    .child(hisUid)
                                    .child(myUid);
                            Chatreference2.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if (!dataSnapshot.exists()) {

                                        Chatreference2.child("id").setValue(myUid);

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }


                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Controller.dimissProgressDialog();
                    Controller.showLongToast(e.toString(), ChatActivity.this);

                }
            });


        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onReadAndShowlistChat() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        recyc_content_chat.setHasFixedSize(true);
        recyc_content_chat.setLayoutManager(linearLayoutManager);

        arrayListChat = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (arrayListChat != null) {

                    arrayListChat.clear();
                }

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    ModelChat modelChat = dataSnapshot1.getValue(ModelChat.class);

                    if (modelChat.getSender().equals(myUid) && modelChat.getReceiver().equals(hisUid)
                            || modelChat.getSender().equals(hisUid) && modelChat.getReceiver().equals(myUid)) {

                        Controller.appLogDebug(Const.LOG_DAT, "Uid Nguoi Gui :" + modelChat.getSender()
                                + "     " + "Uid Nguoi Nhan :" + modelChat.getReceiver());

                        arrayListChat.add(modelChat);

                    }
                }

                chatAdapter = new ChatAdapter(ChatActivity.this, arrayListChat, hisImage);

                Controller.appLogDebug(Const.LOG_DAT, "Arraylist Chat :" + arrayListChat.toString());

                recyc_content_chat.setAdapter(chatAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onSeenMessagetChat() {

        userRefForSeen = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = userRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    ModelChat chat = dataSnapshot1.getValue(ModelChat.class);

                    if (chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUid)) {

                        HashMap<String, Object> hashMapSeen = new HashMap<>();
                        hashMapSeen.put("isseen", true);

                        dataSnapshot1.getRef().updateChildren(hashMapSeen);

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onShowPickupDialog() {

        String options[] = {getResources().getString(R.string.txt_camera)
                , getResources().getString(R.string.txt_gallery)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Pick Image From");

        builder.setItems(options, (dialog, which) -> {

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

        });

        builder.create().show();

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

            case REQUEST_CALL:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    onMakeCallPhone();

                } else {

                    Toast.makeText(this, "Cannot call!", Toast.LENGTH_SHORT).show();

                }

                break;

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

        // Crop image
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                Uri uri_crop = result.getUri();

                // upload image to server
                onSendImageMessage(uri_crop);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

                Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }


        if (resultCode == RESULT_OK) {


            if (requestCode == REQUESTCODE_VOICE) {

                ArrayList<String> stringArrayList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                Log.d("Voice_search", stringArrayList.size() + "  ");

                edt_enter_text_chat.setText(stringArrayList.get(0));
            }

            if (requestCode == IMAGE_PICK_GALLERY_REQUEST_CODE) {

                image_uri = data.getData();

                Controller.appLogDebug(Const.LOG_DAT, "PICK_IMAGE_CHAT " + image_uri.toString());

                // performCrop(image_uri);

                if (image_uri != null) {

                    CropImage.activity(image_uri)
                            .start(this);

                }

            }

            if (requestCode == IMAGE_PICK_CAMERA_REQUEST_CODE) {

                Controller.appLogDebug(Const.LOG_DAT, "PICK_IMAGE_CHAT " + image_uri.toString());

                if (image_uri != null) {

                    CropImage.activity(image_uri)
                            .start(this);

                }

            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCheckOnlineStatus(String status) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User").child(myUid);

        HashMap<String, Object> hashMapStatus = new HashMap<>();
        hashMapStatus.put(Const.Params.ONLINE_STATUS, status);

        Controller.appLogDebug(Const.LOG_DAT, "CheckOnlineStatus :" + hashMapStatus.toString());

        //update status online offline
        reference.updateChildren(hashMapStatus);

    }

    @Override
    public void onCheckTypingTo(String typingTo) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User").child(myUid);

        HashMap<String, Object> hashMapStatus = new HashMap<>();
        hashMapStatus.put(Const.Params.TYPING_TO, typingTo);

        Controller.appLogDebug(Const.LOG_DAT, "CheckTypingTo :" + hashMapStatus.toString());

        //update status online offline
        reference.updateChildren(hashMapStatus);

    }


    @Override
    public void sendNotification(final String hisUid, final String nameSender, final String message) {

        DatabaseReference databaseReferenceAllToken = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = databaseReferenceAllToken.orderByKey().equalTo(hisUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Token token = ds.getValue(Token.class);

                    Data dataMessage = new Data(myUid, nameSender + ":" + message
                            , "New Message"
                            , hisUid
                            , "ChatNotification"
                            , R.drawable.ic_launcher_background);

                    Controller.appLogDebug(Const.LOG_DAT, " SenddataMessage : " + myUid + "   " + nameSender + "  : "
                            + message + "  New Message   " + hisUid + " Token :" + token.getToken());

                    Sender sender = new Sender(dataMessage, token.getToken());

                    try {

                        JSONObject jrqsonObject = new JSONObject(new Gson().toJson(sender));

                        String urlNotification = "https://fcm.googleapis.com/fcm/send";

                        JsonObjectRequest objectRequest = new JsonObjectRequest(urlNotification, jrqsonObject, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                Controller.appLogDebug(Const.LOG_DAT, " getDataMessage : " + response.toString());

                            }
                        }, error ->
                                Controller.appLogDebug(Const.LOG_DAT, " VolleyError : " + error.toString())) {

                            @Override
                            public Map<String, String> getHeaders() {
                                // put params
                                HashMap<String, String> header = new HashMap<>();

                                //volley exxist content_type
                                //  header.put("Content-Type", "application/json");
                                header.put("Authorization"
                                        , "key=AAAAls0H_M0:APA91bHQOVAR0jvzuQusEg4KV7tm8xc5v45SD_nh50pLqn3LLJMajLa9AUw4vM4MOrn--nF8qO7HEKODbAVMnZbucDVRGi-UlRgRg0DB_Z24yKYGW2q1P72nt_ic2BHswpPn3_lPdmYD");

                                return header;
                            }
                        };

                        requestQueue.add(objectRequest);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

//                    apiService.sendNotification(sender).enqueue(new Callback<Response>() {
//                        @Override
//                        public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
//
//                            Controller.appLogDebug(Const.LOG_DAT, " getDataMessage : " + response.body() + " : " + response.toString());
//
//                        }
//
//                        @Override
//                        public void onFailure(Call<Response> call, Throwable t) {
//
//
//                        }
//                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });

    }

//    private void performCrop(Uri picUri) {
//        try {
//            Intent cropIntent = new Intent("com.android.camera.action.CROP");
//            // indicate image type and Uri
//            cropIntent.setDataAndType(picUri, "image/*");
//            // set crop properties here
//            cropIntent.putExtra("crop", true);
//            // indicate aspect of desired crop
//            cropIntent.putExtra("aspectX", 1);
//            cropIntent.putExtra("aspectY", 1);
//            // indicate output X and Y
//            cropIntent.putExtra("outputX", 128);
//            cropIntent.putExtra("outputY", 128);
//            // retrieve data on return
//            cropIntent.putExtra("return-data", true);
//            // start the activity - we handle returning in onActivityResult
//            startActivityForResult(cropIntent, PIC_CROP);
//        }
//        // respond to users whose devices do not support the crop action
//        catch (ActivityNotFoundException anfe) {
//            // display an error message
//            String errorMessage = "Whoops - your device doesn't support the crop action!";
//            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
//            toast.show();
//        }
//    }

    @Override
    protected void onStart() {
        super.onStart();

        onCheckOnlineStatus(Const.Params.ONLINE);

    }

    @Override
    protected void onResume() {
        super.onResume();

        onCheckOnlineStatus(Const.Params.ONLINE);
    }

    @Override
    protected void onPause() {
        super.onPause();

        userRefForSeen.removeEventListener(seenListener);

        onCheckOnlineStatus(String.valueOf(System.currentTimeMillis()));

        onCheckTypingTo("onOne");

    }
}
