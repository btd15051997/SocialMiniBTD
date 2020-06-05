package com.example.socialminibtd.View.Activity.Dashbroad;

import androidx.fragment.app.Fragment;

public interface IDashboardActivityView {
    void onMappingView();
    void onCheckUserCurrent();
    void onCheckOnlineStatus(String status);
    void onAddFragment(Fragment fragment, boolean addToBackStack, boolean TransactionAdd, String Tag, boolean isAnimate);
    void onUpdateToken(String token);
    void onIntentAddPost();
}
