package com.example.lfgapp;

import android.net.Uri;

import java.lang.reflect.GenericArrayType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User {

    private String Email;
    private String Name;
    private String GamerTag;
    private Uri PhotoUri;
    private String UID;
    private ArrayList<String> friends;
    private String GamePlaying;

    public User(){

    }
    public User(String UID,String Email,String Name,Uri PhotoUri,String GamerTag){
        this.Email=Email;
        this.Name=Name;
        this.PhotoUri=PhotoUri;
        this.UID=UID;
        this.friends=new ArrayList<String>();
        this.GamePlaying="";
        this.GamerTag=GamerTag;
    }

    public String getEmail() {
        return Email;
    }

    public String getName() {
        return Name;
    }

    public String getUID() {
        return UID;
    }

    public Uri getPhotoUri() {
        return PhotoUri;
    }

    public String getGamePlaying() {
        return GamePlaying;
    }

    public String getGamerTag(){return GamerTag;}

    public ArrayList<String> getFriends() { return friends;}

    public void setGamePlaying(String gamePlaying) {
        GamePlaying = gamePlaying;
    }

    public void setFriends(ArrayList<String> friends) {
        this.friends = friends;
    }

    public void setEmail(String email){this.Email=email;}

    public void setName(String name){this.Name=name;}

    public void setGamerTag(String gamerTag){this.GamerTag=gamerTag;}

    public void setPhotoUri(Uri uri){this.PhotoUri=uri;}

    public void setUID(String uid){this.UID=uid;}

    public ArrayList<String> filterFriendsString(ArrayList<SimpleUser> players){
        ArrayList<String> answer = new ArrayList<String>();
        for(SimpleUser player:players){
            if(this.isFriend(player)||player.getUID().equals(this.UID)){
                answer.add(player.getGamerTag());
            }
        }
        return answer;
    }

    public boolean isFriend(SimpleUser user){
        for(String friend:friends){
            if(friend.equals(user.getUID())){
                return true;
            }
        }

        return false;
    }

    public boolean in(ArrayList<SimpleUser> users){
        for(SimpleUser user:users){
            if(user.getUID().equals(this.UID)){
                return true;
            }
        }
        return false;
    }

    public Map<String,String> toMap(){
        Map<String,String> map = new HashMap<String, String>();
        map.put("GamerTag",GamerTag);
        map.put("UID",UID);
    return map;
    }

//    public void copy(SimpleUser user){
//        this.UID =
//    }
}
