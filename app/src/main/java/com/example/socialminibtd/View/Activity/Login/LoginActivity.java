package com.example.socialminibtd.View.Activity.Login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.socialminibtd.Presenter.Activity.LoginPresenter.LoginPresenter;
import com.example.socialminibtd.R;
import com.example.socialminibtd.Utils.Const;
import com.example.socialminibtd.Utils.Controller;
import com.example.socialminibtd.View.Activity.Dashbroad.DashboardActivity;
import com.example.socialminibtd.View.Activity.Register.RegisterActivity;
import com.example.socialminibtd.View.Dialog.ForgotPassword.DialogForgotPassword;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements ILoginActivityView, View.OnClickListener {


    private static final int RC_SIGN_IN = 100;
    private EditText edt_login_email, edt_login_pass;
    private TextView txt_register, txt_forgot_pass;
    private Button btn_login_confirm;
    private LoginPresenter mLoginPresenter;
    private FirebaseAuth mAuth;
    private SignInButton btn_google_login;
    private GoogleSignInClient mGoogleSignInClient;
    private CircularImageView img_login_header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        onMappingView();
        onConfigSignGoogle();

    }

    @Override
    public void onMappingView() {

        edt_login_email = findViewById(R.id.edt_login_email);
        btn_login_confirm = findViewById(R.id.btn_login_confirm);
        edt_login_pass = findViewById(R.id.edt_login_pass);
        txt_register = findViewById(R.id.txt_register);
        txt_forgot_pass = findViewById(R.id.txt_forgot_pass);
        btn_google_login = findViewById(R.id.btn_google_login);
        img_login_header = findViewById(R.id.img_login_header);

        img_login_header.setAnimation(Controller.onShowAnimationBounce(getApplicationContext()));


        btn_google_login.setOnClickListener(this);
        txt_register.setOnClickListener(this);
        btn_login_confirm.setOnClickListener(this);
        txt_forgot_pass.setOnClickListener(this);

        mLoginPresenter = new LoginPresenter(LoginActivity.this, this);

    }

    @Override
    public void onConfigSignGoogle() {

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


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

        startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
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
                dialogOTPFragment.setCancelable(true);
                dialogOTPFragment.show(getSupportFragmentManager(), "DialogForgotPassword");

                break;

            case R.id.btn_google_login:

                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);

                break;

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                mLoginPresenter.onfirebaseAuthWithGoogle(account, mAuth);

            } catch (ApiException e) {

                // Google Sign In failed, update UI appropriately
                Controller.appLogDebug(Const.LOG_DAT + " Google Sign In failed :", e.toString());

            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {

            onLoginIntentHome();

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();



    }
}
