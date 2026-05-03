package com.example.mugangaconnect.data.model;

public class Appointment {
    public enum Status { UPCOMING, ATTENDED, MISSED, CANCELLED, RESCHEDULED }
    public enum RiskLevel { LOW, MEDIUM, HIGH }

    private String id;
    private String patientId;
    private String doctorId;
    private String doctorName;
    private String department;
    private String date;        // ISO format: yyyy-MM-dd
    private String time;        // HH:mm
    private String reason;
    private Status status;
    private RiskLevel riskLevel;
    private long createdAt;

    public Appointment() {}

    public Appointment(String patientId, String doctorId, String doctorName,
                       String department, String date, String time) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.department = department;
        this.date = date;
        this.time = time;
        this.status = Status.UPCOMING;
        this.riskLevel = RiskLevel.LOW;
        this.createdAt = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public String getPatientId() { return patientId; }
    public String getDoctorId() { return doctorId; }
    public String getDoctorName() { return doctorName; }
    public String getDepartment() { return department; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getReason() { return reason; }
    public Status getStatus() { return status; }
    public RiskLevel getRiskLevel() { return riskLevel; }
    public long getCreatedAt() { return createdAt; }

    public void setId(String id) { this.id = id; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public void setDepartment(String department) { this.department = department; }
    public void setStatus(Status status) { this.status = status; }
    public void setStatus(String status) { this.status = statusFromString(status); }
    public void setRiskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevelFromString(riskLevel); }
    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
    public void setReason(String reason) { this.reason = reason; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public String getStatusValue() { return status == null ? null : status.name(); }
    public String getRiskLevelValue() { return riskLevel == null ? null : riskLevel.name(); }

    public static Status statusFromString(String value) {
        if (value == null) return null;
        try {
            return Status.valueOf(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static RiskLevel riskLevelFromString(String value) {
        if (value == null) return null;
        try {
            return RiskLevel.valueOf(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
