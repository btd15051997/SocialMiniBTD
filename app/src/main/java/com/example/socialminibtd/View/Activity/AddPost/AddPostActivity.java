package com.example.socialminibtd.View.Activity.AddPost;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.socialminibtd.R;
import com.example.socialminibtd.Utils.Const;
import com.example.socialminibtd.Utils.Controller;
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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddPostActivity extends AppCompatActivity implements IAddPostActivityView, View.OnClickListener {

    private TextView txt_up_header_addpost, txt_name_addpost, txt_up_addpost;
    private ImageView img_content_addpost, img_back_addpost;
    private LinearLayout linear_click_content_addpost;
    private CircularImageView img_user_addpost;
    private EditText edt_title_addpost, edt_description_addpost;

    private FirebaseAuth mAuth;
    private DatabaseReference mReference;


    private Uri image_uri;
    private Uri uri_image_hadcrop;

    //permissions constants
    private static final int CAMERA_REQUEST_CODE = 101;
    private static final int STORAGE_REQUEST_CODE = 102;

    private static final int IMAGE_PICK_GALLERY_REQUEST_CODE = 103;
    private static final int IMAGE_PICK_CAMERA_REQUEST_CODE = 104;

    private String[] cameraPermissions;
    private String[] storagePermissions;


    //user info
    private String name, email, uid, dp;
    private String editTitle, editDescription, editImage;
    private String isUpdateKey, editPostId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_bottom);
        setContentView(R.layout.activity_add_post);

        Intent intent = getIntent();

        onMappingView();

        onInitFirebase();

        // check intent when Update post
        if (intent.getStringExtra("key") != null) {

            if (intent.getStringExtra("key").equals("editPost")) {

                isUpdateKey = intent.getStringExtra("key");
                editPostId = intent.getStringExtra("editPostId");

                Controller.appLogDebug(Const.LOG_DAT, " key UpdatePost " + editPostId + "  " + isUpdateKey);

                onLoadPostData(editPostId);

            }

        }

    }


    @Override
    public void onMappingView() {

        txt_up_header_addpost = findViewById(R.id.txt_up_header_addpost);
        txt_name_addpost = findViewById(R.id.txt_name_addpost);
        txt_up_addpost = findViewById(R.id.txt_up_addpost);
        img_content_addpost = findViewById(R.id.img_content_addpost);
        img_back_addpost = findViewById(R.id.img_back_addpost);
        linear_click_content_addpost = findViewById(R.id.linear_click_content_addpost);

        img_user_addpost = findViewById(R.id.img_user_addpost);
        edt_title_addpost = findViewById(R.id.edt_title_addpost);
        edt_description_addpost = findViewById(R.id.edt_description_addpost);

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        img_back_addpost.setOnClickListener(this);
        txt_up_header_addpost.setOnClickListener(this);
        linear_click_content_addpost.setOnClickListener(this);
        txt_up_addpost.setOnClickListener(this);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.linear_click_content_addpost:

                onShowPickupDialog();

                break;

            case R.id.img_back_addpost:

                onBackPressed();

                break;

            case R.id.txt_up_addpost:
            case R.id.txt_up_header_addpost:

                if (edt_title_addpost.getText().toString().trim().isEmpty()) {

                    Controller.showLongToast(this.getResources().getString(R.string.txt_title_error), this);

                    return;

                }

                if (edt_description_addpost.getText().toString().trim().isEmpty()) {

                    Controller.showLongToast(this.getResources().getString(R.string.txt_description_error), this);

                    return;

                }

                // update, edit post
                if (getIntent().getStringExtra("key") != null) {

                    if (isUpdateKey.equals("editPost")) {

                        //Edit post with image or no image
                        onBeginUpdatePost(edt_title_addpost.getText().toString().trim()
                                , edt_description_addpost.getText().toString(), editPostId);
                    }

                } else {


                    //Post with image or no image
                    onUploadDataPost(edt_title_addpost.getText().toString().trim()
                            , edt_description_addpost.getText().toString());

                }


                break;

        }


    }

    @Override
    public void onInitFirebase() {

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        email = user.getEmail();
        uid = user.getUid();

        // get some info user of current user to include in post

        mReference = FirebaseDatabase.getInstance().getReference("User");

        Query query = mReference.orderByChild("email").equalTo(email);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    name = "" + ds.child("name").getValue();
                    email = "" + ds.child("email").getValue();
                    dp = "" + ds.child("image").getValue();

                    Controller.appLogDebug(Const.LOG_DAT + " Image User ", dp.toString());

                    try {

                        Picasso.get().load(String.valueOf(ds.child("image").getValue()))
                                .placeholder(R.drawable.ic_account).into(img_user_addpost);

                    } catch (Exception e) {

                        Picasso.get().load(R.drawable.ic_account).into(img_user_addpost);

                    }


                    txt_name_addpost.setText(name);

                    Controller.appLogDebug(Const.LOG_DAT, " getInfoUser AddPost " + name + email + dp);

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


    }

    @Override
    public void onGetImageName() {

    }

    @Override
    public void onLoadPostData(String editPostId) {

        txt_up_header_addpost.setText("Update Post");
        txt_up_addpost.setText("Update");

        Controller.showProgressDialog(AddPostActivity.this, ""
                + this.getResources().getString(R.string.txt_loading));

        DatabaseReference RefUpdate = FirebaseDatabase.getInstance().getReference("Posts");

        //get detail post using id of post
        Query query = RefUpdate.orderByChild("pIDTime").equalTo(editPostId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Controller.dimissProgressDialog();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    editTitle = String.valueOf(ds.child("pTitle").getValue());
                    editDescription = String.valueOf(ds.child("pDescription").getValue());
                    editImage = String.valueOf(ds.child("pImage").getValue());

                    Controller.appLogDebug(Const.LOG_DAT, "onUpdatePost :" + editTitle);

                    edt_title_addpost.setText(editTitle);
                    edt_description_addpost.setText(editDescription);

                    if (editImage.equals("noImage")) {

                        if (img_content_addpost.getVisibility() == View.VISIBLE) {

                            img_content_addpost.setVisibility(View.GONE);

                        }

                    } else {

                        if (img_content_addpost.getVisibility() == View.GONE) {

                            img_content_addpost.setVisibility(View.VISIBLE);

                        }

                        try {

                            Picasso.get().load(editImage).placeholder(R.drawable.ic_account).into(img_content_addpost);

                        } catch (Exception e) {

                            Picasso.get().load(R.drawable.ic_account).into(img_content_addpost);

                            Controller.appLogDebug(Const.LOG_DAT, " onUpdatePost :" + e.toString());

                        }


                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Controller.dimissProgressDialog();
                Controller.appLogDebug(Const.LOG_DAT, "onUpdatePost :" + databaseError.getMessage().toString());
                Toast.makeText(AddPostActivity.this, "" + databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();

            }
        });


    }

    @Override
    public void onPrepareNotificaion(String pId, String title, String dscription, String notificationType, String notificationTopic) {

        //prepare data for notification
        String NOTIFICATION_TOPIC = "/topics/" + notificationTopic; // topic must match with what the receive subscribed
        String NOTIFICATION_TITLE = title; // ex: btd add new post
        String NOTIFICATION_MESSAGE = dscription; // ex: btd add new post
        String NOTIFICATION_TYPE = notificationType; // ex: btd add new post

        //prepare json what to send, and where to send
        JSONObject notificationJo = new JSONObject();
        JSONObject notificationBodyJo = new JSONObject();

        try {

            notificationBodyJo.put("notificationType", NOTIFICATION_TYPE);
            notificationBodyJo.put("sender", uid); // uid of current user/sender
            notificationBodyJo.put("pId", pId); // post ID
            notificationBodyJo.put("pTitle", NOTIFICATION_TITLE);
            notificationBodyJo.put("pDescription", NOTIFICATION_MESSAGE);

            //where to send
            notificationJo.put("to", NOTIFICATION_TOPIC);
            notificationJo.put("data", notificationBodyJo);

            Controller.appLogDebug(Const.LOG_DAT, " onSendPostNotification :" + notificationJo.toString());

        } catch (JSONException e) {
            e.printStackTrace();

            Controller.showLongToast(e.toString(), AddPostActivity.this);

        }

        //send post
        onSendPostNotification(notificationJo);

    }


    @Override
    public void onSendPostNotification(JSONObject notificationJo) {

        //send volley object request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationJo
                , response -> Controller.appLogDebug(Const.LOG_DAT, "onSendPostNotification reponse :" + response.toString())
                , error -> Controller.appLogDebug(Const.LOG_DAT, "onSendPostNotification error :" + error.toString())) {

            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> header = new HashMap<>();
                header.put("Authorization"
                        , "key=AAAAls0H_M0:APA91bHQOVAR0jvzuQusEg4KV7tm8xc5v45SD_nh50pLqn3LLJMajLa9AUw4vM4MOrn--nF8qO7HEKODbAVMnZbucDVRGi-UlRgRg0DB_Z24yKYGW2q1P72nt_ic2BHswpPn3_lPdmYD");

                return header;
            }
        };

        Volley.newRequestQueue(getApplicationContext()).add(jsonObjectRequest);

    }

    @Override
    public void onUploadDataPost(final String title, final String description) {

        Controller.showProgressDialog(AddPostActivity.this, "Publishing post ...");

        // set child on database storage
        final String timeStamp = String.valueOf(System.currentTimeMillis());
        String filePathName = "Posts/" + "post_" + timeStamp;

        if (img_content_addpost.getDrawable() != null) {

            byte[] data = onImageToArrayByte();

            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(filePathName);

            storageReference.putBytes(data).addOnSuccessListener(taskSnapshot -> {

                //image is uploaded to firebase storage, now get it is url

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                while (!uriTask.isSuccessful()) ;

                String downloadUri = uriTask.getResult().toString();

                if (uriTask.isSuccessful()) {

                    // url received upload post to firebase database
                    HashMap<Object, String> hashMap = new HashMap<>();
                    hashMap.put("uid", uid);
                    hashMap.put("uName", name);
                    hashMap.put("uEmail", email);
                    hashMap.put("uDp", dp);

                    hashMap.put("pIDTime", timeStamp);
                    hashMap.put("pTitle", title);
                    hashMap.put("pLikes", "0");
                    hashMap.put("pComments", "0");
                    hashMap.put("pDescription", description);
                    hashMap.put("pImage", downloadUri);
                    hashMap.put("pTime", timeStamp);

                    Controller.appLogDebug(Const.LOG_DAT + " Post hashmap", "" + hashMap);

                    //path to store post data
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                    // put data in this ref
                    ref.child(timeStamp).setValue(hashMap).addOnSuccessListener(aVoid -> {

                        Controller.dimissProgressDialog();
                        Controller.showLongToast("Post published", AddPostActivity.this);
                        onResetTextImage();

                        onPrepareNotificaion("" + timeStamp
                                , "" + name + " added new post "
                                , "" + title + "\n" + description
                                , "PostNotification"
                                , "POST");

                        onBackPressed();
                        finish();

                    }).addOnFailureListener(e -> {

                        Controller.dimissProgressDialog();
                        Controller.showLongToast("Upload form post Failed" + e.toString(), AddPostActivity.this);
                        Controller.appLogDebug(Const.LOG_DAT + " Upload form post Failed", "" + e.toString());


                    });


                }

            }).addOnFailureListener(e -> {

                Controller.dimissProgressDialog();
                Controller.showLongToast("Upload Image Failed" + e.toString(), AddPostActivity.this);
                Controller.appLogDebug(Const.LOG_DAT, "" + e.toString());

            });

        } else {
            // url received upload post to firebase database
            HashMap<Object, String> hashMap = new HashMap<>();
            hashMap.put("uid", uid);
            hashMap.put("uName", name);
            hashMap.put("uEmail", email);
            hashMap.put("uDp", dp);

            hashMap.put("pIDTime", timeStamp);
            hashMap.put("pTitle", title);
            hashMap.put("pLikes", "0");
            hashMap.put("pComments", "0");
            hashMap.put("pDescription", description);
            hashMap.put("pImage", "noImage");
            hashMap.put("pTime", timeStamp);

            Controller.appLogDebug(Const.LOG_DAT + " Post hashmap", "" + hashMap);

            //path to store post data
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
            // put data in this ref
            ref.child(timeStamp).setValue(hashMap).addOnSuccessListener(aVoid -> {

                Controller.dimissProgressDialog();
                Controller.showLongToast("Post published", AddPostActivity.this);

                onResetTextImage();
                onPrepareNotificaion("" + timeStamp
                        , "" + name + " added new post "
                        , "" + title + "\n" + description
                        , "PostNotification"
                        , "POST");

                onBackPressed();
                finish();


            }).addOnFailureListener(e -> {

                Controller.dimissProgressDialog();
                Controller.showLongToast("Upload form post Failed" + e.toString(), AddPostActivity.this);
                Controller.appLogDebug(Const.LOG_DAT + " Upload form post Failed", "" + e.toString());


            });


        }

    }

    @Override
    public void onBeginUpdatePost(String title, String description, String editPostId) {

        Controller.showProgressDialog(AddPostActivity.this, "Updating Post");

        if (!editImage.equals("noImage")) {

            // update with old image replace by new image
            onUpdateWasWithImage(title, description, editPostId);
            Log.d(Const.LOG_DAT, " Update with old image replace by new image");


        } else if (img_content_addpost.getDrawable() != null) {

            //update with null old image with new image
            onUpdateWithNowImage(title, description, editPostId);
            Log.d(Const.LOG_DAT, "Update with null old image with new image");

        } else {

            //update without image
            onUpdateWithoutImage(title, description, editPostId);
            Log.d(Const.LOG_DAT, " Update without image");


        }


    }

    @Override
    public void onUpdateWasWithImage(final String title, final String decription, final String editPostId) {

        //first delete image and post is wwith image

        StorageReference RefStorage = FirebaseStorage.getInstance().getReferenceFromUrl(editImage);

        RefStorage.delete()
                .addOnSuccessListener(aVoid -> {

                    //image deleted, upload new image
                    //for post- image   name,post_id,publish_time

                    Controller.appLogDebug(Const.LOG_DAT, " Deleted Image ");

                    final String timeStamp = String.valueOf(System.currentTimeMillis());

                    String filePathandName = "Posts/" + "post_" + timeStamp;

                    byte[] data = onImageToArrayByte();

                    final StorageReference Ref2 = FirebaseStorage.getInstance().getReference().child(filePathandName);

                    Ref2.putBytes(data)
                            .addOnSuccessListener(taskSnapshot -> {

                                Controller.appLogDebug(Const.LOG_DAT, " Update new Image ");

                                //get url from image uploaded

                                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                                while (!uriTask.isSuccessful()) ;

                                String downLoadUrl = uriTask.getResult().toString();

                                Controller.appLogDebug(Const.LOG_DAT, " URL new Image :" + downLoadUrl);

                                if (uriTask.isSuccessful() || !downLoadUrl.isEmpty()) {

                                    //update to firebase database
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("uid", uid);
                                    hashMap.put("uName", name);
                                    hashMap.put("uEmail", email);
                                    hashMap.put("uDp", dp);

                                    hashMap.put("pTitle", title);
                                    hashMap.put("pDescription", decription);
                                    hashMap.put("pImage", downLoadUrl);


                                    Controller.appLogDebug(Const.LOG_DAT + "", " Update to firebase : " + hashMap.toString());

                                    DatabaseReference Refbase = FirebaseDatabase.getInstance().getReference("Posts");

                                    Refbase.child(editPostId)
                                            .updateChildren(hashMap)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid1) {

                                                    Controller.dimissProgressDialog();
                                                    Controller.appLogDebug(Const.LOG_DAT + "", " Update to firebase : Success");
                                                    Toast.makeText(AddPostActivity.this, "Updated Success", Toast.LENGTH_SHORT).show();


                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            Controller.dimissProgressDialog();
                                            Controller.appLogDebug(Const.LOG_DAT + "", " Update to firebase : fasle" + e.toString());

                                        }
                                    });

                                }

                            }).addOnFailureListener(e -> {

                        Controller.dimissProgressDialog();
                        Controller.appLogDebug(Const.LOG_DAT + "", "onUpdatePostWithImage : " + e.toString());

                    });


                }).addOnFailureListener(e -> {

            Controller.dimissProgressDialog();
            Controller.appLogDebug(Const.LOG_DAT + "", "onUpdatePostWithImage : " + e.toString());

        });

    }

    @Override
    public void onUpdateWithNowImage(final String title, final String decription, final String editPostId) {

        final String timeStamp = String.valueOf(System.currentTimeMillis());

        String filePathandName = "Posts/" + "post_" + timeStamp;

        byte[] data = onImageToArrayByte();

        final StorageReference Ref2 = FirebaseStorage.getInstance().getReference().child(filePathandName);

        Ref2.putBytes(data)
                .addOnSuccessListener(taskSnapshot -> {

                    Controller.appLogDebug(Const.LOG_DAT, " Update new Image ");

                    //get url from image uploaded

                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                    while (!uriTask.isSuccessful()) ;

                    String downLoadUrl = uriTask.getResult().toString();

                    Controller.appLogDebug(Const.LOG_DAT, " URL new Image :" + downLoadUrl);

                    if (uriTask.isSuccessful() || !downLoadUrl.isEmpty()) {

                        //update to firebase database
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("uid", uid);
                        hashMap.put("uName", name);
                        hashMap.put("uEmail", email);
                        hashMap.put("uDp", dp);

                        hashMap.put("pTitle", title);
                        hashMap.put("pDescription", decription);
                        hashMap.put("pImage", downLoadUrl);


                        Controller.appLogDebug(Const.LOG_DAT + "", " Update to firebase : " + hashMap.toString());

                        DatabaseReference Refbase = FirebaseDatabase.getInstance().getReference("Posts");

                        Refbase.child(editPostId)
                                .updateChildren(hashMap)
                                .addOnSuccessListener(aVoid -> {

                                    Controller.dimissProgressDialog();
                                    Controller.appLogDebug(Const.LOG_DAT + "", " Update to firebase : Success");
                                    Toast.makeText(AddPostActivity.this, "Updated Success", Toast.LENGTH_SHORT).show();


                                }).addOnFailureListener(e -> {

                            Controller.dimissProgressDialog();
                            Controller.appLogDebug(Const.LOG_DAT + "", " Update to firebase : fasle" + e.toString());

                        });

                    }

                }).addOnFailureListener(e -> {

            Controller.dimissProgressDialog();
            Controller.appLogDebug(Const.LOG_DAT + "", "onUpdatePostWithImage : " + e.toString());

        });


    }


    @Override
    public void onUpdateWithoutImage(String title, String description, String editPostId) {

        //update to firebase database
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("uName", name);
        hashMap.put("uEmail", email);
        hashMap.put("uDp", dp);

        hashMap.put("pTitle", title);
        hashMap.put("pDescription", description);
        hashMap.put("pImage", "noImage");


        Controller.appLogDebug(Const.LOG_DAT + "", " Update to firebase : " + hashMap.toString());

        DatabaseReference Refbase = FirebaseDatabase.getInstance().getReference("Posts");

        Refbase.child(editPostId)
                .updateChildren(hashMap)
                .addOnSuccessListener(aVoid -> {

                    Controller.dimissProgressDialog();
                    Controller.appLogDebug(Const.LOG_DAT + "", " Update to firebase : Success");
                    Toast.makeText(AddPostActivity.this, "Updated Success", Toast.LENGTH_SHORT).show();


                }).addOnFailureListener(e -> {

            Controller.dimissProgressDialog();
            Controller.appLogDebug(Const.LOG_DAT + "", " Update to firebase : fasle" + e.toString());

        });

    }

    @Override
    public String onGetTimeCurrent() {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("EEEE dd-MMM-yyyy hh:mm:ss a");
        String datetime = dateformat.format(c.getTime());

        return datetime;
    }

    @Override
    public void onResetTextImage() {

        edt_title_addpost.setText(null);
        edt_description_addpost.setText(null);
        uri_image_hadcrop = null;
        img_content_addpost.setVisibility(View.GONE);

    }


    @Override
    public byte[] onImageToArrayByte() {

        //get image from imageview
        Bitmap bitmap = ((BitmapDrawable) img_content_addpost.getDrawable()).getBitmap();

/*        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        // image compress
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);*/

        byte[] data = Controller.OncompressImage(bitmap).toByteArray();

        return data;
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

        // Crop image
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                uri_image_hadcrop = result.getUri();

                if (uri_image_hadcrop != null) {

                    img_content_addpost.setVisibility(View.VISIBLE);
                    img_content_addpost.setImageURI(uri_image_hadcrop);

                }
                // upload image to server

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

                Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }

        if (resultCode == RESULT_OK) {

            if (requestCode == IMAGE_PICK_GALLERY_REQUEST_CODE) {

                image_uri = data.getData();

                Controller.appLogDebug("PICK_IMAGE", image_uri.toString());

                if (image_uri != null) {

                    CropImage.activity(image_uri)
                            .start(this);


                }

            }

            if (requestCode == IMAGE_PICK_CAMERA_REQUEST_CODE) {

                Controller.appLogDebug("PICK_IMAGE", image_uri.toString());

                CropImage.activity(image_uri)
                        .start(this);


            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


}
