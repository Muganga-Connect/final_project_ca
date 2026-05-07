package com.example.mugangaconnect.data.model;

public class Notification {
    private String id;
    private String userId;      // Target patient ID
    private String title;
    private String body;
    private long timestamp;
    private String type;        // e.g., "APPOINTMENT", "RISK_ALERT", "MEDICATION"
    private boolean isRead;
    private String referenceId; // e.g., the Appointment ID it refers to

    public Notification() {}

    public Notification(String userId, String title, String body, String type, String referenceId) {
        this.userId = userId;
        this.title = title;
        this.body = body;
        this.type = type;
        this.referenceId = referenceId;
        this.timestamp = System.currentTimeMillis();
        this.isRead = false;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }
}
