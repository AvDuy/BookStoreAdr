package com.example.bookstore;

import com.google.firebase.firestore.PropertyName;

public class user_infoModel {
    @PropertyName("Avatar")
    private String avatar;
    @PropertyName("Name")
    private String name;
    @PropertyName("Gender")
    private boolean gender;
    @PropertyName("Date of birth")
    private String dob;
    @PropertyName("Email")
    private String email;
    @PropertyName("Phone")
    private String phone;

    public user_infoModel(String avatar, String name, boolean gender, String dob, String email, String phone) {
        this.avatar = avatar;
        this.name = name;
        this.gender = gender;
        this.dob = dob;
        this.email = email;
        this.phone = phone;
    }

    public user_infoModel() {
    }

    @PropertyName("Name")
    public String getName() {
        return name;
    }
    @PropertyName("Name")
    public void setName(String name) {
        this.name = name;
    }
    @PropertyName("Gender")
    public boolean isGender() {
        return gender;
    }
    @PropertyName("Gender")
    public void setGender(boolean gender) {
        this.gender = gender;
    }
    @PropertyName("Date of birth")
    public String getDob() {
        return dob;
    }
    @PropertyName("Date of birth")
    public void setDob(String dob) {
        this.dob = dob;
    }

    @PropertyName("Email")
    public String getEmail() {
        return email;
    }
    @PropertyName("Email")
    public void setEmail(String email) {
        this.email = email;
    }
    @PropertyName("Phone")
    public String getPhone() {
        return phone;
    }
    @PropertyName("Phone")
    public void setPhone(String phone) {
        this.phone = phone;
    }
    @PropertyName("Avatar")
    public String getAvatar() {
        return avatar;
    }
    @PropertyName("Avatar")
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
