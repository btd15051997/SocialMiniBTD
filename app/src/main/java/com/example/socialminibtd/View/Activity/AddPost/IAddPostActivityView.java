package com.example.socialminibtd.View.Activity.AddPost;

import org.json.JSONObject;

public interface IAddPostActivityView {
    void onMappingView();
    void onInitFirebase();
    void onShowPickupDialog();
    void onGetImageName();
    void onLoadPostData(String editPostIdD);
    void onPrepareNotificaion(String pId,String title,String dscription,String notificationType,String notificationTopic);

    void onUpdateWasWithImage(String title, String description, String editPostId);
    void onUpdateWithNowImage(String title, String description, String editPostId);
    void onUpdateWithoutImage(String title, String description, String editPostId);

    void onSendPostNotification(JSONObject notificationJo);

    void onUploadDataPost(String title, String description);
    void onBeginUpdatePost(String title, String description, String editPostId);
    String onGetTimeCurrent();
    void onResetTextImage();
    byte[] onImageToArrayByte();
}
