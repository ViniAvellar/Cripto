package org.example;

import java.io.*;
//import java.nio.file.Files;
import java.security.*;
//import java.security.spec.*;
import javax.crypto.Cipher;
//import javax.crypto.KeyGenerator;
//import javax.crypto.SecretKey;
//import javax.crypto.spec.SecretKeySpec;
//import java.util.Base64;

public class RSAFileEncryptor {

    private static final String ALGORITHM = "RSA";
    private static final int KEY_SIZE = 1024;


    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGenerator.initialize(KEY_SIZE, new SecureRandom());
        return keyPairGenerator.generateKeyPair();
    }

    public static void encryptFile(PublicKey publicKey, File inputFile, File outputFile) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile)) {

            byte[] buffer = new byte[117];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                byte[] output = cipher.doFinal(buffer, 0, bytesRead);
                fos.write(output);
            }
        }
    }

    public static void decryptFile(PrivateKey privateKey, File inputFile, File outputFile) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile)) {

            byte[] buffer = new byte[128];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                byte[] output = cipher.doFinal(buffer, 0, bytesRead);
                fos.write(output);
            }
        }
    }
}
