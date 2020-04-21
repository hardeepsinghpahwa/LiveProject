package com.example.pahwa;

public class chatinfo {

    String message,who,timestamp,alreadypaid,file,amount;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public chatinfo() {
    }

    public chatinfo(String message, String who, String timestamp, String alreadypaid, String file, String amount) {
        this.message = message;
        this.who = who;
        this.timestamp = timestamp;
        this.alreadypaid = alreadypaid;
        this.file = file;
        this.amount = amount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getAlreadypaid() {
        return alreadypaid;
    }

    public void setAlreadypaid(String alreadypaid) {
        this.alreadypaid = alreadypaid;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
