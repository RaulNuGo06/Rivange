package com.rivange.app.models;

import java.util.ArrayList;
import java.util.List;

public class Match {

    private String id;
    private String title;
    private String date;
    private String time;
    private String courtName;
    private String courtId;
    private String sport;
    private String level;
    private int maxPlayers;
    private int currentPlayers;
    private String createdBy;
    private boolean open;
    private List<String> playerIds;
    private List<String> playerNames;
    private String refereeId;
    private String refereeName;
    private String score;
    private String winnerId;

    public Match() {
    }

    public Match(String id, String title, String date, String time, String courtName, String courtId, String sport,
                 String level, int maxPlayers, int currentPlayers, String createdBy, boolean open,
                 List<String> playerIds, List<String> playerNames, String refereeId, String refereeName,
                 String score, String winnerId) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.time = time;
        this.courtName = courtName;
        this.courtId = courtId;
        this.sport = sport;
        this.level = level;
        this.maxPlayers = maxPlayers;
        this.currentPlayers = currentPlayers;
        this.createdBy = createdBy;
        this.open = open;
        this.playerIds = playerIds;
        this.playerNames = playerNames;
        this.refereeId = refereeId;
        this.refereeName = refereeName;
        this.score = score;
        this.winnerId = winnerId;
    }

    public String getId() {
        return id == null ? "" : id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title == null ? "" : title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date == null ? "" : date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time == null ? "" : time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCourtName() {
        return courtName == null ? "" : courtName;
    }

    public void setCourtName(String courtName) {
        this.courtName = courtName;
    }

    public String getCourtId() {
        return courtId == null ? "" : courtId;
    }

    public void setCourtId(String courtId) {
        this.courtId = courtId;
    }

    public String getSport() {
        return sport == null ? "" : sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public String getLevel() {
        return level == null ? "" : level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getCurrentPlayers() {
        return currentPlayers;
    }

    public void setCurrentPlayers(int currentPlayers) {
        this.currentPlayers = currentPlayers;
    }

    public String getCreatedBy() {
        return createdBy == null ? "" : createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public List<String> getPlayerIds() {
        return playerIds == null ? new ArrayList<>() : playerIds;
    }

    public void setPlayerIds(List<String> playerIds) {
        this.playerIds = playerIds;
    }

    public List<String> getPlayerNames() {
        return playerNames == null ? new ArrayList<>() : playerNames;
    }

    public void setPlayerNames(List<String> playerNames) {
        this.playerNames = playerNames;
    }

    public String getRefereeId() {
        return refereeId == null ? "" : refereeId;
    }

    public void setRefereeId(String refereeId) {
        this.refereeId = refereeId;
    }

    public String getRefereeName() {
        return refereeName == null ? "" : refereeName;
    }

    public void setRefereeName(String refereeName) {
        this.refereeName = refereeName;
    }

    public String getScore() {
        return score == null ? "" : score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getWinnerId() {
        return winnerId == null ? "" : winnerId;
    }

    public void setWinnerId(String winnerId) {
        this.winnerId = winnerId;
    }
}
