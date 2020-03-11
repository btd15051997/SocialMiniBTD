package com.example.socialminibtd.Notifications.ModelNotifi;

public class Token {

    /*An FCM Token, or much commonly know as a registrationToken, An ID issued by the GCM
    * connection servers to the client app that allows it to receive message*/
    String token;

    public Token(String token) {
        this.token = token;
    }

    public Token() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
