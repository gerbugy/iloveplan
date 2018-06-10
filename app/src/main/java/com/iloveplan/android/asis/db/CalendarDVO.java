package com.iloveplan.android.asis.db;

public class CalendarDVO {

    private String date;
    private String dateLunar;
    private String dateTxt;
    private String holidayYn;

    public CalendarDVO() {
        holidayYn = "N";
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDateLunar() {
        return dateLunar;
    }

    public void setDateLunar(String dateLunar) {
        this.dateLunar = dateLunar;
    }

    public String getDateTxt() {
        return dateTxt;
    }

    public void setDateTxt(String dateTxt) {
        this.dateTxt = dateTxt;
    }

    public String getHolidayYn() {
        return holidayYn;
    }

    public void setHolidayYn(String holidayYn) {
        this.holidayYn = holidayYn;
    }
}
