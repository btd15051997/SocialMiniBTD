package com.example.socialminibtd.Model;

public class ListPost {

    /*Params User*/
    private String uName, uEmail, uDp, uid;

    /*Params Post*/
    private String pTitle, pTime, pImage, pIDTime, pDescription, pLikes, pComments;

    public ListPost(String uName, String uEmail, String uDp, String uid, String pTitle, String pTime, String pImage, String pIDTime, String pDescription, String pLikes, String pComments) {
        this.uName = uName;
        this.uEmail = uEmail;
        this.uDp = uDp;
        this.uid = uid;
        this.pTitle = pTitle;
        this.pTime = pTime;
        this.pImage = pImage;
        this.pIDTime = pIDTime;
        this.pDescription = pDescription;
        this.pLikes = pLikes;
        this.pComments = pComments;
    }

    public ListPost() {
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getuEmail() {
        return uEmail;
    }

    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }

    public String getuDp() {
        return uDp;
    }

    public void setuDp(String uDp) {
        this.uDp = uDp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getpTitle() {
        return pTitle;
    }

    public void setpTitle(String pTitle) {
        this.pTitle = pTitle;
    }

    public String getpTime() {
        return pTime;
    }

    public void setpTime(String pTime) {
        this.pTime = pTime;
    }

    public String getpImage() {
        return pImage;
    }

    public void setpImage(String pImage) {
        this.pImage = pImage;
    }

    public String getpIDTime() {
        return pIDTime;
    }

    public void setpIDTime(String pIDTime) {
        this.pIDTime = pIDTime;
    }

    public String getpDescription() {
        return pDescription;
    }

    public void setpDescription(String pDescription) {
        this.pDescription = pDescription;
    }

    public String getpLikes() {
        return pLikes;
    }

    public void setpLikes(String pLikes) {
        this.pLikes = pLikes;
    }

    public String getpComments() {
        return pComments;
    }

    public void setpComments(String pComments) {
        this.pComments = pComments;
    }
}
