package com.example.michail.fragmentedc;


import java.util.Date;
import java.util.UUID;

public class Time {
    public static final int IMOPTANT_TYPE = 1;
    public static final int CASUAL_TYPE = 0;


    private String mTitle;
    private Date mDate;
    private Date mClockTime;
    private UUID mId;
    private boolean mSolved;
    private int mTypeTask;
    private String mContact;


    public Time() {
        this(UUID.randomUUID());
    }

    public Time(UUID id) {
        mId = id;
        mDate = new Date();
        mClockTime = new Date();
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setDate(Date date) {

        mDate = date;
    }

    public Date getDate() {
        return mDate;
    }

    public void setClockTime(Date date) {

        mClockTime = date;
    }

    public Date getClockTime() {
        return mClockTime;
    }

    public UUID getId() {
        return mId;
    }

    public void setContact(String contact) {
        mContact = contact;
    }

    public String getContact() {
        return mContact;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public String getPhotoFileName() {
        return "IMG_" + getId().toString() + ".jpg";
    }

    public int getTypeTask() {
        return mTypeTask;
    }

    public void setTypeTask(int typeTask) {
        mTypeTask = typeTask;
    }
}
