package org.example;

import org.apache.commons.codec.digest.DigestUtils;

public class Hasher {
    public static void hashPassword(String password) {

        DigestUtils.sha256Hex(password);
    }

}
