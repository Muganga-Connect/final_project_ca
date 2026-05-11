package com.example.mugangaconnect.data.model;

public class Doctor {
    private String id;
    private String name;
    private String specialty;
    private String availability;
    private String imageUrl;

    public Doctor() {}

    public Doctor(String id, String name, String specialty, String availability, String imageUrl) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
        this.availability = availability;
        this.imageUrl = imageUrl;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public String getAvailability() { return availability; }
    public void setAvailability(String availability) { this.availability = availability; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
