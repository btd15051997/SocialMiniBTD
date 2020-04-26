package com.example.socialminibtd.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.socialminibtd.Model.ModelListGroup;
import com.example.socialminibtd.Model.ModelNews;
import com.example.socialminibtd.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NewsDayAdapter extends RecyclerView.Adapter<NewsDayAdapter.ViewHodelNews> {

    private ArrayList<ModelNews> arrayList;
    private Context context;

    public NewsDayAdapter(ArrayList<ModelNews> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHodelNews onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_list_newsday, parent, false);
        return new ViewHodelNews(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHodelNews holder, int position) {

        String nameNews = arrayList.get(position).getName();

        int imageNew = arrayList.get(position).getImg_news();

        holder.img_news_day_item.setImageResource(imageNew);
        holder.txt_news_day_item.setText(nameNews);


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHodelNews extends RecyclerView.ViewHolder {

        private ImageView img_news_day_item;
        private TextView txt_news_day_item;

        public ViewHodelNews(@NonNull View itemView) {
            super(itemView);

            txt_news_day_item = itemView.findViewById(R.id.txt_news_day_item);
            img_news_day_item = itemView.findViewById(R.id.img_news_day_item);
        }
    }
}

