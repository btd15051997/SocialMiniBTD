package com.example.socialminibtd.Presenter.Activity.LoginPresenter;

import com.google.firebase.auth.FirebaseAuth;

public interface ILoginPresenter {
    void onHandleLogin(String email, String pass, FirebaseAuth auth);
}
