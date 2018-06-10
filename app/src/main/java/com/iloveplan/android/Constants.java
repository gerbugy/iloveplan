package com.iloveplan.android;

public interface Constants {

    /**
     * 홈디렉토리
     */
    String HOME_DIRECTORY = "MiniMemo";

    /**
     * 데이터베이스명
     */
    String DATABASE_NAME = "memo.db";

    /**
     * 관리자 디바이스 아이디
     */
    String ADMIN_DEVICE_ID = "A6A9A860FB6EC099AC038A576602A9C5";

    /**
     * 요청코드
     */
    int REQUEST_INSERT = 10;
    int REQUEST_VIEW = 11;
    int REQUEST_CHANGE = 12;

    /**
     * 응답코드
     */
    int RESULT_INSERTED = 10;
    int RESULT_CHANGED = 11;
    int RESULT_REMOVED = 12;

    /**
     * 기타
     */
    long NO_ID = -1;

    /**
     * 암호키
     */
    String CIPHER_KEY = "com.gerbugy";
}