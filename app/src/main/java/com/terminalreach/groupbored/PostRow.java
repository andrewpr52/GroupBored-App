package com.terminalreach.groupbored;

import android.support.annotation.Nullable;

public class PostRow {
    private int id;
    private String imageURL;
    private String username;
    private String timestamp;
    private String postGroup;
    private String postContents;
    private String postImageOneURL;
    private int posRatingCount;
    private int negRatingCount;

    PostRow(int id, String imageURL, String username, String timestamp, String postGroup,
                   String postContents, @Nullable String postImageOneURL, int posRatingCount, int negRatingCount) {
        this.id = id;
        this.imageURL = imageURL;
        this.username = username;
        this.timestamp = timestamp;
        this.postGroup = postGroup;
        this.postContents = postContents;
        this.postImageOneURL = postImageOneURL;

        this.posRatingCount = posRatingCount;
        this.negRatingCount = negRatingCount;
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

    String getPostGroup() {
        return postGroup;
    }

    String getPostContents() {
        return postContents;
    }

    String getPostImageOneURL() {
        if (postImageOneURL == null) {
            return "none";
        }
        else {
            return postImageOneURL;
        }
    }

    int getPosRatingCount() {
        return posRatingCount;
    }

    int getNegRatingCount() {
        return negRatingCount;
    }
}
