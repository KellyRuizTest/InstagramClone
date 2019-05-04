package com.example.krruiz.instagramclone.Model;

public class Picture {

    private String date;
    private String image;
    private String pid;
    private String sharedby;
    private String time;

    public Picture(){
        date = "NoDate";
        image = "NoURL";
        pid = "NoID";
        sharedby = "NobodyShared";
        time = "NoTime";

    }

    public Picture(String date, String image, String pid, String sharedby, String time) {
        this.date = date;
        this.image = image;
        this.pid = pid;
        this.sharedby = sharedby;
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getSharedby() {
        return sharedby;
    }

    public void setSharedby(String sharedby) {
        this.sharedby = sharedby;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
