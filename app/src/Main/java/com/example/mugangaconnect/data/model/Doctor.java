package com.example.mugangaconnect.data.model;

public class Doctor {
    private String id;
    private String name;
    private String specialty;
    private String department;
    private String availability;

    public Doctor() {}

    public Doctor(String id, String name, String specialty, String department, String availability) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
        this.department = department;
        this.availability = availability;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getAvailability() { return availability; }
    public void setAvailability(String availability) { this.availability = availability; }
}
