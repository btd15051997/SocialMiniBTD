package com.example.socialminibtd.View.Dialog.GroupCreate;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.socialminibtd.R;
import com.example.socialminibtd.Utils.Controller;
import com.example.socialminibtd.View.Activity.Dashbroad.DashboardActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;


public class GroupCreateDialog extends DialogFragment implements IGroupCreateDialogView {

    //permissions constants
    private static final int CAMERA_REQUEST_CODE = 101;
    private static final int STORAGE_REQUEST_CODE = 102;

    private static final int IMAGE_PICK_GALLERY_REQUEST_CODE = 103;
    private static final int IMAGE_PICK_CAMERA_REQUEST_CODE = 104;

    private String[] cameraPermissions;
    private String[] storagePermissions;
    private Uri image_uri;

    //view
    private CircularImageView img_avt_creategroup;
    private EditText edt_title_creategroup, edt_descrip_creategroup;
    private FloatingActionButton float_creategroup;

    private Dialog dialog_group;
    private DashboardActivity dashboardActivity;
    private FirebaseAuth auth;


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

        dialog_group.setContentView(R.layout.dialog_group_create);

        onMappingViewDialog();

        return dialog_group;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onMappingViewDialog() {

        img_avt_creategroup = dialog_group.findViewById(R.id.img_avt_creategroup);
        edt_title_creategroup = dialog_group.findViewById(R.id.edt_title_creategroup);
        float_creategroup = dialog_group.findViewById(R.id.float_creategroup);
        edt_descrip_creategroup = dialog_group.findViewById(R.id.edt_descrip_creategroup);

        float_creategroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onStartCreateGroup();

            }
        });

        img_avt_creategroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onShowPickupDialog();

            }
        });

    }

    @Override
    public void onShowPickupDialog() {

        String options[] = {getResources().getString(R.string.txt_camera)
                , getResources().getString(R.string.txt_gallery)};

        AlertDialog.Builder builder = new AlertDialog.Builder(dashboardActivity);

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
    public void onStartCreateGroup() {

        final String title = edt_title_creategroup.getText().toString().trim();
        final String description = edt_descrip_creategroup.getText().toString().trim();
        final String timeStamp = String.valueOf(System.currentTimeMillis());

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description)) {

            Controller.showLongToast("The fields cannot be empty!", dashboardActivity);

            return;

        }

        if (image_uri != null) {
            //creating group with image

            Controller.showProgressDialog(dashboardActivity, "Creating Group");
            String filePathAndName = "Group_Image/" + "image" + timeStamp;

            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                            while (!uriTask.isSuccessful()) ;

                            Uri uri = uriTask.getResult();

                            if (uriTask.isSuccessful()) {

                                onCreateGroup(title, description, timeStamp, String.valueOf(uri));

                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Controller.dimissProgressDialog();
                    Controller.showLongToast("" + e.toString(), dashboardActivity);

                }
            });


        } else {
            //creating group without image

            Controller.showProgressDialog(dashboardActivity, "Creating Group");
            onCreateGroup(title, description, timeStamp, "");

        }


    }

    @Override
    public void onResetTextAndImage() {

        edt_title_creategroup.setText(null);
        edt_descrip_creategroup.setText(null);
        img_avt_creategroup.setImageResource(R.drawable.ic_group_add);
    }

    @Override
    public void onCreateGroup(String title, String descrip, final String timeCreate, String iconGroup) {

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("groupId", timeCreate);
        hashMap.put("groupTitle", title);
        hashMap.put("groupDescription", descrip);
        hashMap.put("groupIcon", iconGroup);
        hashMap.put("timeStamp", timeCreate);
        hashMap.put("createBy", auth.getCurrentUser().getUid());

        //create group to database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(timeCreate).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        //set up member inf(add current user in group' participants list)
                        HashMap<String, String> hashMap1 = new HashMap<>();
                        hashMap1.put("uid", auth.getCurrentUser().getUid());
                        hashMap1.put("role", "creator");
                        hashMap1.put("timeStamp", timeCreate);


                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Groups");
                        reference1.child(timeCreate).child("Participants").child(auth.getCurrentUser().getUid())
                                .setValue(hashMap1).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                //Participants added
                                Controller.dimissProgressDialog();
                                Controller.showLongToast("Groups created...", dashboardActivity);
                                onResetTextAndImage();

                                if (getDialog().isShowing()) {

                                    getDialog().dismiss();

                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Controller.dimissProgressDialog();
                                Controller.showLongToast(e.toString(), dashboardActivity);

                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Controller.dimissProgressDialog();
                Controller.showLongToast("" + e.toString(), dashboardActivity);

            }
        });


    }


    @Override
    public String onGetTimeCurrent() {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("EEEE dd-MMM-yyyy hh:mm:ss a");
        String datetime = dateformat.format(c.getTime());

        return datetime;
    }

    private void pickImageFromCamera() {

        //Intent of picking image from device camera
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Group Image Icon Title");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Group Image Icon Description");

        //put image uri
        image_uri = dashboardActivity.getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //insert to start camera
        Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraintent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraintent, IMAGE_PICK_CAMERA_REQUEST_CODE);

    }

    private void pickImageFromGallery() {

        //pick from gallery
        Intent gallery = new Intent(Intent.ACTION_PICK);
        gallery.setType("image/*");
        startActivityForResult(gallery, IMAGE_PICK_GALLERY_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {

        boolean result = ContextCompat.checkSelfPermission(dashboardActivity, Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(dashboardActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);

        Controller.appLogDebug("CHECK PERMISSION", result + "  " + result1);

        return result && result1;
    }

    private void requestCameraPermission() {

        ActivityCompat.requestPermissions(dashboardActivity, cameraPermissions, CAMERA_REQUEST_CODE);

    }

    private boolean checkStoragePermission() {

        //true if enable
        //false if not anable

        boolean result = ContextCompat.checkSelfPermission(dashboardActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);

        Controller.appLogDebug("CHECK PERMISSION", result + "");

        return result;
    }

    private void requestStoragePermission() {

        ActivityCompat.requestPermissions(dashboardActivity, storagePermissions, STORAGE_REQUEST_CODE);

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

                        Toast.makeText(dashboardActivity, "Please anable camera and storage permision !", Toast.LENGTH_SHORT).show();

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

                        Toast.makeText(dashboardActivity, "Please anable storage permision !", Toast.LENGTH_SHORT).show();

                    }

                }

            }

            break;

        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK) {

            if (requestCode == IMAGE_PICK_GALLERY_REQUEST_CODE) {

                image_uri = data.getData();

                Controller.appLogDebug("PICK_IMAGE", image_uri.toString());

                if (image_uri != null) {

                    img_avt_creategroup.setImageURI(image_uri);

                }

            }

            if (requestCode == IMAGE_PICK_CAMERA_REQUEST_CODE) {

                image_uri = data.getData();

                Controller.appLogDebug("PICK_IMAGE", image_uri.toString());

                if (image_uri != null) {

                    img_avt_creategroup.setImageURI(image_uri);

                }

            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
