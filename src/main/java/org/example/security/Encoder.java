package org.example.security;

import org.example.config.ConfigLoader;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.Serializable;
import java.security.spec.KeySpec;
import java.util.Base64;

public class Encoder implements Serializable {
    private static final long serialVersionUID = 5486865543976730876L;
    // For production
    //private static final String key = System.getenv("PROD_ENCODER_KEY");
    //private static final String salt = System.getenv("PROD_ENCODER_SALT");

    // Para pruebas en local host
    private static final String key;
    private static final String salt;

    static{
        ConfigLoader configLoader = ConfigLoader.getInstance();
        if(!configLoader.isEmpty()){
            // Para pruebas en local host
            key = configLoader.getProperty("encoder.key");
            salt =configLoader.getProperty("encoder.salt");
        }else {
            // For production
            key = System.getenv("PROD_ENCODER_KEY");
            salt = System.getenv("PROD_ENCODER_SALT");
        }
    }

    private SecretKey secretKeyTemp;

    public Encoder(){
        SecretKeyFactory secretKeyFactory;
        KeySpec keySpec;
        try{
            secretKeyFactory=SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            keySpec= new PBEKeySpec(key.toCharArray(), salt.getBytes(), 65536,256);
            secretKeyTemp=secretKeyFactory.generateSecret(keySpec);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String encrypt(String contrasena){
        byte[] iv = new byte[16];
        try{
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            SecretKeySpec secretKey = new SecretKeySpec(secretKeyTemp.getEncoded(),"AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey,ivParameterSpec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(contrasena.getBytes("UTF-8")));
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String decrypt(String contrasena){
        byte[] iv = new byte[16];
        try{
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            SecretKeySpec secretKey = new SecretKeySpec(secretKeyTemp.getEncoded(),"AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey,ivParameterSpec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(contrasena)));
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
