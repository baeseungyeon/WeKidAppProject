package com.example.wekid;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ChatDTO {
    private String userName;
    private String message;
    private String date;
    private String fileName;
    private String fileUrl;

    public ChatDTO() {}

    public ChatDTO(String userName, String message, String date) {
        this.userName = userName;
        this.message = message;
        this.date = date;
    }

    public ChatDTO(String userName, String message, String date, String fileName) {
        this.userName = userName;
        this.message = message;
        this.date = date;
        this.fileName = fileName;
    }

    public ChatDTO(String userName, String message, String date, String fileName, String fileUrl) {
        this.userName = userName;
        this.message = message;
        this.date = date;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) { this.message = message; }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public String getFileName() { return fileName; }

    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileUrl() { return fileUrl; }

    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
}