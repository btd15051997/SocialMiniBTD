package com.example.socialminibtd.View.Activity.PostDetail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.socialminibtd.Adapter.CommentAdapter;
import com.example.socialminibtd.Model.ListComment;
import com.example.socialminibtd.R;
import com.example.socialminibtd.Utils.Const;
import com.example.socialminibtd.Utils.Controller;
import com.example.socialminibtd.View.Activity.AddPost.AddPostActivity;
import com.example.socialminibtd.View.Activity.Login.LoginActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class PostDetailActivity extends AppCompatActivity implements IPostDetailActivityView, View.OnClickListener {

    private TextView tv_name_postdetail, tv_date_postdetail, txt_more_postdetail, tv_title_postdetail, tv_decription_postdetail, tv_sumlike_postdetail, tv_plike_postdetail, tv_share_postdetail, tv_commenter_postdetail, tv_sum_comment_postdetail;
    private ImageView img_content_postdetail;
    private CircularImageView img_account_postdetail, img_commenter_postdetail;
    private RecyclerView recyc_post_detail;
    private EditText edt_commenter_postdetail;

    //to get detail of user and post
    private String myUid, myEmail, myName, myDp, postId, pLikes, pImage, hisDp, hisName, hisUid;

    //show list comment
    private ArrayList<ListComment> mArrayList;
    private CommentAdapter mCommentAdapter;

    private boolean mProessComment = false;
    private boolean mProcessLike = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_left);
        setContentView(R.layout.activity_post_detail);

        Intent intent = getIntent();

        postId = intent.getStringExtra("postId");

        onMappingViewPostDetail();

        // get user current info
        onCheckUserCurrent();

        onLoadUserInfo();

        onLoadPostInfo();

        onSetLikePost();

        onLoadAllComment();


    }


    @Override
    public void onMappingViewPostDetail() {

        tv_name_postdetail = findViewById(R.id.tv_name_postdetail);
        tv_date_postdetail = findViewById(R.id.tv_date_postdetail);
        txt_more_postdetail = findViewById(R.id.txt_more_postdetail);
        tv_title_postdetail = findViewById(R.id.tv_title_postdetail);

        tv_decription_postdetail = findViewById(R.id.tv_decription_postdetail);
        tv_sumlike_postdetail = findViewById(R.id.tv_sumlike_postdetail);
        tv_plike_postdetail = findViewById(R.id.tv_plike_postdetail);
        tv_share_postdetail = findViewById(R.id.tv_share_postdetail);

        tv_commenter_postdetail = findViewById(R.id.tv_commenter_postdetail);
        tv_sum_comment_postdetail = findViewById(R.id.tv_sum_comment_postdetail);

        img_content_postdetail = findViewById(R.id.img_content_postdetail);
        img_account_postdetail = findViewById(R.id.img_account_postdetail);
        img_commenter_postdetail = findViewById(R.id.img_commenter_postdetail);
        edt_commenter_postdetail = findViewById(R.id.edt_commenter_postdetail);
        recyc_post_detail = findViewById(R.id.recyc_post_detail);

        tv_commenter_postdetail.setOnClickListener(this);
        tv_plike_postdetail.setOnClickListener(this);
        txt_more_postdetail.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.tv_commenter_postdetail) {

            onPostComment();

        } else if (v.getId() == R.id.tv_plike_postdetail) {

            onPostLike();

        } else if (v.getId() == R.id.txt_more_postdetail) {

            onMoreSettingPost();

        }

    }

    @Override
    public void onLoadPostInfo() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");

        Query query = databaseReference.orderByChild("uIDTime").equalTo(postId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //keep checking the posts until get the required post

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    String pTitle = "" + ds.child("uTitle").getValue();
                    String pDescr = "" + ds.child("uDescription").getValue();

                    pLikes = "" + ds.child("uLikes").getValue();
                    hisDp = "" + ds.child("uDp").getValue();
                    hisName = "" + ds.child("uName").getValue();

                    String pTimeStamp = "" + ds.child("uTime").getValue();
                    pImage = "" + ds.child("uImage").getValue();
                    String pCommentCount = "" + ds.child("pComments").getValue();

                    hisUid = "" + ds.child("uid").getValue();
                    String uEmail = "" + ds.child("uEmail").getValue();


                    //set Value to view
                    tv_title_postdetail.setText(pTitle);
                    tv_decription_postdetail.setText(pDescr);
                    tv_date_postdetail.setText(pTimeStamp);
                    tv_name_postdetail.setText(hisName);
                    tv_sumlike_postdetail.setText("Likes " + pLikes);
                    tv_sum_comment_postdetail.setText(pCommentCount + " Comments");

                    try {

                        Picasso.get().load(hisDp).placeholder(R.drawable.ic_account).into(img_account_postdetail);

                    } catch (Exception e) {

                        Picasso.get().load(R.drawable.ic_account).into(img_account_postdetail);

                    }

                    if (pImage.equals("noImage")) {

                        img_content_postdetail.setVisibility(View.GONE);

                    } else {

                        try {

                            Picasso.get().load(pImage).placeholder(R.drawable.ic_account).into(img_content_postdetail);

                        } catch (Exception e) {

                            Picasso.get().load(R.drawable.ic_account).into(img_content_postdetail);

                        }
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Controller.appLogDebug(Const.LOG_DAT, " onLoadPostInfo  :" + databaseError.getMessage().toString());

            }
        });

    }

    @Override
    public void onCheckUserCurrent() {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {

            myUid = user.getUid();
            myEmail = user.getEmail();


        } else {

            startActivity(new Intent(PostDetailActivity.this, LoginActivity.class));
            finish();

        }
    }

    @Override
    public void onLoadUserInfo() {

        Query query = FirebaseDatabase.getInstance().getReference("User");
        query.orderByChild("uid").equalTo(myUid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    if (myUid.equals(ds.child("uid").getValue())){

                        myName = "" + ds.child("name").getValue();
                        myDp = "" + ds.child("image").getValue();

                        Controller.appLogDebug(Const.LOG_DAT, "onLoadUserInfo :" + myName + "  " + myDp);

                        try {

                            Picasso.get().load(myDp).placeholder(R.drawable.ic_account).into(img_commenter_postdetail);

                        } catch (Exception e) {

                            Picasso.get().load(R.drawable.ic_account).into(img_commenter_postdetail);

                        }

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onPostComment() {

        Controller.showProgressDialog(PostDetailActivity.this
                , "Adding comment...");

        String comment = edt_commenter_postdetail.getText().toString().trim();

        if (TextUtils.isEmpty(comment)) {

            tv_commenter_postdetail.setClickable(false);
            Toast.makeText(this, "" + getResources().getString(R.string.txt_enter), Toast.LENGTH_SHORT).show();

            return;

        }


        String TimeStamp = onGetTimeCurrent();

        //each post with have a child "Comments" will contain comment of post
        DatabaseReference RefAddComment = FirebaseDatabase.getInstance()
                .getReference("Posts").child(postId).child("Comments");

        HashMap<String, Object> hashMap = new HashMap<>();
        //put info in hashmap
        hashMap.put("cId", TimeStamp);
        hashMap.put("comment", comment);
        hashMap.put("timestamp", TimeStamp);
        hashMap.put("uid", myUid);
        hashMap.put("uEmail", myEmail);
        hashMap.put("uDp", myDp);
        hashMap.put("uName", myName);

        Controller.appLogDebug(Const.LOG_DAT, " onPostComment " + hashMap.toString());

        //put this data to Firebase
        RefAddComment.child(TimeStamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Controller.dimissProgressDialog();
                        Controller.showLongToast("Comment Added", PostDetailActivity.this);
                        edt_commenter_postdetail.setText("");
                        onUpdateCommentCount();


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Controller.dimissProgressDialog();
                Controller.appLogDebug(Const.LOG_DAT, " onPostComment" + e.toString());

            }
        });

    }

    @Override
    public void onLoadAllComment() {


        recyc_post_detail.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PostDetailActivity.this
                , LinearLayoutManager.VERTICAL, true);

        //show newest post first, for this load from last
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        recyc_post_detail.setLayoutManager(linearLayoutManager);

        mArrayList = new ArrayList<>();


        DatabaseReference RefListComment = FirebaseDatabase.getInstance().getReference("Posts")
                .child(postId).child("Comments");

        RefListComment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (mArrayList != null) {

                    mArrayList.clear();

                }

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    ListComment listComment = ds.getValue(ListComment.class);

                    mArrayList.add(listComment);

                }

                mCommentAdapter = new CommentAdapter(mArrayList, PostDetailActivity.this,myUid,postId);

                recyc_post_detail.setAdapter(mCommentAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    @Override
    public void onSetLikePost() {

        //when the detail of post loading, also check if current user has liked it or not
        final DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(postId).hasChild(myUid)) {
                    // user has liked this post
                    tv_plike_postdetail.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorited
                            , 0, 0, 0);

                    tv_plike_postdetail.setText("Liked");

                } else {
                    // user has not liked this post
                    tv_plike_postdetail.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite
                            , 0, 0, 0);

                    tv_plike_postdetail.setText("Like");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });

    }

    @Override
    public void onMoreSettingPost() {

        //creating popup menu option Delete, we will add more options later
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {

            PopupMenu popupMenu = new PopupMenu(this, txt_more_postdetail, Gravity.END);

            if (hisUid.equals(myUid)) {

                popupMenu.getMenu().add(Menu.NONE, 0, 0, getResources().getString(R.string.txt_delete));

                popupMenu.getMenu().add(Menu.NONE, 1, 0, getResources().getString(R.string.txt_edit));
            }

            popupMenu.show();

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    if (item.getItemId() == 0) {

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PostDetailActivity.this);

                        alertDialog.setTitle("Do you want to delete the post ?");

                        alertDialog.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (pImage.equals("noImage")) {

                                    onDeleteWithoutImage(postId);

                                } else {

                                    onDeleteWithImage(postId, pImage);

                                }


                            }
                        });

                        alertDialog.setPositiveButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();

                            }
                        });

                        AlertDialog dialog = alertDialog.create();
                        dialog.show();


                    } else if (item.getItemId() == 1) {

                        //start AddPostActivity with "editPost" and the id of the post clicked
                        Intent intent = new Intent(PostDetailActivity.this, AddPostActivity.class);

                        intent.putExtra("key", "editPost");
                        intent.putExtra("editPostId", postId);

                        startActivity(intent);

                    }

                    return false;
                }
            });

        }

    }

    @Override
    public void onDeleteWithoutImage(String postId) {

        Controller.showProgressDialog(PostDetailActivity.this, "Deleting...");

        //delete database
        Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("uIDTime").equalTo(postId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    ds.getRef().removeValue(); // remove values from firebase when pid matches

                }
                Controller.showLongToast("Deleted successfully", PostDetailActivity.this);

                Controller.dimissProgressDialog();

                onBackPressed();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Controller.dimissProgressDialog();
                Controller.appLogDebug(Const.LOG_DAT, databaseError.getMessage());
                Controller.showLongToast(databaseError.getMessage().toString(), PostDetailActivity.this);

            }
        });


    }

    @Override
    public void onDeleteWithImage(final String postId, String pImage) {

        Controller.showProgressDialog(PostDetailActivity.this, "Deleting...");
        /*
         *1: Delete Image url using url
         *2: Delete from database using post id*/

        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(pImage);
        picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                //delete database
                Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("uIDTime").equalTo(postId);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            ds.getRef().removeValue(); // remove values from firebase when pid matches

                        }

                        Controller.showLongToast("Deleted successfully", PostDetailActivity.this);

                        Controller.dimissProgressDialog();

                        onBackPressed();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        Controller.dimissProgressDialog();
                        Controller.appLogDebug(Const.LOG_DAT, databaseError.getMessage());
                        Controller.showLongToast(databaseError.getMessage().toString(), PostDetailActivity.this);

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Controller.dimissProgressDialog();
                Controller.showLongToast(e.toString(), PostDetailActivity.this);
                Controller.appLogDebug(Const.LOG_DAT, "" + e.toString());

            }
        });

    }

    @Override
    public void onPostLike() {

        mProcessLike = true;

        // get id of the post cliked
        final DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        final DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");


        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (mProcessLike) {

                    if (dataSnapshot.child(postId).hasChild(myUid)) {
                        //already like post, so remove like
                        postsRef.child(postId).child("uLikes").setValue("" + (Integer.parseInt(pLikes) - 1));

                        likesRef.child(postId).child(myUid).removeValue();

                        mProcessLike = false;


                    } else {
                        //not liked, like it
                        postsRef.child(postId).child("uLikes").setValue("" + (pLikes + 1));

                        likesRef.child(postId).child(myUid).setValue("Liked"); // set any value

                        mProcessLike = false;


                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });


    }


    @Override
    public void onUpdateCommentCount() {

        mProessComment = true;

        final DatabaseReference Ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        Ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (mProessComment) {

                    String comment = "" + dataSnapshot.child("pComments").getValue();
                    int newComment = Integer.parseInt(comment) + 1;

                    Ref.child("pComments").setValue("" + newComment);

                    mProessComment = false;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
