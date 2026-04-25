package com.example.mugangaconnect.data.model;

public class User {
    private String uid;
    private String fullName;
    private String email;
    private String phone;
    private String fcmToken;

    public User() {}

    public User(String uid, String fullName, String email, String phone) {
        this.uid = uid;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
    }

    public String getUid() { return uid; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getFcmToken() { return fcmToken; }

    public void setUid(String uid) { this.uid = uid; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setFcmToken(String fcmToken) { this.fcmToken = fcmToken; }
}
