package org.example;

import static spark.Spark.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.*;
import javax.servlet.MultipartConfigElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final String ENCRYPTION_DIR = "upload/";
    private static final String UPLOAD_DIR = "testepasta/";
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private static final String ALGORITHM = "RSA";
    private static final int KEY_SIZE = 1024;

    public static void main(String[] args) {
        port(8080);
        enableCORS("http://127.0.0.1:5500", "GET, POST, OPTIONS", "Content-Type, Accept");

        handleFileUpload();
        handleFileUploadEncrypt();
        handleFileUploadDecrypt();
    }
    public static PublicKey loadPublicKey(String filename) throws Exception {
        byte[] keyBytes = readKeyFromFile(filename);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePublic(spec);
    }

    public static PrivateKey loadPrivateKey(String filename) throws Exception {
        byte[] keyBytes = readKeyFromFile(filename);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePrivate(spec);
    }

    private static byte[] readKeyFromFile(String filename) throws IOException {
        try (FileInputStream fis = new FileInputStream(filename)) {
            return fis.readAllBytes();
        }
    }

    private static void enableCORS(String origin, String methods, String headers) {
        options("/*", (req, res) -> {
            String accessControlRequestHeaders = req.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                res.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = req.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                res.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            return "OK";
        });

        before((req, res) -> {
            res.header("Access-Control-Allow-Origin", origin);
            res.header("Access-Control-Allow-Methods", methods);
            res.header("Access-Control-Allow-Headers", headers);
        });
    }
    public static void encryptFile(String inputFile, String outputFile, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile)) {

            byte[] buffer = new byte[117];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) > 0) {
                byte[] encrypted = cipher.doFinal(buffer, 0, bytesRead);
                fos.write(encrypted);
            }
        }
    }

    public static void decryptFile(String inputFile, String outputFile, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile)) {

            byte[] buffer = new byte[128];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) > 0) {
                byte[] decrypted = cipher.doFinal(buffer, 0, bytesRead);
                fos.write(decrypted);
            }
        }
    }

    private static Object handleFileUpload() {
        post("/upload", (req, res) -> {

            PublicKey publicKey = loadPublicKey("public.key");
            PrivateKey privateKey = loadPrivateKey("private.key");



            String uploadDir = "/home/vinicius-avellar/Downloads/PUC/crypto/testepasta";
            File uploadFolder = new File(uploadDir);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs();
            }


            req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
            try (InputStream input = req.raw().getPart("file").getInputStream()) {
                String fileName = Paths.get(req.raw().getPart("file").getSubmittedFileName()).getFileName().toString();
                File uploadedFile = new File(uploadFolder, fileName);


                Files.copy(input, uploadedFile.toPath());
                System.out.println(uploadedFile.toPath().toString());
                decryptFile(uploadedFile.toPath().toString(), uploadDir+"/decript"+fileName,privateKey);

                Files.copy(input, uploadedFile.toPath());
                System.out.println(uploadedFile.toPath().toString());
                encryptFile(uploadedFile.toPath().toString(), uploadDir+"/encript"+fileName,publicKey);

                return "{\"status\":\"success\", \"message\":\"File uploaded successfully.\", \"path\":\"" + uploadedFile.getAbsolutePath() + "\"}";
            } catch (Exception e) {
                res.status(500);
                return "{\"status\":\"error\", \"message\":\"File upload failed: " + e.getMessage() + "\"}";
            }
        });
        return null;
    }

    private static void handleFileUploadDecrypt() {
        post("/upload/decript", (req, res) -> {

            PublicKey publicKey = loadPublicKey("public.key");
            PrivateKey privateKey = loadPrivateKey("private.key");

            String uploadDir = UPLOAD_DIR;
            File uploadFolder = new File(uploadDir);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs();
            }


            req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
            try (InputStream input = req.raw().getPart("file").getInputStream()) {
                String fileName = Paths.get(req.raw().getPart("file").getSubmittedFileName()).getFileName().toString();
                File uploadedFile = new File(uploadFolder, fileName);


                Files.copy(input, uploadedFile.toPath());


                String decryptedFilePath = uploadDir + "/decript_" + fileName;
                decryptFile(uploadedFile.getPath(), decryptedFilePath, privateKey);

                return "{\"status\":\"success\", \"message\":\"File uploaded and decrypted successfully.\", \"path\":\"" + uploadedFile.getAbsolutePath() + "\"}";
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "{\"status\":\"error\", \"message\":\"File upload and decryption failed: " + e.getMessage() + "\"}";
            }
        });
    }

    private static void handleFileUploadEncrypt() {
        post("/upload/encript", (req, res) -> {

            PublicKey publicKey = loadPublicKey("public.key");
            PrivateKey privateKey = loadPrivateKey("private.key");

            String uploadDir = UPLOAD_DIR;
            File uploadFolder = new File(uploadDir);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs();
            }


            req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
            try (InputStream input = req.raw().getPart("file").getInputStream()) {
                String fileName = Paths.get(req.raw().getPart("file").getSubmittedFileName()).getFileName().toString();
                File uploadedFile = new File(uploadFolder, fileName);


                Files.copy(input, uploadedFile.toPath());


                String encryptedFilePath = uploadDir + "/encript_" + fileName;
                encryptFile(uploadedFile.getPath(), encryptedFilePath, publicKey);

                return "{\"status\":\"success\", \"message\":\"File uploaded and encrypted successfully.\", \"path\":\"" + uploadedFile.getAbsolutePath() + "\"}";
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "{\"status\":\"error\", \"message\":\"File upload and encryption failed: " + e.getMessage() + "\"}";
            }
        });
    }
}
