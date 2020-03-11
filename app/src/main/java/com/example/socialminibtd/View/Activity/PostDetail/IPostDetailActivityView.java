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
    void onDeleteWithoutImage(String postId);
    void onDeleteWithImage(String postId,String pImage);
    void onUpdateCommentCount();
    String onGetTimeCurrent();
}
