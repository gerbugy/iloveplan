package com.iloveplan.android.asis.db;

import java.sql.Timestamp;

public class PlanDVO {

    private int planNo;
    private String planNm;
    private String planStdt;
    private String planEddt;
    private int totalCount;
    private int successCount;
    private String onMonYn;
    private String onTueYn;
    private String onWedYn;
    private String onThuYn;
    private String onFriYn;
    private String onSatYn;
    private String onSunYn;
    private String onHolidayYn;
    private int orderNo;
    private Timestamp regTs;
    private Timestamp chgTs;

    public PlanDVO() {
        onMonYn = "Y";
        onTueYn = "Y";
        onWedYn = "Y";
        onThuYn = "Y";
        onFriYn = "Y";
        onSatYn = "Y";
        onSunYn = "Y";
        onHolidayYn = "Y";
    }

    public int getPlanNo() {
        return planNo;
    }

    public void setPlanNo(int planNo) {
        this.planNo = planNo;
    }

    public String getPlanNm() {
        return planNm;
    }

    public void setPlanNm(String planNm) {
        this.planNm = planNm;
    }

    public String getPlanStdt() {
        return planStdt;
    }

    public void setPlanStdt(String planStdt) {
        this.planStdt = planStdt;
    }

    public String getPlanEddt() {
        return planEddt;
    }

    public void setPlanEddt(String planEddt) {
        this.planEddt = planEddt;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public String getOnMonYn() {
        return onMonYn;
    }

    public void setOnMonYn(String onMonYn) {
        this.onMonYn = onMonYn;
    }

    public String getOnTueYn() {
        return onTueYn;
    }

    public void setOnTueYn(String onTueYn) {
        this.onTueYn = onTueYn;
    }

    public String getOnWedYn() {
        return onWedYn;
    }

    public void setOnWedYn(String onWedYn) {
        this.onWedYn = onWedYn;
    }

    public String getOnThuYn() {
        return onThuYn;
    }

    public void setOnThuYn(String onThuYn) {
        this.onThuYn = onThuYn;
    }

    public String getOnFriYn() {
        return onFriYn;
    }

    public void setOnFriYn(String onFriYn) {
        this.onFriYn = onFriYn;
    }

    public String getOnSatYn() {
        return onSatYn;
    }

    public void setOnSatYn(String onSatYn) {
        this.onSatYn = onSatYn;
    }

    public String getOnSunYn() {
        return onSunYn;
    }

    public void setOnSunYn(String onSunYn) {
        this.onSunYn = onSunYn;
    }

    public String getOnHolidayYn() {
        return onHolidayYn;
    }

    public void setOnHolidayYn(String onHolidayYn) {
        this.onHolidayYn = onHolidayYn;
    }

    public int getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }

    public Timestamp getRegTs() {
        return regTs;
    }

    public void setRegTs(Timestamp regTs) {
        this.regTs = regTs;
    }

    public Timestamp getChgTs() {
        return chgTs;
    }

    public void setChgTs(Timestamp chgTs) {
        this.chgTs = chgTs;
    }

}
