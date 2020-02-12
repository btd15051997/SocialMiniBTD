package com.example.socialminibtd.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialminibtd.Model.ListUser;
import com.example.socialminibtd.R;

import java.util.ArrayList;

public class ListUserAdapter extends RecyclerView.Adapter<ListUserAdapter.ViewHolder> {

    private ArrayList<ListUser> arrayList;
    private Context mContext;

    public ListUserAdapter(ArrayList<ListUser> arrayList, Context mContext) {
        this.arrayList = arrayList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_list_user, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView img_avt_listuser;
        ImageView txt_name_listuser, txt_email_listuser, txt_phone_listuser;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img_avt_listuser = itemView.findViewById(R.id.img_avt_listuser);
            txt_name_listuser = itemView.findViewById(R.id.txt_name_listuser);
            txt_email_listuser = itemView.findViewById(R.id.txt_email_listuser);
            txt_phone_listuser = itemView.findViewById(R.id.txt_phone_listuser);

        }
    }
}
