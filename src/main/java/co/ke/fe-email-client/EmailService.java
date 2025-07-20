package co.ke.fe_email_client;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;

/**
 * NIS-Level Email Service with Enhanced Security and Logging
 */
public class EmailService {
    private final ConfigurationManager.SmtpConfig smtpConfig;
    private final AuditLogger auditLogger;
    
    public EmailService(ConfigurationManager.SmtpConfig smtpConfig) {
        this.smtpConfig = smtpConfig;
        this.auditLogger = new AuditLogger();
    }

    /**
     * Sends email to multiple recipients with audit logging
     */
    public void sendEmailToRecipients(List<ConfigurationManager.Recipient> recipients, 
                                    String subject, String htmlContent) throws MessagingException {
        
        auditLogger.logInfo("Starting email send operation to " + recipients.size() + " recipients");
        
        Properties props = createSmtpProperties();
        Session session = createSecureSession(props);
        
        int successCount = 0;
        int failureCount = 0;
        
        for (ConfigurationManager.Recipient recipient : recipients) {
            try {
                sendSingleEmail(session, recipient, subject, htmlContent);
                successCount++;
                auditLogger.logInfo("[OK] Email sent successfully to: " + recipient.name + " (" + recipient.email + ")");
            } catch (MessagingException e) {
                failureCount++;
                auditLogger.logError("[XX] Failed to send email to: " + recipient.name + " (" + recipient.email + ")", e);
            }
        }
        
        auditLogger.logInfo("Email operation completed. Success: " + successCount + ", Failures: " + failureCount);
    }
    
    /**
     * Creates SMTP properties with security settings
     */
    private Properties createSmtpProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.host", smtpConfig.host);
        props.put("mail.smtp.port", smtpConfig.port);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.connectiontimeout", "30000");
        props.put("mail.smtp.timeout", "30000");
        props.put("mail.smtp.writetimeout", "30000");
        
        if (smtpConfig.useSSL) {
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3");
        }
        
        if (smtpConfig.useTLS) {
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3");
        }
        
        // Enhanced security settings
        props.put("mail.smtp.ssl.checkserveridentity", "true");
        props.put("mail.smtp.ssl.trust", smtpConfig.host);
        
        return props;
    }
    
    /**
     * Creates secure email session with authentication
     */
    private Session createSecureSession(Properties props) {
        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpConfig.username, smtpConfig.password);
            }
        });
    }
    
    /**
     * Sends single email with enhanced security headers
     */
    private void sendSingleEmail(Session session, ConfigurationManager.Recipient recipient, 
                               String subject, String htmlContent) throws MessagingException {
        
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(smtpConfig.username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient.email));
        message.setSubject(subject);
        
        // Enhanced email headers for security and deliverability
        message.setHeader("X-Mailer", "Tatua Email Client v1.0 (NIS-Level)");
        message.setHeader("X-Priority", "3");
        message.setHeader("X-MSMail-Priority", "Normal");
        message.setHeader("Importance", "Normal");
        message.setHeader("Content-Type", "text/html; charset=UTF-8");
        
        // Set HTML content
        message.setContent(htmlContent, "text/html; charset=utf-8");
        
        // Send with retry logic
        int maxRetries = 3;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                Transport.send(message);
                break;
            } catch (MessagingException e) {
                if (attempt == maxRetries) {
                    throw e;
                }
                auditLogger.logWarning("Retry attempt " + attempt + " for " + recipient.email);
                try {
                    Thread.sleep(2000 * attempt); // Exponential backoff
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new MessagingException("Interrupted during retry", ie);
                }
            }
        }
    }

    /**
     * Loads email template from resources
     */
    public String loadTemplate(String templatePath) throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(templatePath)) {
            if (inputStream == null) {
                throw new IOException("Template not found: " + templatePath);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
    
    /**
     * Simple audit logger for email operations
     */
    private static class AuditLogger {
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        public void logInfo(String message) {
            System.out.println("[" + LocalDateTime.now().format(formatter) + "] INFO: " + message);
        }
        
        public void logWarning(String message) {
            System.out.println("[" + LocalDateTime.now().format(formatter) + "] WARN: " + message);
        }
        
        public void logError(String message, Exception e) {
            System.err.println("[" + LocalDateTime.now().format(formatter) + "] ERROR: " + message);
            if (e != null) {
                System.err.println("Exception: " + e.getMessage());
            }
        }
    }
}