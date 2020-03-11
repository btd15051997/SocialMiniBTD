package com.example.socialminibtd.View.Fragment.ProfileFragment;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialminibtd.Adapter.ListPostAdapter;
import com.example.socialminibtd.Model.ListPost;
import com.example.socialminibtd.Presenter.Fragment.ProfilePresenter.ProfilePresenter;
import com.example.socialminibtd.R;
import com.example.socialminibtd.Utils.Const;
import com.example.socialminibtd.Utils.Controller;
import com.example.socialminibtd.View.Activity.AddPost.AddPostActivity;
import com.example.socialminibtd.View.Activity.Dashbroad.DashboardActivity;
import com.example.socialminibtd.View.Dialog.ForgotPassword.DialogForgotPassword;
import com.example.socialminibtd.View.Dialog.SettingsDialog.SettingsDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

import java.util.ArrayList;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment implements IProfileFragmentView, View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private View mView;
    private DashboardActivity mDashboardActivity;
    private ImageView img_edit_text_profile, img_background_profile;
    private ImageView img_avatar_profile,img_profile_sortmenu;
    private TextView txt_name_profile, txt_email_profile, txt_phone_profile;
    private RelativeLayout relative1_img_profile;
    private EditText edt_search_listpost_profile;
    private FloatingActionButton fab_profile;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mReference;
    private ProfilePresenter mPresenter;
    private FirebaseDatabase mFirebaseDatabase;

    private StorageReference mStorageReference;

    private String uid;

    //permissions constants
    private static final int CAMERA_REQUEST_CODE = 101;
    private static final int STORAGE_REQUEST_CODE = 102;
    private static final int IMAGE_PICK_GALLERY_REQUEST_CODE = 103;
    private static final int IMAGE_PICK_CAMERA_REQUEST_CODE = 104;


    //uri image
    private Uri image_uri;
    private String cameraPermissions[];
    private String storagePermissions[];
    private String PhotoAvatarOrPhotoCover;
    private String storagePath = "Users_Profile_Cover_Image/";


    //adapter
    private RecyclerView recyc_listpost_profile;
    private ListPostAdapter postAdapter;
    private ArrayList<ListPost> postArrayList;


    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDashboardActivity = (DashboardActivity) getActivity();

        if (getArguments() != null) {

        }

    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_profile, container, false);

        onInitFirebase();

        onMappingView();

        onGetDataShowProfile();

        onShowListPostProfile();

        return mView;
    }

    @Override
    public void onMappingView() {


        img_avatar_profile = mView.findViewById(R.id.img_avatar_profile);
        txt_name_profile = mView.findViewById(R.id.txt_name_profile);
        txt_email_profile = mView.findViewById(R.id.txt_email_profile);
        txt_phone_profile = mView.findViewById(R.id.txt_phone_profile);
        img_edit_text_profile = mView.findViewById(R.id.img_edit_text_profile);
        img_background_profile = mView.findViewById(R.id.img_background_profile);
        relative1_img_profile = mView.findViewById(R.id.relative1_img_profile);
        recyc_listpost_profile = mView.findViewById(R.id.recyc_listpost_profile);
        edt_search_listpost_profile = mView.findViewById(R.id.edt_search_listpost_profile);
        img_profile_sortmenu = mView.findViewById(R.id.img_profile_sortmenu);
        fab_profile = mView.findViewById(R.id.fab_profile);

        mPresenter = new ProfilePresenter(mDashboardActivity, this);

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        onSettingRecyclerView();

        img_background_profile.setOnClickListener(this);
        img_edit_text_profile.setOnClickListener(this);
        img_profile_sortmenu.setOnClickListener(this);
        relative1_img_profile.setOnClickListener(this);

        edt_search_listpost_profile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                onSearchListPost(s.toString());


            }
        });

        fab_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDashboardActivity.startActivity(new Intent(mDashboardActivity, AddPostActivity.class));

            }
        });

    }


    @Override
    public void onInitFirebase() {

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mReference = mFirebaseDatabase.getReference("User");
        mStorageReference = FirebaseStorage.getInstance().getReference(); // firebase instance
        onShowProgressBarLoading();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.img_edit_text_profile:

                onShowEditTextProfile();

                break;

            case R.id.relative1_img_profile:

                PhotoAvatarOrPhotoCover = "image";
                onShowImagePickDialog();

                break;

            case R.id.img_background_profile:

                PhotoAvatarOrPhotoCover = "image_cover";
                onShowImagePickDialog();

                break;

            case R.id.img_profile_sortmenu:

                PopupMenu popupMenu = new PopupMenu(mDashboardActivity, img_profile_sortmenu, Gravity.CENTER_HORIZONTAL);

                popupMenu.getMenu().add(Menu.NONE, 0, 0, mDashboardActivity.getResources().getString(R.string.txt_logout));

                popupMenu.getMenu().add(Menu.NONE, 1, 0,"Settings");


                popupMenu.show();


                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {

                            case 0:

                                mAuth.signOut();
                                mDashboardActivity.onCheckUserCurrent();

                                break;

                            case 1:

                                SettingsDialog dialogSetting = new SettingsDialog();
                                dialogSetting.setCancelable(true);
                                dialogSetting.show(getFragmentManager(), "DialogSettings");

                                break;

                        }

                        return false;
                    }
                });


                break;

        }

    }


    @Override
    public void onGetDataShowProfile() {

        //we have to get info of currently signed in user
        //we can get it using user email or UId, retrive user detail using email
        // By using orderByChild query we will show the detail from a node whose key named email has value equal to currently signed in email.
        // It will search all nodes, where the key matches it will get its detail

        Query query = mReference.orderByChild("email").equalTo(mUser.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //checkc until required data get
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //get data

                    mPresenter.onGetDataShowProfile(ds);

                    uid = "" + ds.child("uid").getValue();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void onShowImagePickDialog() {

        String options[] = {mDashboardActivity.getResources().getString(R.string.txt_camera)
                , mDashboardActivity.getResources().getString(R.string.txt_gallery)};

        AlertDialog.Builder builder = new AlertDialog.Builder(mDashboardActivity);

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
    public void onShowEditTextProfile() {

        String options[] = {mDashboardActivity.getResources().getString(R.string.txt_edit_name)
                , mDashboardActivity.getResources().getString(R.string.txt_edit_email)
                , mDashboardActivity.getResources().getString(R.string.txt_edit_phone)};

        AlertDialog.Builder builder = new AlertDialog.Builder(mDashboardActivity);

        builder.setTitle(null);

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {

                    case 0:

                        onUpdateNamePhoneProfile("name");

                        break;
                    case 1:

                        break;

                    case 2:

                        onUpdateNamePhoneProfile("phone");

                        break;

                }

            }
        });

        builder.create().show();

    }

    @Override
    public void onSettingRecyclerView() {

        recyc_listpost_profile.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mDashboardActivity, LinearLayoutManager.VERTICAL, true);

        linearLayoutManager.setStackFromEnd(true);

        linearLayoutManager.setReverseLayout(true);

        recyc_listpost_profile.setLayoutManager(linearLayoutManager);

        postArrayList = new ArrayList<>();

    }

    @Override
    public void onShowListPostProfile() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (postArrayList != null) {

                    postArrayList.clear();

                }

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    ListPost listPost = ds.getValue(ListPost.class);

                    if (mUser.getUid().equals(listPost.getUid())) {

                        postArrayList.add(listPost);

                    }
                }

                postAdapter = new ListPostAdapter(postArrayList, mDashboardActivity);

                recyc_listpost_profile.setAdapter(postAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Controller.showLongToast(databaseError.toString(), mDashboardActivity);

            }
        });

    }

    @Override
    public void onSearchListPost(final String textQuery) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (postArrayList != null) {

                    postArrayList.clear();

                }

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    ListPost listPost = ds.getValue(ListPost.class);

                    if (mUser.getUid().equals(listPost.getUid()) && listPost.getuTitle().toLowerCase().contains(textQuery)
                            || listPost.getuDescription().toLowerCase().contains(textQuery)) {

                        postArrayList.add(listPost);

                    }
                }

                postAdapter = new ListPostAdapter(postArrayList, mDashboardActivity);

                recyc_listpost_profile.setAdapter(postAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Controller.showLongToast(databaseError.toString(), mDashboardActivity);

            }
        });


    }

    @Override
    public void onShowUIProfile(String name, String email, String phone, String image, String image_cover) {

        txt_name_profile.setText(name);
        txt_email_profile.setText(email);

        if (phone.equals("")) {

            txt_phone_profile.setText(null);

        } else {

            txt_phone_profile.setText(phone);
        }

        //image account
        try {

            Picasso.get().load(image).into(img_avatar_profile);


        } catch (Exception e) {

            Picasso.get().load(R.drawable.ic_account).into(img_avatar_profile);

        }

        //image background
        try {

            Picasso.get().load(image_cover).into(img_background_profile);

        } catch (Exception e) {

            Picasso.get().load(R.color.color_background_main).into(img_background_profile);

        }

    }

    @Override
    public void onUploadConverPhotoProfile(Uri image_uriimage_uri) {

        //path and name of image to be stored in firebase storage

        onShowProgressBarLoading();

        String filePathAndName = storagePath + "" + PhotoAvatarOrPhotoCover + "_" + mUser.getUid();

        StorageReference storageReference2 = mStorageReference.child(filePathAndName);

        storageReference2.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                //image is upload to storage, new get it url and store database
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                while (!uriTask.isSuccessful()) ;

                final Uri downLoadUri = uriTask.getResult();

                Log.d("TESST", String.valueOf(taskSnapshot.getStorage().getDownloadUrl()));

                if (uriTask.isSuccessful()) {

                    // add/update url in user database
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put(PhotoAvatarOrPhotoCover, downLoadUri.toString());

                    mReference.child(mUser.getUid()).updateChildren(hashMap)

                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Controller.removeProgressDialog();
                                    Controller.showLongToast("Image Updated", mDashboardActivity);


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Controller.removeProgressDialog();
                            Controller.appLogDebug(Const.LOG_DAT, " update url in user database " + e.toString());
                            Controller.showLongToast("" + e.toString(), mDashboardActivity);

                        }
                    });

                    if (PhotoAvatarOrPhotoCover.equals("image")) {

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                        Query query = ref.orderByChild("uid").equalTo(uid);

                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                    String child = ds.getKey();
                                    dataSnapshot.getRef().child(child).child("uDp").setValue(downLoadUri.toString());

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                Controller.appLogDebug(Const.LOG_DAT, databaseError.getMessage());

                            }
                        });
                        //  update image in current users comments on posts
                        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                    String child = ds.getKey();

                                    if (dataSnapshot.child(child).hasChild("Comments")) {

                                        String child1 = "" + dataSnapshot.child(child).getKey();

                                        Query child2 = FirebaseDatabase.getInstance().getReference("Posts")
                                                .child(child1).child("Comments").orderByChild("uid").equalTo(uid);

                                        child2.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                                    String child = ds.getKey();

                                                    dataSnapshot.getRef().child(child).child("uDp").setValue(downLoadUri.toString());

                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                Controller.appLogDebug(Const.LOG_DAT, "" + databaseError.getMessage());

                                            }
                                        });

                                    }

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                } else {

                    //error
                    Controller.showLongToast("Error Upload", mDashboardActivity);

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Controller.removeProgressDialog();
                Controller.appLogDebug(Const.LOG_DAT, " UploadConverPhotoProfile " + e.toString());
                Controller.showLongToast("" + e.toString(), mDashboardActivity);


            }
        });


    }

    @Override
    public void onUpdateNamePhoneProfile(final String key) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mDashboardActivity);
        builder.setTitle(mDashboardActivity.getResources().getString(R.string.txt_update) + " " + key);

        LinearLayout linearLayout = new LinearLayout(mDashboardActivity);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10, 10, 10, 10);

        final EditText editText = new EditText(mDashboardActivity);
        editText.setHint(mDashboardActivity.getResources().getString(R.string.txt_enter) + " " + key);
        linearLayout.addView(editText);

        builder.setView(linearLayout);

        builder.setPositiveButton(mDashboardActivity.getResources().getString(R.string.txt_update)
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        final String value = editText.getText().toString().trim();

                        if (editText.getText().toString().isEmpty()) {

                            Controller.showLongToast("Please Enter " + key, mDashboardActivity);

                        } else {

                            onShowProgressBarLoading();

                            HashMap<String, Object> hashMap = new HashMap<>();

                            hashMap.put(key, value);

                            mReference.child(mUser.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    onRemoveProgressBarLoading();
                                    Controller.showLongToast(mDashboardActivity.getResources().getString(R.string.txt_update), mDashboardActivity);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    onRemoveProgressBarLoading();
                                    Controller.appLogDebug(Const.LOG_DAT, " update url in user database " + e.toString());
                                    Controller.showLongToast(e.toString(), mDashboardActivity);
                                }
                            });


                            if (key.equals("name")) {

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                                Query query = ref.orderByChild("uid").equalTo(uid);

                                query.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                            String child = ds.getKey();
                                            dataSnapshot.getRef().child(child).child("uName").setValue(value);

                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                        Controller.appLogDebug(Const.LOG_DAT, databaseError.getMessage());

                                    }
                                });
                                //  update name in current users comments on posts
                                mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                            String child = ds.getKey();

                                            if (dataSnapshot.child(child).hasChild("Comments")) {

                                                String child1 = "" + dataSnapshot.child(child).getKey();

                                                Query child2 = FirebaseDatabase.getInstance().getReference("Posts")
                                                        .child(child1).child("Comments").orderByChild("uid").equalTo(uid);

                                                child2.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                                            String child = ds.getKey();

                                                            dataSnapshot.getRef().child(child).child("uName").setValue(value);

                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        Controller.appLogDebug(Const.LOG_DAT, "" + databaseError.getMessage());

                                                    }
                                                });

                                            }

                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                            }

                        }

                    }
                });

        builder.setNegativeButton(mDashboardActivity.getResources().getString(R.string.txt_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }
        });

        builder.create().show();

    }

    @Override
    public void onShowProgressBarLoading() {

        Controller.showSimpleProgressDialog(mDashboardActivity
                , mDashboardActivity.getResources().getString(R.string.txt_loading)
                , false);

    }

    @Override
    public void onRemoveProgressBarLoading() {

        Controller.removeProgressDialog();

    }

    private boolean checkStoragePermission() {

        //true if enable
        //false if not anable

        boolean result = ContextCompat.checkSelfPermission(mDashboardActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);

        Controller.appLogDebug("CHECK PERMISSION", result + "");

        return result;
    }

    private void requestStoragePermission() {

        ActivityCompat.requestPermissions(mDashboardActivity, storagePermissions, STORAGE_REQUEST_CODE);

    }

    private boolean checkCameraPermission() {

        boolean result = ContextCompat.checkSelfPermission(mDashboardActivity, Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(mDashboardActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);

        Controller.appLogDebug("CHECK PERMISSION", result + "");

        return result && result1;
    }

    private void requestCameraPermission() {

        ActivityCompat.requestPermissions(mDashboardActivity, cameraPermissions, CAMERA_REQUEST_CODE);

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

                        Toast.makeText(mDashboardActivity, "Please anable camera and storage permision !", Toast.LENGTH_SHORT).show();

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

                        Toast.makeText(mDashboardActivity, "Please anable storage permision !", Toast.LENGTH_SHORT).show();

                    }

                }

            }

            break;

        }

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
        image_uri = mDashboardActivity.getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //insert to start camera
        Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraintent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraintent, IMAGE_PICK_CAMERA_REQUEST_CODE);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK) {

            if (requestCode == IMAGE_PICK_GALLERY_REQUEST_CODE) {

                image_uri = data.getData();

                Controller.appLogDebug("PICK_IMAGE", image_uri.toString());

                onUploadConverPhotoProfile(image_uri);

            }

            if (requestCode == IMAGE_PICK_CAMERA_REQUEST_CODE) {

                Controller.appLogDebug("PICK_IMAGE", image_uri.toString());

                onUploadConverPhotoProfile(image_uri);

            }

        }


        super.onActivityResult(requestCode, resultCode, data);
    }
}
