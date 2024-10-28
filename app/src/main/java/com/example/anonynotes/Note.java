package com.example.anonynotes;

public class Note {
    private String username, dateCreated, content, id, tvTime, noteId, comment_id;
    private boolean isExpanded, isLiked;
    private int commentCount, heartCount;

    public Note(String username, String dateCreated, String content, String noteId) {
        this.username = username;
        this.dateCreated = dateCreated;
        this.content = content;
        this.isExpanded = false;
        this.noteId = noteId;
        this.comment_id = comment_id;
        this.heartCount = heartCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public boolean setLiked(boolean liked) {
        return isLiked = liked;
    }

    public int getHeartCount() {
        return heartCount;
    }

    public void setHeartCount(int heartCount) {
        this.heartCount = heartCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
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
