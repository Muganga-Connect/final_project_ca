package com.example.mugangaconnect.data.model;

public class Doctor {
    private String id;
    private String name;
    private String specialty;
    private String department;
    private double rating;
    private String imageUrl;
    private String profileImage;
    private String availability;
    private AvailabilitySchedule availabilitySchedule;
    private String hospitalId;

    public static class AvailabilitySchedule {
        public java.util.List<String> days;
        public String startTime;
        public String endTime;
        public int slotDuration;
    }

    public Doctor() {}

    // ... existing constructors ...

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public String getImageUrl() { return imageUrl != null ? imageUrl : profileImage; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
    public String getAvailability() { return availability; }
    public void setAvailability(String availability) { this.availability = availability; }
    public AvailabilitySchedule getAvailabilitySchedule() { return availabilitySchedule; }
    public void setAvailabilitySchedule(AvailabilitySchedule schedule) { this.availabilitySchedule = schedule; }
    public String getHospitalId() { return hospitalId; }
    public void setHospitalId(String hospitalId) { this.hospitalId = hospitalId; }
}
