package com.example.mugangaconnect.data.model;

public class ChatMessage {
    public static final String ROLE_USER = "user";
    public static final String ROLE_AI = "model";

    private long id;
    private String patientId;
    private String role;
    private String content;
    private long timestamp;

    public ChatMessage() {}

    public ChatMessage(String patientId, String role, String content) {
        this.patientId = patientId;
        this.role = role;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
    }

    public long getId() { return id; }
    public String getPatientId() { return patientId; }
    public String getRole() { return role; }
    public String getContent() { return content; }
    public long getTimestamp() { return timestamp; }

    public void setId(long id) { this.id = id; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public void setRole(String role) { this.role = role; }
    public void setContent(String content) { this.content = content; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
