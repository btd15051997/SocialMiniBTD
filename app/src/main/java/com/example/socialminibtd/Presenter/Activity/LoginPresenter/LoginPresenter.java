package com.example.socialminibtd.Presenter.Activity.LoginPresenter;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.socialminibtd.R;
import com.example.socialminibtd.Utils.Const;
import com.example.socialminibtd.Utils.Controller;
import com.example.socialminibtd.View.Activity.Login.ILoginActivityView;
import com.example.socialminibtd.View.Activity.Login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginPresenter implements ILoginPresenter {

    private LoginActivity mLoginActivity;
    private ILoginActivityView iLoginActivityView;


    public LoginPresenter(LoginActivity mLoginActivity, ILoginActivityView iLoginActivityView) {
        this.mLoginActivity = mLoginActivity;
        this.iLoginActivityView = iLoginActivityView;
    }

    @Override
    public void onHandleLogin(String email, String pass, FirebaseAuth auth) {

        if (email.isEmpty()) {

            Controller.showLongToast(mLoginActivity.getResources().getString(R.string.txt_email_error)
                    , mLoginActivity);

        } else if (pass.isEmpty()) {

            Controller.showLongToast(mLoginActivity.getResources().getString(R.string.txt_pass_error)
                    , mLoginActivity);

        } else {

            Controller.showSimpleProgressDialog(mLoginActivity
                    , mLoginActivity.getResources().getString(R.string.txt_loading)
                    , false);

            LoginUserSocial(email, pass, auth);

        }


    }

    private void LoginUserSocial(String email, String pass, final FirebaseAuth mAuth) {

        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(mLoginActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();

                            Log.d(Const.LOG_DAT, "loginUserWithEmail:success  " + user.getEmail());

                            iLoginActivityView.onLoginIntentHome();

                            Controller.showLongToast(user.getEmail().toString(), mLoginActivity);

                            Controller.removeProgressDialog();


                        } else {

                            // If sign in fails, display a message to the user.
                            Controller.removeProgressDialog();

                            Controller.appLogDebug(Const.LOG_DAT, "loginUserWithEmail:false  " + task.getException().toString());

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.d(Const.LOG_DAT, "loginUserWithEmail:false  " + e.toString());

                Controller.showLongToast(e.getMessage().toString(), mLoginActivity);

            }
        });

    }
}
