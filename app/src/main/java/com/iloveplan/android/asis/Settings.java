package com.iloveplan.android.asis;

public final class Settings {

    /**
     * 전역
     */
    public static final String ENCODING = "UTF-8";
    public static final String APPLICATION_NAME = "iloveplan";

    /**
     * 설정
     */
    public static final String PREF_TODAY = "PREF_TODAY";
    public static final String PREF_CALENDAR_VERSION = "PREF_CALENDAR_VERSION";
    public static final String PREF_PLAN_BATCHED_TIME = "PREF_PLAN_BATCHED_TIME";
    public static final String PREF_USER_EMAIL = "PREF_USER_EMAIL";
    public static final String PREF_USER_PW = "PREF_USER_PW";
    public static final String PREF_USER_NM = "PREF_USER_NM";

    /**
     * 컨텍스트메뉴
     */
    public static final int CONTEXT_EDIT = 1;
    public static final int CONTEXT_DELETE = 2;
    public static final int CONTEXT_SHARE = 3;

    /**
     * 요청코드
     */
    public static final int REQUEST_VIEW = 1;
    public static final int REQUEST_EDIT = 2;

    /**
     * 결과코드
     */
    public static final int RESULT_INSERTED = 1;
    public static final int RESULT_UPDATED = 2;
    public static final int RESULT_DELETED = 3;
}
