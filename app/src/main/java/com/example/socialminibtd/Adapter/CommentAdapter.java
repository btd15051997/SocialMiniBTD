package com.example.socialminibtd.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.socialminibtd.Model.ListComment;
import com.example.socialminibtd.R;
import com.example.socialminibtd.Utils.Controller;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private ArrayList<ListComment> commentArrayList;
    private Context context;
    private String myUid;
    private String postID;

    public CommentAdapter(ArrayList<ListComment> commentArrayList, Context context, String myUid, String postID) {
        this.commentArrayList = commentArrayList;
        this.context = context;
        this.myUid = myUid;
        this.postID = postID;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_list_comment, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final String cId = commentArrayList.get(position).getcId();
        String cName = commentArrayList.get(position).getuName();
        String cComment = commentArrayList.get(position).getComment();
        String cDate = commentArrayList.get(position).getTimestamp();
        String cDp = commentArrayList.get(position).getuDp();
        final String uid = commentArrayList.get(position).getUid();


        holder.txt_name_commenter.setText(cName);
        holder.txt_text_commenter.setText(cComment);
        holder.txt_date_commenter.setText(cDate);

        try {

            Picasso.get().load(cDp).placeholder(R.drawable.ic_account).into(holder.img_item_comment);

        } catch (Exception e) {

            Picasso.get().load(R.drawable.ic_account).into(holder.img_item_comment);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (myUid.equals(uid)) {
                    //my comment

                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
                    builder.setTitle("Are you sure detele this comment?");
                    builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();

                        }
                    });
                    builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            onDeleteComment(cId);

                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                } else {

                    Controller.showLongToast("Can't delete other's comment...", context);

                }

            }
        });


    }

    private void onDeleteComment(String cID) {

        final DatabaseReference Ref = FirebaseDatabase.getInstance().getReference("Posts").child(postID);
        Ref.child("Comments").child(cID).removeValue(); // it will delete the comment

        //now update the comment count
        Ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String comment = "" + dataSnapshot.child("pComments").getValue();
                int newComment = Integer.parseInt(comment) - 1;
                Ref.child("pComments").setValue("" + newComment);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Controller.showLongToast(databaseError.getMessage(), context);

            }
        });


    }

    @Override
    public int getItemCount() {
        return commentArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircularImageView img_item_comment;
        private TextView txt_name_commenter, txt_text_commenter, txt_date_commenter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img_item_comment = itemView.findViewById(R.id.img_item_comment);
            txt_name_commenter = itemView.findViewById(R.id.txt_name_commenter);
            txt_text_commenter = itemView.findViewById(R.id.txt_text_commenter);
            txt_date_commenter = itemView.findViewById(R.id.txt_date_commenter);

        }
    }
}
