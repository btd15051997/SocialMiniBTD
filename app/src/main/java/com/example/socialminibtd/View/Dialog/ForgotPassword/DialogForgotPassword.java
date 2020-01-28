package com.example.socialminibtd.View.Dialog.ForgotPassword;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.socialminibtd.R;
import com.example.socialminibtd.View.Activity.Login.LoginActivity;

public class DialogForgotPassword extends DialogFragment {

    private LoginActivity mLoginActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLoginActivity = (LoginActivity) getActivity();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Dialog dialog_forgot = new Dialog(mLoginActivity, R.style.DialogThemeforview);

        dialog_forgot.setContentView(R.layout.layout_dialog_forgot_pass);

        return dialog_forgot;


    }
}
