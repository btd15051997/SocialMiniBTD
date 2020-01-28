package com.example.socialminibtd.Presenter.Activity.SignUpPresenter;

import com.google.firebase.auth.FirebaseAuth;

public interface ISignUpPresenter {
    void onHandleSignNormal(String email, String pass, String re_pass, FirebaseAuth auth);
}
