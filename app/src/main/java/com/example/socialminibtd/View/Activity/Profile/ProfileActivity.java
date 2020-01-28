package com.example.socialminibtd.View.Activity.Profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.socialminibtd.R;
import com.example.socialminibtd.View.Activity.Login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity implements IProfileActivityView, View.OnClickListener {


    private FirebaseAuth firebaseAuth;
    private ImageView img_profile_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        onMappingView();
    }

    @Override
    public void onMappingView() {

        img_profile_logout = findViewById(R.id.img_profile_logout);
        img_profile_logout.setOnClickListener(this);

    }

    @Override
    public void onCheckUserCurrent() {

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {


        } else {

            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();

        }

    }

    @Override
    protected void onStart() {

        onCheckUserCurrent();

        super.onStart();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.img_profile_logout:

                firebaseAuth.signOut();
                onCheckUserCurrent();

                break;

        }
    }
}
