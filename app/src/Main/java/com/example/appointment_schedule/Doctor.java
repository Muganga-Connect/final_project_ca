package com.example.appointment_schedule;

public class Doctor {
    private String name;
    private String specialty;
    private String availability;
    private int imageResId;

    public Doctor(String name, String specialty, String availability, int imageResId) {
        this.name = name;
        this.specialty = specialty;
        this.availability = availability;
        this.imageResId = imageResId;
    }

    public String getName() { return name; }
    public String getSpecialty() { return specialty; }
    public String getAvailability() { return availability; }
    public int getImageResId() { return imageResId; }
}