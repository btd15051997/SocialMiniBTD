package com.example.socialminibtd.View.Activity.PostDetail;

public interface IPostDetailActivityView {
    void onMappingViewPostDetail();
    void onLoadPostInfo();
    void onCheckUserCurrent();
    void onLoadUserInfo();
    void onPostComment();
    void onLoadAllComment();
    void onPostLike();
    void onSetLikePost();
    void onMoreSettingPost();
    void onShowDialogPostLikedBy();
    void onDeleteWithoutImage(String postId);
    void onDeleteWithImage(String postId,String pImage);
    void onUpdateCommentCount();
    void onAddHistoryNotification(String hisUid, String pId, String message);
    String onGetTimeCurrent();
}
