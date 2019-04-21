package com.example.leena.mypills;

/**
 * Created by leena on 18-06-2018.
 */

public class Posts {
    public String time;
    public String date;
    public String imageURL;
    public String userid;
    public String description;
    public String fullname;

    public Posts(){

    }

    public Posts(String imageURL, String userid, String fullname, String description) {
        this.time = time;
        this.date = date;
        this.imageURL = imageURL;
        this.userid = userid;
        this.fullname = fullname;
        this.description = description;
    }



    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
