package com.example.socialminibtd.View.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.socialminibtd.R;
import com.example.socialminibtd.View.Activity.Language.LanguageActivity;

public class WellcomeActivity extends AppCompatActivity {

    private int TIME_PLASH_WELLCOME = 3000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wellcome);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(new Intent(WellcomeActivity.this, LanguageActivity.class));
                finish();

            }
        },TIME_PLASH_WELLCOME);
    }
}
