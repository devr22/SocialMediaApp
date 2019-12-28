package com.example.socialmediaapp.model;

public class model_user {

    String name, email, phone, profilePhoto, coverPhoto, search, uid;

    public model_user(){

    }

    public model_user(String name, String email, String phone, String profilePhoto, String coverPhoto, String search, String uid) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.profilePhoto = profilePhoto;
        this.coverPhoto = coverPhoto;
        this.search = search;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(String coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
