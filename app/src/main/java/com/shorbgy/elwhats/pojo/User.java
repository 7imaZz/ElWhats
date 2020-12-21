package com.shorbgy.elwhats.pojo;

public class User {

    private String id;
    private String imageUrl;
    private String username;

    public User() {
    }

    public User(String id, String imageUrl, String username) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
