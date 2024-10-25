package com.example.anonynotes;

import java.util.List;

public class Comment {

    String id;
    String username;
    String dateCreated;
    String content;
    boolean isExpanded;




    public Comment(String id, String username, String content, String dateCreated) {
        this.id = id;
        this.username = username;
        this.dateCreated = dateCreated;
        this.content = content;
        this.isExpanded = false;

    }

    public boolean isExpanded() {return isExpanded;}

    public void setExpanded(boolean expanded) {isExpanded = expanded;}

    public String getDateCreated() {
        return dateCreated;
    }

    public String getContent() {
        return content;
    }

    public String getUsername() {
        return username;
    }

}