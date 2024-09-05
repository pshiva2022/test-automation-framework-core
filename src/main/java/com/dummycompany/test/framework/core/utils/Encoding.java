package com.dummycompany.test.framework.core.utils;

import org.apache.commons.codec.binary.Base64;

public class Encoding {
    public static String encodePassword(String pswd) {
        byte[] encodedPwdBytes = Base64.encodeBase64(pswd.getBytes());
        String decodedPwd = new String(encodedPwdBytes);
        return decodedPwd;
    }

    public static String decodePassword(String pswd) {
        byte[] decodedPwdBytes = Base64.decodeBase64(pswd.getBytes());
        String decodedPwd = new String(decodedPwdBytes);
        return decodedPwd;
    }
}
