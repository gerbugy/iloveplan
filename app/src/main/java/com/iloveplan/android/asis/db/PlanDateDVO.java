package com.iloveplan.android.asis.db;

import java.sql.Timestamp;

public class PlanDateDVO {

    private int planNo;
    private String planDt;
    private String successYn;
    private String memoTxt;
    private Timestamp regTs;
    private Timestamp chgTs;

    public PlanDateDVO() {
        successYn = "N";
    }

    public int getPlanNo() {
        return planNo;
    }

    public void setPlanNo(int planNo) {
        this.planNo = planNo;
    }

    public String getPlanDt() {
        return planDt;
    }

    public void setPlanDt(String planDt) {
        this.planDt = planDt;
    }

    public String getSuccessYn() {
        return successYn;
    }

    public void setSuccessYn(String successYn) {
        this.successYn = successYn;
    }

    public String getMemoTxt() {
        return memoTxt;
    }

    public void setMemoTxt(String memoTxt) {
        this.memoTxt = memoTxt;
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
