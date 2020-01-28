package com.example.socialminibtd.View.Activity.Login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.socialminibtd.Presenter.Activity.LoginPresenter.LoginPresenter;
import com.example.socialminibtd.R;
import com.example.socialminibtd.View.Activity.Home.HomeActivity;
import com.example.socialminibtd.View.Activity.Profile.ProfileActivity;
import com.example.socialminibtd.View.Activity.Register.RegisterActivity;
import com.example.socialminibtd.View.Dialog.ForgotPassword.DialogForgotPassword;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements ILoginActivityView, View.OnClickListener {


    private EditText edt_login_email, edt_login_pass;
    private TextView txt_register, txt_forgot_pass;
    private Button btn_login_confirm;
    private LoginPresenter mLoginPresenter;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        onMappingView();

    }

    @Override
    public void onMappingView() {

        edt_login_email = findViewById(R.id.edt_login_email);
        btn_login_confirm = findViewById(R.id.btn_login_confirm);
        edt_login_pass = findViewById(R.id.edt_login_pass);
        txt_register = findViewById(R.id.txt_register);
        txt_forgot_pass = findViewById(R.id.txt_forgot_pass);

        txt_register.setOnClickListener(this);
        btn_login_confirm.setOnClickListener(this);
        txt_forgot_pass.setOnClickListener(this);

        mLoginPresenter = new LoginPresenter(LoginActivity.this, this);

    }

    @Override
    public void onIntentRegister() {

        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }


    @Override
    public void onGoneLayoutLogin() {


    }

    @Override
    public void onLoginIntentHome() {

        startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
        finish();
    }

    @Override
    public void onAddFragment(Fragment fragment, boolean addToBackStack, boolean TransactionAdd, String Tag, boolean isAnimate) {


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.txt_register:

                onIntentRegister();

                break;

            case R.id.btn_login_confirm:

                if (edt_login_email.getText().length() == 0
                        || !Patterns.EMAIL_ADDRESS.matcher(edt_login_email.getText()).matches()) {

                    edt_login_email.setError(getResources().getString(R.string.txt_email_error));
                    edt_login_email.setFocusable(true);

                } else if (edt_login_email.getText().length() == 0) {

                    edt_login_pass.setError(getResources().getString(R.string.txt_pass_error));
                    edt_login_pass.setFocusable(true);

                } else {

                    mLoginPresenter.onHandleLogin(edt_login_email.getText().toString().trim()
                            , edt_login_pass.getText().toString().trim()
                            , mAuth);

                }

                break;

            case R.id.txt_forgot_pass:

                DialogForgotPassword dialogOTPFragment = new DialogForgotPassword();
                dialogOTPFragment.setCancelable(false);
                dialogOTPFragment.show(getSupportFragmentManager(), "DialogForgotPassword");

                break;

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
