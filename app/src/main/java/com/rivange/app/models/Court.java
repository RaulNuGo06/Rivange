package com.rivange.app.models;

public class Court {

    private String id;
    private String name;
    private String sport;
    private boolean available;
    private String surface;
    private String imageUrl;

    public Court() {
    }

    public Court(String id, String name, String sport, boolean available, String surface, String imageUrl) {
        this.id = id;
        this.name = name;
        this.sport = sport;
        this.available = available;
        this.surface = surface;
        this.imageUrl = imageUrl;
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

    public String getSport() {
        return sport == null ? "" : sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getSurface() {
        return surface == null ? "" : surface;
    }

    public void setSurface(String surface) {
        this.surface = surface;
    }

    public String getImageUrl() {
        return imageUrl == null ? "" : imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}