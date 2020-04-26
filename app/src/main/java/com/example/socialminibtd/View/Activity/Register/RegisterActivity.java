package com.example.socialminibtd.View.Activity.Register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.socialminibtd.Presenter.Activity.SignUpPresenter.SignUpPresenter;
import com.example.socialminibtd.R;
import com.example.socialminibtd.Utils.Controller;
import com.example.socialminibtd.View.Activity.Dashbroad.DashboardActivity;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity implements IRegisterActivityView, View.OnClickListener {


    private EditText edt_login_email, edt_signup_pass, edt_signup_re_pass, edt_signup_name, edt_signup_phone;
    private Button btn_signup_confirm;
    private SignUpPresenter mSignUpPresenter;
    private ImageView img_male_female,img_register_header;
    private FirebaseAuth mAuth;
    private RadioButton radio_men, radio_girl;
    private String FeOrMale ="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_bottom);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        onMappingView();

        onStartAnimation();

    }

    private void onStartAnimation() {

        img_male_female.setAnimation(Controller.onShowAnimationBlink(getApplicationContext()));
        img_register_header.setAnimation(Controller.onShowAnimationBounce(getApplicationContext()));


    }

    @Override
    public void onMappingView() {

        edt_login_email = findViewById(R.id.edt_signup_email);
        edt_signup_pass = findViewById(R.id.edt_signup_pass);
        edt_signup_re_pass = findViewById(R.id.edt_signup_re_pass);
        edt_signup_name = findViewById(R.id.edt_signup_name);
        edt_signup_phone = findViewById(R.id.edt_signup_phone);
        btn_signup_confirm = findViewById(R.id.btn_signup_confirm);
        img_male_female = findViewById(R.id.img_male_female);
        img_register_header = findViewById(R.id.img_register_header);

        radio_men = findViewById(R.id.radio_men);
        radio_girl = findViewById(R.id.radio_girl);


        radio_men.setOnCheckedChangeListener(listenerRadio);
        radio_girl.setOnCheckedChangeListener(listenerRadio);

        mSignUpPresenter = new SignUpPresenter(RegisterActivity.this, this);

        btn_signup_confirm.setOnClickListener(this);


    }

    CompoundButton.OnCheckedChangeListener listenerRadio
            = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            if (b) {

                FeOrMale = compoundButton.getText().toString().trim();

            }
        }
    };

    @Override
    public void onIntentProfile() {

        startActivity(new Intent(RegisterActivity.this, DashboardActivity.class));
        finish();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_signup_confirm:

                mSignUpPresenter.onHandleSignNormal(edt_login_email.getText().toString().trim()
                        , edt_signup_pass.getText().toString().trim()
                        , edt_signup_name.getText().toString().trim()
                        , edt_signup_phone.getText().toString().trim()
                        , edt_signup_re_pass.getText().toString().trim()
                        ,FeOrMale
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

