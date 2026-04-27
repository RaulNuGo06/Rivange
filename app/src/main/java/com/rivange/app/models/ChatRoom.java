package com.rivange.app.models;

import java.util.ArrayList;
import java.util.List;

public class ChatRoom {

    private String id;
    private String name;
    private String lastMessage;
    private long lastMessageAt;
    private String lastSenderId;
    private List<String> participants;

    public ChatRoom() {
    }

    public ChatRoom(String id, String name, String lastMessage, long lastMessageAt, String lastSenderId) {
        this.id = id;
        this.name = name;
        this.lastMessage = lastMessage;
        this.lastMessageAt = lastMessageAt;
        this.lastSenderId = lastSenderId;
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

    public String getLastMessage() {
        return lastMessage == null ? "" : lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getLastMessageAt() {
        return lastMessageAt;
    }

    public void setLastMessageAt(long lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
    }

    public String getLastSenderId() {
        return lastSenderId == null ? "" : lastSenderId;
    }

    public void setLastSenderId(String lastSenderId) {
        this.lastSenderId = lastSenderId;
    }

    public List<String> getParticipants() {
        return participants == null ? new ArrayList<>() : participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }
}
