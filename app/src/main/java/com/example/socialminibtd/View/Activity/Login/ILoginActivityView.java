package com.example.socialminibtd.View.Activity.Login;

import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public interface ILoginActivityView {
    void onMappingView();
    void onConfigSignGoogle();
    void onIntentRegister();
    void onGoneLayoutLogin();
    void onLoginIntentHome();
    void onAddFragment(Fragment fragment, boolean addToBackStack, boolean TransactionAdd, String Tag, boolean isAnimate);
}
