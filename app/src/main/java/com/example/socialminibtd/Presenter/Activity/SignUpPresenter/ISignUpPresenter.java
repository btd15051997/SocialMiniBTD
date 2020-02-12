package com.example.socialminibtd.Presenter.Activity.SignUpPresenter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public interface ISignUpPresenter {
    void onHandleSignNormal(String email, String pass, String name, String phone, String re_pass, FirebaseAuth auth);

    void onPutAuthToRealTimeDatabase(FirebaseUser user,String name,String phone);
}
