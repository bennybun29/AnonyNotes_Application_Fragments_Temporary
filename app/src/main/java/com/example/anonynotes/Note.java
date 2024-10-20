package com.example.anonynotes;

public class Note {
    private String username, dateCreated, content, id, tvTime;
    private boolean isExpanded;

    public Note(String username, String dateCreated, String content) {
        this.username = username;
        this.dateCreated = dateCreated;
        this.content = content;
        this.isExpanded = false;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public String getDateCreated() { return dateCreated; }
    public String getContent() { return content; }
}
