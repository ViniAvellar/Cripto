package org.example;

import java.io.*;
//import java.nio.file.Files;
import java.security.*;
//import java.security.spec.*;
//import javax.crypto.Cipher;
//import javax.crypto.KeyGenerator;
//import javax.crypto.SecretKey;
//import javax.crypto.spec.SecretKeySpec;
//import java.util.Base64;


public class Main {
    public static void main(String[] args) {
        try {

            KeyPair keyPair = RSAFileEncryptor.generateKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();

            File fileToEncrypt = new File("/home/vinicius-avellar/Downloads/PUC/crypto/cripto.txt");
            File encryptedFile = new File("/home/vinicius-avellar/Downloads/PUC/crypto/encrypted_file.enc");
            File decryptedFile = new File("/home/vinicius-avellar/Downloads/PUC/crypto/decrypted_file.txt");


            RSAFileEncryptor.encryptFile(publicKey, fileToEncrypt, encryptedFile);


            RSAFileEncryptor.decryptFile(privateKey, encryptedFile, decryptedFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}