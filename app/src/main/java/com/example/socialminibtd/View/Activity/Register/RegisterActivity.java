package com.example.socialminibtd.View.Activity.Register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.socialminibtd.Presenter.Activity.SignUpPresenter.SignUpPresenter;
import com.example.socialminibtd.R;
import com.example.socialminibtd.Utils.Controller;
import com.example.socialminibtd.View.Activity.Profile.ProfileActivity;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity implements IRegisterActivityView, View.OnClickListener {


    private EditText edt_login_email, edt_signup_pass, edt_signup_re_pass;
    private Button btn_signup_confirm;
    private SignUpPresenter mSignUpPresenter;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        onMappingView();

    }

    @Override
    public void onMappingView() {

        edt_login_email = findViewById(R.id.edt_signup_email);
        edt_signup_pass = findViewById(R.id.edt_signup_pass);
        edt_signup_re_pass = findViewById(R.id.edt_signup_re_pass);
        btn_signup_confirm = findViewById(R.id.btn_signup_confirm);
        mSignUpPresenter = new SignUpPresenter(RegisterActivity.this, this);

        btn_signup_confirm.setOnClickListener(this);


    }

    @Override
    public void onIntentProfile() {

        startActivity(new Intent(RegisterActivity.this, ProfileActivity.class));
        finish();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_signup_confirm:


                mSignUpPresenter.onHandleSignNormal(edt_login_email.getText().toString().trim()
                        , edt_signup_pass.getText().toString().trim()
                        , edt_signup_re_pass.getText().toString().trim()
                        , mAuth);

                break;

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

