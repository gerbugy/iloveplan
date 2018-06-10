package com.iloveplan.android.asis.db;

public class VersionDVO {

    private int applicationVersionMin;
    private int applicationVersionMax;
    private int calendarVersion;

    public int getApplicationVersionMin() {
        return applicationVersionMin;
    }

    public void setApplicationVersionMin(int applicationVersionMin) {
        this.applicationVersionMin = applicationVersionMin;
    }

    public int getApplicationVersionMax() {
        return applicationVersionMax;
    }

    public void setApplicationVersionMax(int applicationVersionMax) {
        this.applicationVersionMax = applicationVersionMax;
    }

    public int getCalendarVersion() {
        return calendarVersion;
    }

    public void setCalendarVersion(int calendarVersion) {
        this.calendarVersion = calendarVersion;
    }
}