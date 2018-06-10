package com.iloveplan.android.asis.util;

import java.util.regex.Pattern;

public class StringUtil {

    /**
     * 빈문자열 여부를 반환합니다.
     */
    public static boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }

    /**
     * 공백포함여부를 반환합니다.
     */
    public static boolean hasWhiteSpace(String str) {
        return Pattern.compile("\\s").matcher(str).find();
    }

    /**
     * 제목을 추출합니다.
     */
    public static CharSequence extractTitle(String content) {

        // 공백을 제거합니다.
        content = content.trim();

        // 엔터문까지 제한합니다.
        int lineSeparatorIndex = content.indexOf(System.getProperty("line.separator"));
        if (lineSeparatorIndex > -1)
            content = content.substring(0, lineSeparatorIndex);

        // 글자수를 제한합니다.
        int maxLength = 20;
        if (content.length() > maxLength)
            content = content.substring(0, maxLength);

        return content;
    }
}
