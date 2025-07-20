package co.ke.fe_email_client;

import org.apache.commons.codec.binary.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKeyFactory;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

/**
 * NIS-Level Encryption Utility for Email Configuration
 * Uses AES-256-GCM with PBKDF2 key derivation
 */
public class EncryptionUtil {
    
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String KEY_DERIVATION = "PBKDF2WithHmacSHA256";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;
    private static final int PBKDF2_ITERATIONS = 100000;
    private static final int SALT_LENGTH = 32;
    private static final int KEY_LENGTH = 256;
    
    private static final SecureRandom secureRandom = new SecureRandom();
    
    /**
     * Encrypts text using AES-256-GCM with PBKDF2 key derivation
     */
    public static String encrypt(String plainText, String password) throws Exception {
        // Generate salt and IV
        byte[] salt = new byte[SALT_LENGTH];
        byte[] iv = new byte[GCM_IV_LENGTH];
        secureRandom.nextBytes(salt);
        secureRandom.nextBytes(iv);
        
        // Derive key from password
        SecretKey key = deriveKey(password, salt);
        
        // Encrypt
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec);
        
        byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        
        // Combine salt + iv + ciphertext
        byte[] encryptedData = new byte[SALT_LENGTH + GCM_IV_LENGTH + cipherText.length];
        System.arraycopy(salt, 0, encryptedData, 0, SALT_LENGTH);
        System.arraycopy(iv, 0, encryptedData, SALT_LENGTH, GCM_IV_LENGTH);
        System.arraycopy(cipherText, 0, encryptedData, SALT_LENGTH + GCM_IV_LENGTH, cipherText.length);
        
        return Base64.encodeBase64String(encryptedData);
    }
    
    /**
     * Decrypts text using AES-256-GCM with PBKDF2 key derivation
     */
    public static String decrypt(String encryptedText, String password) throws Exception {
        byte[] encryptedData = Base64.decodeBase64(encryptedText);
        
        // Extract salt, IV, and ciphertext
        byte[] salt = new byte[SALT_LENGTH];
        byte[] iv = new byte[GCM_IV_LENGTH];
        byte[] cipherText = new byte[encryptedData.length - SALT_LENGTH - GCM_IV_LENGTH];
        
        System.arraycopy(encryptedData, 0, salt, 0, SALT_LENGTH);
        System.arraycopy(encryptedData, SALT_LENGTH, iv, 0, GCM_IV_LENGTH);
        System.arraycopy(encryptedData, SALT_LENGTH + GCM_IV_LENGTH, cipherText, 0, cipherText.length);
        
        // Derive key from password
        SecretKey key = deriveKey(password, salt);
        
        // Decrypt
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec);
        
        byte[] plainText = cipher.doFinal(cipherText);
        return new String(plainText, StandardCharsets.UTF_8);
    }
    
    /**
     * Derives encryption key from password using PBKDF2
     */
    private static SecretKey deriveKey(String password, byte[] salt) throws Exception {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_DERIVATION);
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }
    
    /**
     * Generates a secure random password for encryption
     */
    public static String generateSecurePassword() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return Base64.encodeBase64String(randomBytes);
    }
    
    /**
     * Validates encryption strength
     */
    public static boolean validateEncryptionStrength() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(KEY_LENGTH);
            return true;
        } catch (NoSuchAlgorithmException e) {
            return false;
        }
    }
}