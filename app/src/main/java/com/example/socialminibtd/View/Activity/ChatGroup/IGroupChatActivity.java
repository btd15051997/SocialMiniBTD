package com.example.socialminibtd.View.Activity.ChatGroup;

import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public interface IGroupChatActivity {
    void onMappingViewGroupChat();
    void onGetInfoGroupChat();
    void onGetChatDataGroupChat();
    void onGetMyGroupRole();
    void onSendImageMessage(Uri image_uri);
    void onSendMessageGroupChat(String textMessage);
    String onGetTimeCurrent();
}
