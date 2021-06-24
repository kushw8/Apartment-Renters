package com.example.rentalapp;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;

import java.util.Date;

public class ChatMessage {

    private String messageText;
    private String messageUser;
    long messageTime;
    private String from;
    private String to;
    private String isTenant;
    private String isRenter;
    private String renterName;
    private String tenantName;

    public ChatMessage(String messageText, String from, String to, long messageTime, String isTenant, String isRenter, String tenantName, String renterName) {
        this.messageText = messageText;
        this.messageTime = messageTime;
        this.from = from;
        this.to = to;
        this.isRenter = isRenter;
        this.isTenant = isTenant;
        this.renterName = renterName;
        this.tenantName = tenantName;
    }

    public ChatMessage(){

    }

    public String getRenterName() {
        return renterName;
    }

    public void setRenterName(String renterName) {
        this.renterName = renterName;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getIsTenant() {
        return isTenant;
    }

    public void setIsTenant(String isTenant) {
        this.isTenant = isTenant;
    }

    public String getIsRenter() {
        return isRenter;
    }

    public void setIsRenter(String isRenter) {
        this.isRenter = isRenter;
    }

    public ChatMessage(String from) {
        this.from = from;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}
