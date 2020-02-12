package com.example.socialminibtd.View.Dialog.ForgotPassword;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.socialminibtd.R;
import com.example.socialminibtd.Utils.Const;
import com.example.socialminibtd.Utils.Controller;
import com.example.socialminibtd.View.Activity.Login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class DialogForgotPassword extends DialogFragment implements View.OnClickListener {

    private LoginActivity mLoginActivity;
    private EditText edt_forgot_email;
    private FirebaseAuth mFirebaseAuth;
    private Button btn_forgot_password;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLoginActivity = (LoginActivity) getActivity();

        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Dialog dialog_forgot = new Dialog(mLoginActivity, R.style.DialogThemeforview);

        dialog_forgot.setContentView(R.layout.layout_dialog_forgot_pass);

        edt_forgot_email = dialog_forgot.findViewById(R.id.edt_forgot_email);

        btn_forgot_password = dialog_forgot.findViewById(R.id.btn_forgot_password);

        btn_forgot_password.setOnClickListener(this);


        return dialog_forgot;


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_forgot_password:

                if (edt_forgot_email.getText().toString().length() == 0
                        || !Patterns.EMAIL_ADDRESS.matcher(edt_forgot_email.getText()).matches()) {

                    edt_forgot_email.setError(getResources().getString(R.string.txt_email_error));
                    edt_forgot_email.setFocusable(true);

                } else {

                    Controller.showSimpleProgressDialog(mLoginActivity
                            , mLoginActivity.getResources().getString(R.string.txt_loading)
                            , false);
                    HandleOnForgotPassword(edt_forgot_email.getText().toString());

                }


                break;
        }

    }

    private void HandleOnForgotPassword(String email) {

        mFirebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    Controller.removeProgressDialog();

                    Toast.makeText(mLoginActivity, "" + mLoginActivity.getResources().getString(R.string.txt_sent_pass_to_email)
                            , Toast.LENGTH_SHORT).show();

                    Controller.appLogDebug(Const.LOG_DAT + " ForgotPassword :", task.toString());

                } else {

                    Controller.removeProgressDialog();

                    Toast.makeText(mLoginActivity, "" + mLoginActivity.getResources().getString(R.string.txt_failed)
                            , Toast.LENGTH_SHORT).show();


                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Controller.removeProgressDialog();

                Toast.makeText(mLoginActivity, "" + e.toString()
                        , Toast.LENGTH_SHORT).show();

                Controller.appLogDebug(Const.LOG_DAT + " ForgotPassword :", e.toString());

            }
        });

    }
}
