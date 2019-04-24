package com.terminalreach.groupbored;

public class CommentRow {
    private int id;
    private String imageURL;
    private String username;
    private String timestamp;
    private String postContents;

    CommentRow(int id, String imageURL, String username, String timestamp, String postContents) {
        this.id = id;
        this.imageURL = imageURL;
        this.username = username;
        this.timestamp = timestamp;
        this.postContents = postContents;
    }

    int getPostId() {
        return id;
    }

    String getImageURL() {
        return imageURL;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    String getTimestamp() {
        return timestamp;
    }

    String getPostContents() {
        return postContents;
    }
}
