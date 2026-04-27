package com.rivange.app.models;

public class ChatMessage {

    private String id;
    private String chatId;
    private String senderId;
    private String senderName;
    private String text;
    private long createdAt;

    public ChatMessage() {
    }

    public ChatMessage(String id, String chatId, String senderId, String senderName, String text, long createdAt) {
        this.id = id;
        this.chatId = chatId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.text = text;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id == null ? "" : id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChatId() {
        return chatId == null ? "" : chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getSenderId() {
        return senderId == null ? "" : senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName == null ? "" : senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getText() {
        return text == null ? "" : text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}