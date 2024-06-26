package com.nbr.bankingsystem.utils;

import java.util.regex.Pattern;

public class ValidationUtil {

    private static final Pattern MOBILE_PATTERN = Pattern.compile("^(078|079|072|073|074)\\d{7}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");

    public static boolean isValidMobile(String mobile) {
        return MOBILE_PATTERN.matcher(mobile).matches();
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
}
