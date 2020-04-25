package com.example.pahwa;

public class chatinfo {

    String message,who;
    Long timestamp;
    String alreadypaid;
    String file;
    String amount;
    String type;

    public chatinfo(String message, String who, Long timestamp, String alreadypaid, String file, String amount, String type, String cleared) {
        this.message = message;
        this.who = who;
        this.timestamp = timestamp;
        this.alreadypaid = alreadypaid;
        this.file = file;
        this.amount = amount;
        this.type = type;
        this.cleared = cleared;
    }


    String cleared;

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

    public String getCleared() {
        return cleared;
    }

    public void setCleared(String cleared) {
        this.cleared = cleared;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
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
