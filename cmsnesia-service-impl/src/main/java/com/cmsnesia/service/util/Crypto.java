package com.cmsnesia.service.util;

import java.security.MessageDigest;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class Crypto {

  private static final SecureRandom RANDOM = new SecureRandom();

  private final TokenInfo tokenInfo;

  public byte[] encrypt(String plainText) throws Exception {
    byte[] clean = plainText.getBytes();

    // Generating IV.
    int ivSize = 16;
    byte[] iv = new byte[ivSize];
    RANDOM.nextBytes(iv);
    IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

    // Hashing key.
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    digest.update(tokenInfo.getSecret().getBytes("UTF-8"));
    byte[] keyBytes = new byte[16];
    System.arraycopy(digest.digest(), 0, keyBytes, 0, keyBytes.length);
    SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

    // Encrypt.
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
    byte[] encrypted = cipher.doFinal(clean);

    // Combine IV and encrypted part.
    byte[] encryptedIVAndText = new byte[ivSize + encrypted.length];
    System.arraycopy(iv, 0, encryptedIVAndText, 0, ivSize);
    System.arraycopy(encrypted, 0, encryptedIVAndText, ivSize, encrypted.length);

    return encryptedIVAndText;
  }

  public String decrypt(byte[] encryptedIvTextBytes) throws Exception {
    int ivSize = 16;
    int keySize = 16;

    // Extract IV.
    byte[] iv = new byte[ivSize];
    System.arraycopy(encryptedIvTextBytes, 0, iv, 0, iv.length);
    IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

    // Extract encrypted part.
    int encryptedSize = encryptedIvTextBytes.length - ivSize;
    byte[] encryptedBytes = new byte[encryptedSize];
    System.arraycopy(encryptedIvTextBytes, ivSize, encryptedBytes, 0, encryptedSize);

    // Hash key.
    byte[] keyBytes = new byte[keySize];
    MessageDigest md = MessageDigest.getInstance("SHA-256");
    md.update(tokenInfo.getSecret().getBytes());
    System.arraycopy(md.digest(), 0, keyBytes, 0, keyBytes.length);
    SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

    // Decrypt.
    Cipher cipherDecrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
    cipherDecrypt.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
    byte[] decrypted = cipherDecrypt.doFinal(encryptedBytes);

    return new String(decrypted);
  }
}
