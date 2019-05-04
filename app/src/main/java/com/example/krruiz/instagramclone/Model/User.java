package com.example.krruiz.instagramclone.Model;

public class User {

    private String username;
    private String password;

    public User(){

        username = "QWERTY";
        password = "qwerty";

    }

    public User(String user, String pass){
        username = user;
        password = pass;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
