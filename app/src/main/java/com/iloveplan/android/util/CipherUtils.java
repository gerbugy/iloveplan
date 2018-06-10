package com.iloveplan.android.util;

import android.text.TextUtils;

import java.security.Key;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public final class CipherUtils {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    private Key mSecretKeySpec;
    private Cipher mCipher;

    public CipherUtils(String key) {
        try {
            byte[] keyBytes = Arrays.copyOf(key.getBytes(), 16);
            mSecretKeySpec = new SecretKeySpec(keyBytes, ALGORITHM);
            mCipher = Cipher.getInstance(TRANSFORMATION);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String encrypt(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        try {
            mCipher.init(Cipher.ENCRYPT_MODE, mSecretKeySpec);
            byte[] encrypt = mCipher.doFinal(str.getBytes());
            return bytesToHex(encrypt);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public String decrypt(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        try {
            mCipher.init(Cipher.DECRYPT_MODE, mSecretKeySpec);
            byte[] decrypt = mCipher.doFinal(hexToBytes(str));
            return new String(decrypt);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private String bytesToHex(byte[] bytes) {
        final char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        final char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private byte[] hexToBytes(String str) {
        final int len = str.length();
        final byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(str.charAt(i), 16) << 4) + Character.digit(str.charAt(i + 1), 16));
        }
        return data;
    }
}
