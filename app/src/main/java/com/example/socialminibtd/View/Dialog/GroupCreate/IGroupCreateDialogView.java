package com.example.socialminibtd.View.Dialog.GroupCreate;

public interface IGroupCreateDialogView {
    void onMappingViewDialog();
    void onShowPickupDialog();
    void onStartCreateGroup();
    void onResetTextAndImage();
    String onGetTimeCurrent();
    void onCreateGroup(String title,String descrip,String timeCreate,String iconGroup);
}
