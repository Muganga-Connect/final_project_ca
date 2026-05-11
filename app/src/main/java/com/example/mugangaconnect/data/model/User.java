package com.example.mugangaconnect.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey
    @NonNull
    public String uid;
    
    public String fullName;
    public String email;
    public String phone;
    public String fcmToken;
    public String dob;
    public String gender;
    public String bloodType;
    public String insuranceId;
    public String allergies;
    public String emergencyContact;
    public String profileImageUrl;
    public String weight;
    public String height;
    public long updatedAt;

    public User() {
        uid = "";
    }

    public User(@NonNull String uid, String fullName, String email, String phone) {
        this.uid = uid;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.updatedAt = System.currentTimeMillis();
    }

    // Getters
    @NonNull
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
    public String getProfileImageUrl() { return profileImageUrl; }
    public String getWeight() { return weight; }
    public String getHeight() { return height; }

    // Setters
    public void setUid(@NonNull String uid) { this.uid = uid; }
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
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
    public void setWeight(String weight) { this.weight = weight; }
    public void setHeight(String height) { this.height = height; }
}
