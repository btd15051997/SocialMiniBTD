package com.example.socialminibtd.View.Activity.Login;

import androidx.fragment.app.Fragment;

public interface ILoginActivityView {
    void onMappingView();
    void onIntentRegister();
    void onGoneLayoutLogin();
    void onLoginIntentHome();
    void onAddFragment(Fragment fragment, boolean addToBackStack, boolean TransactionAdd, String Tag, boolean isAnimate);
}
