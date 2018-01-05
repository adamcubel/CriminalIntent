package com.bignerdranch.android.criminalintent;

import java.util.Date;
import java.util.UUID;

/**
 * Created by acubel on 1/4/2018.
 */

public class Crime {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private  boolean mSolved;

    public Crime() {
        mId = UUID.randomUUID();
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date mDate) {
        this.mDate = mDate;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        this.mSolved = solved;
    }
}