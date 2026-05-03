package com.example.mugangaconnect.data.model;

public class User {
    private String uid;
    private String fullName;
    private String email;
    private String phone;
    private String fcmToken;
    private String dob;
    private String gender;
    private String bloodType;
    private String insuranceId;
    private String allergies;
    private String emergencyContact;

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
    public String getDob() { return dob; }
    public String getGender() { return gender; }
    public String getBloodType() { return bloodType; }
    public String getInsuranceId() { return insuranceId; }
    public String getAllergies() { return allergies; }
    public String getEmergencyContact() { return emergencyContact; }

    public void setUid(String uid) { this.uid = uid; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setFcmToken(String fcmToken) { this.fcmToken = fcmToken; }
    public void setDob(String dob) { this.dob = dob; }
    public void setGender(String gender) { this.gender = gender; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }
    public void setInsuranceId(String insuranceId) { this.insuranceId = insuranceId; }
    public void setAllergies(String allergies) { this.allergies = allergies; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }

    @Override
    public String toString() {
        return "User{uid='" + uid + "', fullName='" + (fullName == null ? "" : fullName) + "'}";
    }
}
