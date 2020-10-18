package com.vhoh.vhonlinehospital;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public class Notification {
    private String note;
    private String profileid;
    private String date;

    public Notification() {

    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getProfileid() {
        return profileid;
    }

    public void setProfileid(String profileid) {
        this.profileid = profileid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
