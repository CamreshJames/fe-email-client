package co.ke.fe_email_client;

import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * NIS-Level Configuration Manager for Email Client
 * Handles encrypted XML configuration with automatic encryption on first run
 */
public class ConfigurationManager {
    
    private static final String CONFIG_FILE = "email-config.xml";
    private static final String MASTER_KEY_FILE = ".email-master.key";
    
    private Document configDocument;
    private String masterPassword;
    private boolean isEncrypted;
    
    public ConfigurationManager() throws Exception {
        loadConfiguration();
    }
    
    /**
     * Loads and processes configuration file
     */
    private void loadConfiguration() throws Exception {
        File configFile = new File(CONFIG_FILE);
        if (!configFile.exists()) {
            throw new RuntimeException("Configuration file not found: " + CONFIG_FILE);
        }
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        configDocument = builder.parse(configFile);
        
        Element root = configDocument.getDocumentElement();
        String type = root.getAttribute("type");
        
        if ("CLEAR-TEXT".equals(type)) {
            System.out.println("⚠️  Configuration is in CLEAR-TEXT mode. Encrypting for security...");
            encryptConfiguration();
        } else if ("ENCRYPTED".equals(type)) {
            isEncrypted = true;
            loadMasterPassword();
            decryptSensitiveData();
        }
    }
    
    /**
     * Encrypts configuration on first run
     */
    private void encryptConfiguration() throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.print("🔐 Enter master password for encryption (or press Enter for auto-generated): ");
        String password = scanner.nextLine().trim();
        
        if (password.isEmpty()) {
            password = EncryptionUtil.generateSecurePassword();
            System.out.println("🔑 Auto-generated master password: " + password);
            System.out.println("⚠️  SAVE THIS PASSWORD SECURELY - YOU'LL NEED IT TO RUN THE APPLICATION!");
        }
        
        masterPassword = password;
        
        // Encrypt sensitive fields
        encryptSensitiveFields();
        
        // Update configuration type
        Element root = configDocument.getDocumentElement();
        root.setAttribute("type", "ENCRYPTED");
        
        // Save encrypted configuration
        saveConfiguration();
        
        // Save master key hint (not the actual password)
        saveMasterKeyHint();
        
        isEncrypted = true;
        System.out.println("✅ Configuration encrypted successfully!");
    }
    
    /**
     * Encrypts sensitive fields in the configuration
     */
    private void encryptSensitiveFields() throws Exception {
        // Encrypt SMTP password
        NodeList passwordNodes = configDocument.getElementsByTagName("password");
        for (int i = 0; i < passwordNodes.getLength(); i++) {
            Element passwordElement = (Element) passwordNodes.item(i);
            String plainPassword = passwordElement.getTextContent();
            if (!plainPassword.isEmpty() && !plainPassword.startsWith("ENC:")) {
                String encryptedPassword = EncryptionUtil.encrypt(plainPassword, masterPassword);
                passwordElement.setTextContent("ENC:" + encryptedPassword);
            }
        }
        
        // Encrypt SMTP username
        NodeList usernameNodes = configDocument.getElementsByTagName("username");
        for (int i = 0; i < usernameNodes.getLength(); i++) {
            Element usernameElement = (Element) usernameNodes.item(i);
            String plainUsername = usernameElement.getTextContent();
            if (!plainUsername.isEmpty() && !plainUsername.startsWith("ENC:")) {
                String encryptedUsername = EncryptionUtil.encrypt(plainUsername, masterPassword);
                usernameElement.setTextContent("ENC:" + encryptedUsername);
            }
        }
    }
    
    /**
     * Loads master password for decryption
     */
    private void loadMasterPassword() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("🔐 Enter master password to decrypt configuration: ");
        masterPassword = scanner.nextLine().trim();
    }
    
    /**
     * Decrypts sensitive data from configuration
     */
    private void decryptSensitiveData() throws Exception {
        // This method would decrypt data in memory for use, but keep file encrypted
        // For security, we'll decrypt on-demand when accessing sensitive data
    }
    
    /**
     * Gets decrypted SMTP configuration
     */
    public SmtpConfig getSmtpConfig() throws Exception {
        Element smtpElement = (Element) configDocument.getElementsByTagName("smtpSettings").item(0);
        
        String host = getElementText(smtpElement, "host");
        String port = getElementText(smtpElement, "port");
        String username = getDecryptedValue(getElementText(smtpElement, "username"));
        String password = getDecryptedValue(getElementText(smtpElement, "password"));
        boolean useSSL = Boolean.parseBoolean(getElementText(smtpElement, "useSSL"));
        boolean useTLS = Boolean.parseBoolean(getElementText(smtpElement, "useTLS"));
        
        return new SmtpConfig(host, port, username, password, useSSL, useTLS);
    }
    
    /**
     * Gets list of active recipients
     */
    public List<Recipient> getActiveRecipients() {
        List<Recipient> recipients = new ArrayList<>();
        NodeList recipientNodes = configDocument.getElementsByTagName("recipient");
        
        for (int i = 0; i < recipientNodes.getLength(); i++) {
            Element recipientElement = (Element) recipientNodes.item(i);
            boolean active = Boolean.parseBoolean(getElementText(recipientElement, "active"));
            
            if (active) {
                String name = getElementText(recipientElement, "name");
                String email = getElementText(recipientElement, "email");
                String type = getElementText(recipientElement, "type");
                
                recipients.add(new Recipient(name, email, type));
            }
        }
        
        return recipients;
    }
    
    /**
     * Gets list of active email templates
     */
    public List<EmailTemplate> getActiveTemplates() {
        List<EmailTemplate> templates = new ArrayList<>();
        NodeList templateNodes = configDocument.getElementsByTagName("template");
        
        for (int i = 0; i < templateNodes.getLength(); i++) {
            Element templateElement = (Element) templateNodes.item(i);
            boolean active = Boolean.parseBoolean(getElementText(templateElement, "active"));
            
            if (active) {
                String name = getElementText(templateElement, "name");
                String path = getElementText(templateElement, "path");
                String subject = getElementText(templateElement, "subject");
                
                templates.add(new EmailTemplate(name, path, subject));
            }
        }
        
        return templates;
    }
    
    /**
     * Decrypts encrypted values
     */
    private String getDecryptedValue(String value) throws Exception {
        if (value.startsWith("ENC:")) {
            return EncryptionUtil.decrypt(value.substring(4), masterPassword);
        }
        return value;
    }
    
    /**
     * Helper method to get element text content
     */
    private String getElementText(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return "";
    }
    
    /**
     * Saves configuration to file
     */
    private void saveConfiguration() throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty("indent", "yes");
        
        DOMSource source = new DOMSource(configDocument);
        StreamResult result = new StreamResult(new File(CONFIG_FILE));
        transformer.transform(source, result);
    }
    
    /**
     * Saves master key hint (not actual password)
     */
    private void saveMasterKeyHint() throws Exception {
        // Save a hint or hash, not the actual password
        String hint = "Master key configured on: " + java.time.LocalDateTime.now();
        java.nio.file.Files.write(java.nio.file.Paths.get(MASTER_KEY_FILE), hint.getBytes());
    }
    
    // Inner classes for configuration data
    public static class SmtpConfig {
        public final String host, port, username, password;
        public final boolean useSSL, useTLS;
        
        public SmtpConfig(String host, String port, String username, String password, boolean useSSL, boolean useTLS) {
            this.host = host;
            this.port = port;
            this.username = username;
            this.password = password;
            this.useSSL = useSSL;
            this.useTLS = useTLS;
        }
    }
    
    public static class Recipient {
        public final String name, email, type;
        
        public Recipient(String name, String email, String type) {
            this.name = name;
            this.email = email;
            this.type = type;
        }
    }
    
    public static class EmailTemplate {
        public final String name, path, subject;
        
        public EmailTemplate(String name, String path, String subject) {
            this.name = name;
            this.path = path;
            this.subject = subject;
        }
    }
}