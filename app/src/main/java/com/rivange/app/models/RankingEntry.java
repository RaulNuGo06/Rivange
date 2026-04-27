package com.rivange.app.models;

public class RankingEntry {

    private String id;
    private String userId;
    private String userName;
    private int points;
    private int wins;
    private int losses;
    private int position;
    private String rankName;

    public RankingEntry() {
    }

    public RankingEntry(String id, String userId, String userName, int points, int wins, int losses, int position, String rankName) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.points = points;
        this.wins = wins;
        this.losses = losses;
        this.position = position;
        this.rankName = rankName;
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

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getRankName() {
        return rankName == null ? "" : rankName;
    }

    public void setRankName(String rankName) {
        this.rankName = rankName;
    }
}
