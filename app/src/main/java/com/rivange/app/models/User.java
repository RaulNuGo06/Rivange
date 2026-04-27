package com.rivange.app.models;

public class User {

    private String id;
    private String name;
    private String email;
    private String role;
    private String phone;
    private String profileImageUrl;
    private long createdAt;

    public User() {
    }

    public User(String id, String name, String email, String role, String phone, String profileImageUrl, long createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.phone = phone;
        this.profileImageUrl = profileImageUrl;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id == null ? "" : id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email == null ? "" : email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role == null ? "user" : role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhone() {
        return phone == null ? "" : phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfileImageUrl() {
        return profileImageUrl == null ? "" : profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(getRole());
    }

    public boolean isReferee() {
        return "arbitro".equalsIgnoreCase(getRole());
    }
}
