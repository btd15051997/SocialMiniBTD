package com.example.socialminibtd.Model;

public class ListPost {

    /*Params User*/
    private String uName, uEmail, uDp, uid;

    /*Params Post*/
    private String uTitle, uTime, uImage, uIDTime, uDescription, uLikes, pComments;

    public ListPost(String uName, String uEmail, String uDp, String uid, String uTitle, String uTime, String uImage, String uIDTime, String uDescription, String uLikes, String pComments) {
        this.uName = uName;
        this.uEmail = uEmail;
        this.uDp = uDp;
        this.uid = uid;
        this.uTitle = uTitle;
        this.uTime = uTime;
        this.uImage = uImage;
        this.uIDTime = uIDTime;
        this.uDescription = uDescription;
        this.uLikes = uLikes;
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

    public String getuTitle() {
        return uTitle;
    }

    public void setuTitle(String uTitle) {
        this.uTitle = uTitle;
    }

    public String getuTime() {
        return uTime;
    }

    public void setuTime(String uTime) {
        this.uTime = uTime;
    }

    public String getuImage() {
        return uImage;
    }

    public void setuImage(String uImage) {
        this.uImage = uImage;
    }

    public String getuIDTime() {
        return uIDTime;
    }

    public void setuIDTime(String uIDTime) {
        this.uIDTime = uIDTime;
    }

    public String getuDescription() {
        return uDescription;
    }

    public void setuDescription(String uDescription) {
        this.uDescription = uDescription;
    }

    public String getuLikes() {
        return uLikes;
    }

    public void setuLikes(String uLikes) {
        this.uLikes = uLikes;
    }

    public String getpComments() {
        return pComments;
    }

    public void setpComments(String pComments) {
        this.pComments = pComments;
    }
}
