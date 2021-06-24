package com.example.rentalapp;

public class ChatUserModel {
    String tenantid, documentid, renterid, chatid, chatroomname,rentername,tenantname;

    public ChatUserModel() {
    }

    public String getTenantid() {
        return tenantid;
    }

    public void setTenantid(String tenantid) {
        this.tenantid = tenantid;
    }

    public String getDocumentid() {
        return documentid;
    }

    public void setDocumentid(String documentid) {
        this.documentid = documentid;
    }

    public String getRenterid() {
        return renterid;
    }

    public void setRenterid(String renterid) {
        this.renterid = renterid;
    }

    public String getChatid() {
        return chatid;
    }

    public void setChatid(String chatid) {
        this.chatid = chatid;
    }

    public String getChatroomname() {
        return chatroomname;
    }

    public void setChatroomname(String chatroomname) {
        this.chatroomname = chatroomname;
    }

    public String getRentername() {
        return rentername;
    }

    public void setRentername(String rentername) {
        this.rentername = rentername;
    }

    public String getTenantname() {
        return tenantname;
    }

    public void setTenantname(String tenantname) {
        this.tenantname = tenantname;
    }
}
