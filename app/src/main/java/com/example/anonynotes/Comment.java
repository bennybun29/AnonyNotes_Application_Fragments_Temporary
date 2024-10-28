package com.example.anonynotes;

import java.util.List;

public class Comment {

    String id;
    String username;
    String dateCreated;
    String content, noteId, commentId;
    boolean isExpanded, isLiked;




    public Comment(String id, String username, String content, String dateCreated) {
        this.id = id;
        this.username = username;
        this.dateCreated = dateCreated;
        this.content = content;
        this.isExpanded = false;

    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public String getCommentId() {
        return commentId;
    }



    public boolean isExpanded() {return isExpanded;}

    public void setExpanded(boolean expanded) {isExpanded = expanded;}

    public String getDateCreated() {
        return dateCreated;
    }

    public String getNoteId() {
        return noteId;
    }

    public String getContent() {
        return content;
    }

    public String getUsername() {
        return username;
    }

}