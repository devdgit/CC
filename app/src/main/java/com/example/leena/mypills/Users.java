package com.example.leena.mypills;

public class Users {

    public String age;
    public String fullname;
    public String points;
    public String profile_img;
    public String username;

    public Users(){

    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getProfile_img() {
        return profile_img;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Users(String age, String fullname, String points, String profile_img, String username) {
        this.age = age;
        this.fullname = fullname;
        this.points = points;
        this.profile_img = profile_img;
        this.username = username;
    }


}
