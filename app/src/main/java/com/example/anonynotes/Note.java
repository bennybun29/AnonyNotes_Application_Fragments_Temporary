package com.example.anonynotes;

public class Note {
    private String username, dateCreated, content, id, tvTime, noteId, comment_id;
    private boolean isExpanded;

    public Note(String username, String dateCreated, String content, String noteId) {
        this.username = username;
        this.dateCreated = dateCreated;
        this.content = content;
        this.isExpanded = false;
        this.noteId = noteId;
        this.comment_id = comment_id;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public String getNoteId() {
        return noteId;
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

    public String getComment_id(){
        return comment_id;
    }
}
