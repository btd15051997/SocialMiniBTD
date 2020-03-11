package com.example.socialminibtd.View.Activity.ChatUser;

import android.net.Uri;

public interface IChatActivityView {
    void onMappingView();
    void onFindUidUser(String uid);
    void onSendMessageChat(String message);
    void onReadAndShowlistChat();
    void onSeenMessagetChat();
    void onShowPickupDialog();
    void onSendImageMessage(Uri image_uri);
    void onCheckOnlineStatus(String status);
    void onCheckTypingTo(String typingTo);
    String onGetTimeCurrent();
    void sendNotification(String hisUid,String nameSender,String message);
}
