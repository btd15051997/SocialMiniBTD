package com.example.socialminibtd.Model;

public class ChatList {

    String id; // we need this id to get chatlist sender,receiver uid

    public ChatList(String id) {
        this.id = id;
    }

    public ChatList() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
