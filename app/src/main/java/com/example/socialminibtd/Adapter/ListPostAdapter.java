package com.example.socialminibtd.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.socialminibtd.Model.ListPost;
import com.example.socialminibtd.R;
import com.example.socialminibtd.Utils.Const;
import com.example.socialminibtd.Utils.Controller;
import com.example.socialminibtd.View.Activity.AddPost.AddPostActivity;
import com.example.socialminibtd.View.Activity.PostDetail.PostDetailActivity;
import com.example.socialminibtd.View.Dialog.PostLikedBy.PostLikedByDialog;
import com.example.socialminibtd.View.Dialog.ShowImagePost.ShowImageOfPost;
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
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;


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


        final String pTitle = arrayList.get(position).getpTitle();
        String pTime = arrayList.get(position).getpTime();
        final String pImage = arrayList.get(position).getpImage();
        final String pIDTime = arrayList.get(position).getpIDTime();
        final String pDescription = arrayList.get(position).getpDescription();
        final String pLike = arrayList.get(position).getpLikes();
        String pComment = arrayList.get(position).getpComments();

        holder.txt_description_listpost.setText(pDescription);
        holder.txt_name_listpost.setText(uName);


        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(pTime));
        String dateTime = DateFormat.format("dd-MM-yyyy hh:mm aa", cal).toString();

        holder.txt_date_listpost.setText(dateTime);

        holder.txt_title_listpost.setText(pTitle);

        holder.txt_sumlike_listpost.setText(pLike + " Likes");

        holder.tv_sumcomment_post.setText(pComment + " Comments");

        // set likes for each post
        setLike(holder, pIDTime);


        try {

            Picasso.get().load(uDp).placeholder(R.drawable.icon_logoapp).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                    holder.img_account_listpost.setImageBitmap(bitmap);

                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
            Controller.appLogDebug(Const.LOG_DAT, uDp.toString());

        } catch (Exception e) {

            holder.img_account_listpost.setImageResource(R.drawable.icon_logoapp);

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

        holder.img_content_listpost.setOnClickListener(v -> {

            FragmentActivity activity = (FragmentActivity) (context);
            FragmentManager fm = activity.getSupportFragmentManager();

            ShowImageOfPost showImageOfPost = new ShowImageOfPost();
            showImageOfPost.setCancelable(true);

            Bundle bundle = new Bundle();

            bundle.putString("postID", pIDTime);

            showImageOfPost.setArguments(bundle);

            showImageOfPost.show(fm, "ShowImageOfPost");

        });

        holder.txt_more_listpost.setOnClickListener(v -> showMoreOptions(holder.txt_more_listpost, uid, myUid, pIDTime, pImage));

        holder.txt_sumlike_listpost.setOnClickListener(v -> {

            FragmentActivity activity = (FragmentActivity) (context);
            FragmentManager fm = activity.getSupportFragmentManager();

            PostLikedByDialog postLikedByDialog = new PostLikedByDialog();
            postLikedByDialog.setCancelable(true);

            Bundle bundle = new Bundle();

            bundle.putString("postID", pIDTime);

            postLikedByDialog.setArguments(bundle);

            postLikedByDialog.show(fm, "DialogPostLikedBy");


        });

        holder.txt_click_likepost.setOnClickListener(v -> {

            onRefreshItemView(position, arrayList.get(position));

            final int pLikes = Integer.parseInt(arrayList.get(position).getpLikes());

            mProgressLike = true;

            // get id of the post cliked
            final String postId = arrayList.get(position).getpIDTime();

            likesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (mProgressLike) {


                        if (dataSnapshot.child(postId).hasChild(myUid)) {
                            //already like post, so remove like
                            postsRef.child(postId).child("pLikes").setValue("" + (pLikes - 1));

                            likesRef.child(postId).child(myUid).removeValue();

                            mProgressLike = false;

                        } else {
                            //not liked, like it
                            postsRef.child(postId).child("pLikes").setValue("" + (pLikes + 1));

                            likesRef.child(postId).child(myUid).setValue("Liked"); // set any value


                            mProgressLike = false;

                            addHistoryNotification("" + uid, postId, "Liked your post");

                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {


                }
            });

        });

        holder.txt_comment_post.setOnClickListener(v -> onStartPostDetail(pIDTime));

        holder.txt_share_post.setOnClickListener(v -> {

            /*some posts contains only text, and some contains image and text so
            , we will handle them both*/
            //get image from imageview

            BitmapDrawable bitmapDrawable = (BitmapDrawable) holder.img_content_listpost.getDrawable();

            if (bitmapDrawable == null) {
                //post without image

                onShareTextOnly(pTitle, pDescription);

            } else {

                //covert bitmap
                Bitmap bitmap = bitmapDrawable.getBitmap();

                onShareTextAndImage(pTitle, pDescription, bitmap);


            }

        });
    }

    private void onRefreshItemView(int position, ListPost listPost) {

        arrayList.set(position, listPost);
        notifyItemChanged(position);

    }


    private void onShareTextAndImage(String pTitle, String pDescription, Bitmap bitmap) {

        //concatenate title and description to share
        String shareBody = pTitle + "\n" + pDescription;

        //first we will  save this image in cache, get the saved image uri
        Uri uri = saveImageToShare(bitmap);

        //share intent
        //share intent
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);// in case you share via an email app
        intent.putExtra(Intent.EXTRA_TEXT, shareBody);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject here");
        intent.setType("image/png");
        context.startActivity(Intent.createChooser(intent, "Share Via"));


    }

    private Uri saveImageToShare(Bitmap bitmap) {

        File imageFolder = new File(context.getCacheDir(), "images");
        Uri uri = null;

        try {

            imageFolder.mkdir(); // create if not exists
            File file = new File(imageFolder, "shared_image.png");
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();

            uri = FileProvider.getUriForFile(context, "com.example.socialminibtd.fileprovider", file);


        } catch (Exception e) {

            Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

        }

        return uri;
    }

    private void onShareTextOnly(String pTitle, String pDescription) {

        //concatenate title and description to share
        String shareBody = pTitle + "\n" + pDescription;

        //share intent
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject  here");// in case you share via an email app
        intent.putExtra(Intent.EXTRA_TEXT, shareBody);
        context.startActivity(Intent.createChooser(intent, "Share Via"));// message to show in share dialog

    }

    private void addHistoryNotification(String hisUid, String pId, String message) {

        String timeStamp = String.valueOf(System.currentTimeMillis());

        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("pId", pId);
        hashMap.put("timestamp", timeStamp);
        hashMap.put("pUid", hisUid);
        hashMap.put("notification", message);
        hashMap.put("sUid", myUid);


        DatabaseReference Ref = FirebaseDatabase.getInstance().getReference("User");
        Ref.child(hisUid).child("Notifications")
                .child(timeStamp)
                .setValue(hashMap)
                .addOnSuccessListener(aVoid -> Controller.appLogDebug(Const.LOG_DAT, "Add notification success"))
                .addOnFailureListener(e -> Controller.appLogDebug(Const.LOG_DAT, e.toString()));

    }


    public String onGetTimeCurrent() {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("EEEE dd-MMM-yyyy hh:mm:ss a");
        String datetime = dateformat.format(c.getTime());

        return datetime;

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

            popupMenu.setOnMenuItemClickListener(item -> {

                if (item.getItemId() == 0) {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

                    alertDialog.setTitle("Do you want to delete the post ?");

                    alertDialog.setNegativeButton("Yes", (dialog, which) -> {

                        if (pImage.equals("noImage")) {

                            deleteWithoutImage(pIDTime);

                        } else {

                            deleteWithImage(pIDTime, pImage);

                        }


                    });

                    alertDialog.setPositiveButton("No", (dialog, which) -> dialog.dismiss());

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
        picRef.delete().addOnSuccessListener(aVoid -> {

            //delete database

            Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pIDTime").equalTo(pIDTime);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        ds.getRef().removeValue(); // remove values from firebase when pid matches

                    }

                    Controller.showLongToast(context.getResources().getString(R.string.txt_deleted_successfully), context);

                    progressDialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    progressDialog.dismiss();
                    Controller.appLogDebug(Const.LOG_DAT, databaseError.getMessage());
                    Controller.showLongToast(databaseError.getMessage().toString(), context);

                }
            });

        }).addOnFailureListener(e -> {

            progressDialog.dismiss();
            Controller.showLongToast(e.toString(), context);
            Controller.appLogDebug(Const.LOG_DAT, "" + e.toString());

        });

    }

    private void deleteWithoutImage(String pIDTime) {

        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Deleting...");

        //delete database
        Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pIDTime").equalTo(pIDTime);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    ds.getRef().removeValue(); // remove values from firebase when pid matches

                }
                Controller.showLongToast(context.getResources().getString(R.string.txt_deleted_successfully), context);

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
        TextView txt_name_listpost, txt_date_listpost, txt_more_listpost, txt_title_listpost, txt_description_listpost, txt_sumlike_listpost, txt_click_likepost, txt_comment_post, txt_share_post, tv_sumcomment_post;


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
