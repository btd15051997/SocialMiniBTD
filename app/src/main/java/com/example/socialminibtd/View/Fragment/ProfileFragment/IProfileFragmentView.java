package com.example.socialminibtd.View.Fragment.ProfileFragment;

import android.net.Uri;

public interface IProfileFragmentView {
    void onMappingView();
    void onInitFirebase();
    void onGetDataShowProfile();
    void onShowEditTextProfile();
    void onShowUIProfile(String name,String email,String phone,String image,String image_cover);
    void onUploadConverPhotoProfile(Uri image_uri);
    void onUpdateNamePhoneProfile(String name);
    void onShowProgressBarLoading();
    void onRemoveProgressBarLoading();
}
