package com.example.lfgapp;

public class SimpleUser {
    private String Email;
    private String GamerTag;
    private String UID;

    public SimpleUser(){

    }
    public SimpleUser(String GamerTag, String UID){
        this.UID = UID;
        this.GamerTag = GamerTag;
    }

    public SimpleUser(User user){
        this.UID = user.getUID();
        this.GamerTag = user.getGamerTag();
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getGamerTag() {
        return GamerTag;
    }

    public void setGamerTag(String gamerTag) {
        GamerTag = gamerTag;
    }
}
