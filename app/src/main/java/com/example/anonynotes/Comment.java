package com.example.anonynotes;

import java.util.List;

public class Comment {

    String id;
    String username;
    String dateCreated;
    String content;




    public Comment(String id, String username, String dateCreated, String content) {
        this.id = id;
        this.username = username;
        this.dateCreated = dateCreated;
        this.content = content;

    }


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
