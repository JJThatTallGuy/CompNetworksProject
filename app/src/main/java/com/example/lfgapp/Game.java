package com.example.lfgapp;

import java.util.ArrayList;

public class Game {
    private String Name;
    private ArrayList<SimpleUser> Looking;
    private ArrayList<SimpleUser> Playing;

    public Game(){

    }

    public Game(String name){
        this.Name = name;
        this.Looking = new ArrayList<SimpleUser>();
        this.Playing = new ArrayList<SimpleUser>();
        this.Looking.add(new SimpleUser("000000000","Test"));
        this.Playing.add(new SimpleUser("000000000","Test"));
    }

    public Game(String name, ArrayList<SimpleUser> looking, ArrayList<SimpleUser> playing){
        this.Name = name;
        this.Looking = looking;
        this.Playing = playing;
    }


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public ArrayList<SimpleUser> getLooking() {
        return Looking;
    }

    public void setLooking(ArrayList<SimpleUser> looking) {
        Looking = looking;
    }

    public ArrayList<SimpleUser> getPlaying() {
        return Playing;
    }

    public void setPlaying(ArrayList<SimpleUser> playing) {
        Playing = playing;
    }

    public void RemovePlaying(User user){
        for(SimpleUser Suser: this.Playing){
            if(Suser.getUID().equals(user.getUID())){
                this.Playing.remove(Suser);
            }
        }
    }
    public void RemoveLooking(User user){
        for(SimpleUser Suser: this.Looking){
            if(Suser.getUID().equals(user.getUID())){
                this.Looking.remove(Suser);
            }
        }
    }
}
