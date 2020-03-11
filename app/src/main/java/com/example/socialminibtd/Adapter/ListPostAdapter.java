package com.example.socialminibtd.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.socialminibtd.Model.ListPost;
import com.example.socialminibtd.R;
import com.example.socialminibtd.Utils.Const;
import com.example.socialminibtd.Utils.Controller;
import com.example.socialminibtd.View.Activity.AddPost.AddPostActivity;
import com.example.socialminibtd.View.Activity.PostDetail.PostDetailActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListPostAdapter extends RecyclerView.Adapter<ListPostAdapter.ViewHolder> {

    private ArrayList<ListPost> arrayList;
    private Context context;
    private String myUid;

    private DatabaseReference likesRef; // for likes of post
    private DatabaseReference postsRef; // for post

    private boolean mProgressLike = false;

    public ListPostAdapter(ArrayList<ListPost> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;

        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_list_post, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {


        final String uid = arrayList.get(position).getUid();
        String uDp = arrayList.get(position).getuDp();
        String uEmail = arrayList.get(position).getuEmail();
        String uName = arrayList.get(position).getuName();


        String pTitle = arrayList.get(position).getuTitle();
        String pTime = arrayList.get(position).getuTime();
        final String pImage = arrayList.get(position).getuImage();
        final String pIDTime = arrayList.get(position).getuIDTime();
        String pDescription = arrayList.get(position).getuDescription();
        final String pLike = arrayList.get(position).getuLikes();
        String pComment = arrayList.get(position).getpComments();

        holder.txt_description_listpost.setText(pDescription);
        holder.txt_name_listpost.setText(uName);
        holder.txt_date_listpost.setText(pTime);
        holder.txt_title_listpost.setText(pTitle);

        holder.txt_sumlike_listpost.setText(pLike + " Likes");
        holder.tv_sumcomment_post.setText(pComment + " Comments");

        // set likes for each post
        setLike(holder, pIDTime);


        try {

            Picasso.get().load(uDp).placeholder(R.drawable.ic_account).into(holder.img_account_listpost);
            Controller.appLogDebug(Const.LOG_DAT, uDp.toString());

        } catch (Exception e) {

            Picasso.get().load(R.drawable.ic_account).into(holder.img_account_listpost);

        }

        try {

            if (pImage.equals("noImage")
                    && holder.img_content_listpost.getVisibility() == View.VISIBLE) {

                holder.img_content_listpost.setVisibility(View.GONE);


            } else {

                //   Animation slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_in_up);

                holder.img_content_listpost.setVisibility(View.VISIBLE);

                //    holder.img_content_listpost.startAnimation(slideUp);

                Picasso.get().load(pImage).into(holder.img_content_listpost);

            }

        } catch (Exception e) {

            Picasso.get().load(R.drawable.ic_account).into(holder.img_account_listpost);

        }

        holder.txt_more_listpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                showMoreOptions(holder.txt_more_listpost, uid, myUid, pIDTime, pImage);

            }
        });

        holder.txt_click_likepost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final int pLikes = Integer.parseInt(arrayList.get(position).getuLikes());

                mProgressLike = true;

                // get id of the post cliked
                final String postId = arrayList.get(position).getuIDTime();

                likesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (mProgressLike) {

                            if (dataSnapshot.child(postId).hasChild(myUid)) {
                                //already like post, so remove like
                                postsRef.child(postId).child("uLikes").setValue("" + (pLikes - 1));

                                likesRef.child(postId).child(myUid).removeValue();

                                mProgressLike = false;

                            } else {
                                //not liked, like it
                                postsRef.child(postId).child("uLikes").setValue("" + (pLikes + 1));

                                likesRef.child(postId).child(myUid).setValue("Liked"); // set any value

                                mProgressLike = false;
                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {


                    }
                });

            }
        });

        holder.txt_comment_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onStartPostDetail(pIDTime);

            }
        });


        holder.txt_share_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Toast.makeText(context, "Share", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void onStartPostDetail(String pIDTime) {

        //start postdetail
        Intent intentPostDetail = new Intent(context, PostDetailActivity.class);
        intentPostDetail.putExtra("postId", pIDTime);
        context.startActivity(intentPostDetail);
    }

    private void setLike(final ViewHolder holder, final String postKey) {

        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(postKey).hasChild(myUid)) {
                    // user has liked this post
                    holder.txt_click_likepost.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorited
                            , 0, 0, 0);

                    holder.txt_click_likepost.setText("Liked");

                } else {
                    // user has not liked this post
                    holder.txt_click_likepost.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite
                            , 0, 0, 0);

                    holder.txt_click_likepost.setText("Like");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });

    }

    private void showMoreOptions(TextView txt_more_listpost, String uid, String myUid, final String pIDTime, final String pImage) {

        //creating popup menu option Delete, we will add more options later
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {

            PopupMenu popupMenu = new PopupMenu(context, txt_more_listpost, Gravity.END);

            if (uid.equals(myUid)) {

                popupMenu.getMenu().add(Menu.NONE, 0, 0, context.getResources().getString(R.string.txt_delete));

                popupMenu.getMenu().add(Menu.NONE, 1, 0, context.getResources().getString(R.string.txt_edit));
            }

            popupMenu.getMenu().add(Menu.NONE, 2, 0, context.getResources().getString(R.string.txt_view_detail));

            popupMenu.show();

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    if (item.getItemId() == 0) {

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

                        alertDialog.setTitle("Do you want to delete the post ?");

                        alertDialog.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (pImage.equals("noImage")) {

                                    deleteWithoutImage(pIDTime);

                                } else {

                                    deleteWithImage(pIDTime, pImage);

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
                        Intent intent = new Intent(context, AddPostActivity.class);

                        intent.putExtra("key", "editPost");
                        intent.putExtra("editPostId", pIDTime);

                        context.startActivity(intent);

                    } else if (item.getItemId() == 2) {

                        onStartPostDetail(pIDTime);

                    }

                    return false;
                }
            });

        }

    }

    private void deleteWithImage(final String pIDTime, String pImage) {

        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Deleting...");
        /*
         *1: Delete Image url using url
         *2: Delete from database using post id*/

        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(pImage);
        picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                //delete database

                Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("uIDTime").equalTo(pIDTime);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            ds.getRef().removeValue(); // remove values from firebase when pid matches

                        }

                        Controller.showLongToast("Deleted successfully", context);

                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        progressDialog.dismiss();
                        Controller.appLogDebug(Const.LOG_DAT, databaseError.getMessage());
                        Controller.showLongToast(databaseError.getMessage().toString(), context);

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressDialog.dismiss();
                Controller.showLongToast(e.toString(), context);
                Controller.appLogDebug(Const.LOG_DAT, "" + e.toString());

            }
        });

    }

    private void deleteWithoutImage(String pIDTime) {

        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Deleting...");

        //delete database
        Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("uIDTime").equalTo(pIDTime);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    ds.getRef().removeValue(); // remove values from firebase when pid matches

                }
                Controller.showLongToast("Deleted successfully", context);

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                progressDialog.dismiss();
                Controller.appLogDebug(Const.LOG_DAT, databaseError.getMessage());
                Controller.showLongToast(databaseError.getMessage().toString(), context);

            }
        });


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircularImageView img_account_listpost;
        ImageView img_content_listpost;
        TextView txt_name_listpost, txt_date_listpost, txt_more_listpost, txt_title_listpost
                , txt_description_listpost, txt_sumlike_listpost, txt_click_likepost
                , txt_comment_post, txt_share_post, tv_sumcomment_post;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img_account_listpost = itemView.findViewById(R.id.img_account_listpost);

            img_content_listpost = itemView.findViewById(R.id.img_content_listpost);

            txt_name_listpost = itemView.findViewById(R.id.txt_name_listpost);

            txt_date_listpost = itemView.findViewById(R.id.txt_date_listpost);

            txt_more_listpost = itemView.findViewById(R.id.txt_more_listpost);

            txt_title_listpost = itemView.findViewById(R.id.txt_title_listpost);

            txt_description_listpost = itemView.findViewById(R.id.txt_description_listpost);

            txt_sumlike_listpost = itemView.findViewById(R.id.txt_sumlike_listpost);

            txt_click_likepost = itemView.findViewById(R.id.txt_click_likepost);

            txt_comment_post = itemView.findViewById(R.id.txt_comment_post);

            txt_share_post = itemView.findViewById(R.id.txt_share_post);

            tv_sumcomment_post = itemView.findViewById(R.id.tv_sumcomment_post);


        }
    }
}
