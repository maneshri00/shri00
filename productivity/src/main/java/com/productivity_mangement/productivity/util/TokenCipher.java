package com.productivity_mangement.productivity.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class TokenCipher {

    @Value("${app.enc.key:}")
    private String key;

    public String encrypt(String plain) {
        if (plain == null) return null;
        if (key == null || key.isBlank()) return plain;
        try {
            byte[] k = normalizeKey(key);
            SecretKeySpec sk = new SecretKeySpec(k, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, sk);
            byte[] enc = cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(enc);
        } catch (Exception e) {
            return plain;
        }
    }

    public String decrypt(String enc) {
        if (enc == null) return null;
        if (key == null || key.isBlank()) return enc;
        try {
            byte[] k = normalizeKey(key);
            SecretKeySpec sk = new SecretKeySpec(k, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, sk);
            byte[] dec = cipher.doFinal(Base64.getDecoder().decode(enc));
            return new String(dec, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return enc;
        }
    }

    private byte[] normalizeKey(String k) {
        byte[] b = k.getBytes(StandardCharsets.UTF_8);
        byte[] out = new byte[16];
        System.arraycopy(b, 0, out, 0, Math.min(b.length, 16));
        return out;
    }
}
