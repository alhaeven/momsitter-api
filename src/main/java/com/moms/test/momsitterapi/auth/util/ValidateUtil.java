package com.moms.test.momsitterapi.auth.util;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateUtil {

    static final Pattern PW_REGEX = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*(),\\/\\.?])[A-Za-z\\d!@#$%^&*(),\\/\\.?]{8,}");


    public static boolean validatePassword(String password) {
        Matcher m = PW_REGEX.matcher(password);

        return m.matches();
    }
}
