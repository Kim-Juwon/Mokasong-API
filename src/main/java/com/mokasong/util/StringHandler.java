package com.mokasong.util;

public class StringHandler {
    /**
     *  이메일의 아이디 부분을 앞 2글자만 남겨두고 전부 '*'로 대체합니다.
     *  ex) damiano102777@naver.com -> da***********@naver.com
     */
    public static String hideIdOfEmail(String email) {
        StringBuilder stringBuilder = new StringBuilder(email);

        for (int i = 2; i < email.length(); i++) {
            if (email.charAt(i) == '@') {
                break;
            }
            stringBuilder.setCharAt(i, '*');
        }

        return stringBuilder.toString();
    }
}
