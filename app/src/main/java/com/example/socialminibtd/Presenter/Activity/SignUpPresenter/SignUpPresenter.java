package com.example.socialminibtd.Presenter.Activity.SignUpPresenter;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.socialminibtd.R;
import com.example.socialminibtd.Utils.Const;
import com.example.socialminibtd.Utils.Controller;
import com.example.socialminibtd.View.Activity.Register.IRegisterActivityView;
import com.example.socialminibtd.View.Activity.Register.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpPresenter implements ISignUpPresenter {

    private RegisterActivity registerActivity;
    private IRegisterActivityView iRegisterActivityView;

    public SignUpPresenter(RegisterActivity registerActivity, IRegisterActivityView iRegisterActivityView) {
        this.registerActivity = registerActivity;
        this.iRegisterActivityView = iRegisterActivityView;
    }

    @Override
    public void onHandleSignNormal(String email, String pass, String re_pass, FirebaseAuth auth) {

        if (email.isEmpty()) {

            Controller.showLongToast(registerActivity.getResources().getString(R.string.txt_email_error), registerActivity);

        } else if (pass.isEmpty()) {

            Controller.showLongToast(registerActivity.getResources().getString(R.string.txt_pass_error), registerActivity);

        } else if (re_pass.isEmpty()) {

            Controller.showLongToast(registerActivity.getResources().getString(R.string.txt_re_pass_error), registerActivity);

        } else if (pass.length() < 6) {

            Controller.showLongToast(registerActivity.getResources().getString(R.string.txt_pass_six_character), registerActivity);

        } else if (re_pass.length() < 6) {

            Controller.showLongToast(registerActivity.getResources().getString(R.string.txt_pass_six_character), registerActivity);

        } else {

            if (pass.equals(re_pass)) {

                Controller.showSimpleProgressDialog(registerActivity
                        , registerActivity.getResources().getString(R.string.txt_loading), false);

                SignUpWithEmailPass(email, pass, auth);

            } else {

                Controller.showLongToast(registerActivity.getResources().getString(R.string.txt_pass_confirmpass_not_match), registerActivity);

            }

        }

    }

    private void SignUpWithEmailPass(String email, String password, final FirebaseAuth mAuth) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(registerActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            Log.d(Const.LOG_DAT, "createUserWithEmail:success");

                            Controller.removeProgressDialog();

                            FirebaseUser user = mAuth.getCurrentUser();

                            Log.d(Const.LOG_DAT, "createUserWithEmail:success  " + user.getEmail());

                            Controller.showLongToast(user.getEmail().toString(),registerActivity);

                            iRegisterActivityView.onIntentProfile();

                        } else {

                            Controller.removeProgressDialog();

                            Controller.appLogDebug(Const.LOG_DAT, "createUserWithEmail:false  " + task.getException().toString());

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Controller.removeProgressDialog();

                Log.d(Const.LOG_DAT, "createUserWithEmail:false  " + e.toString());

                Controller.showLongToast(e.getMessage().toString(),registerActivity);

            }
        });
    }
}
