package com.example.socialminibtd.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.socialminibtd.R;

public class SpinnerLanguageAdapter extends ArrayAdapter<String> {

    private Context SP_context;
    private String[] stringArray;
    private Integer[] imgArray;

    public SpinnerLanguageAdapter(Context context, int resource, String[] stringArray, Integer[] imgArray) {
        super(context, resource, R.id.txt_flag_name, stringArray);
        this.SP_context = context;
        this.stringArray = stringArray;
        this.imgArray = imgArray;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) SP_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_spinner_reg, parent, false);

        TextView txt_flag_name = view.findViewById(R.id.txt_flag_name);
        txt_flag_name.setText(stringArray[position]);

        ImageView img_flag = view.findViewById(R.id.img_flag);


        if (imgArray[position] != null) {

            img_flag.setImageResource(imgArray[position]);

        } else {

            img_flag.setVisibility(View.GONE);

        }

        return view;
    }
}