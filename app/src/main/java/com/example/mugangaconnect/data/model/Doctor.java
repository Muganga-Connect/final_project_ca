package com.example.mugangaconnect.data.model;

public class Doctor {
    private String id;
    private String name;
    private String specialty;
    private String department;
    private String availability;  // e.g. "Mon-Fri 08:00-17:00"
    private String photoUrl;
    private float rating;

    public Doctor() {}

    public Doctor(String id, String name, String specialty, String department, String availability) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
        this.department = department;
        this.availability = availability;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getSpecialty() { return specialty; }
    public String getDepartment() { return department; }
    public String getAvailability() { return availability; }
    public String getPhotoUrl() { return photoUrl; }
    public float getRating() { return rating; }

    public void setId(String id) { this.id = id; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public void setRating(float rating) { this.rating = rating; }
}
