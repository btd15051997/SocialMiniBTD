package com.example.socialminibtd.Presenter.Activity.LoginPresenter;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public interface ILoginPresenter {
    void onHandleLogin(String email, String pass, FirebaseAuth auth);
    void onfirebaseAuthWithGoogle(GoogleSignInAccount account, FirebaseAuth mAuth);
    void onPutAuthToRealTimeDatabase(FirebaseUser user);
}
