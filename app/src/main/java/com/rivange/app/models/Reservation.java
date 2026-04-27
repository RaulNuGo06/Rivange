package com.rivange.app.models;

public class Reservation {

    private String id;
    private String userId;
    private String userName;
    private String courtId;
    private String courtName;
    private String sport;
    private String date;
    private String startTime;
    private String endTime;
    private String status;
    private long createdAt;

    public Reservation() {
    }

    public Reservation(String id, String userId, String userName, String courtId, String courtName, String sport,
                       String date, String startTime, String endTime, String status, long createdAt) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.courtId = courtId;
        this.courtName = courtName;
        this.sport = sport;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id == null ? "" : id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId == null ? "" : userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName == null ? "" : userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCourtId() {
        return courtId == null ? "" : courtId;
    }

    public void setCourtId(String courtId) {
        this.courtId = courtId;
    }

    public String getCourtName() {
        return courtName == null ? "" : courtName;
    }

    public void setCourtName(String courtName) {
        this.courtName = courtName;
    }

    public String getSport() {
        return sport == null ? "" : sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public String getDate() {
        return date == null ? "" : date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime == null ? "" : startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime == null ? "" : endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status == null ? "" : status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
