package com.example.mugangaconnect;

public class Appointment {
    private String id;
    private Doctor doctor;
    private String date;
    private String time;
    private String status;

    public Appointment(String id, Doctor doctor, String date, String time, String status) {
        this.id = id;
        this.doctor = doctor;
        this.date = date;
        this.time = time;
        this.status = status;
    }

    public String getId() { return id; }
    public Doctor getDoctor() { return doctor; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getStatus() { return status; }

    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
    public void setStatus(String status) { this.status = status; }
}