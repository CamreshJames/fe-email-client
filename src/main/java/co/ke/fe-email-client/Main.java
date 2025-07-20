package co.ke.fe_email_client;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Print ASCII art header
        AsciiArt.printTatuaLogo();
        AsciiArt.printEmailIcon();
        
        try {
            // Load configuration
            ConfigurationManager configManager = new ConfigurationManager();
            
            // Get SMTP configuration
            ConfigurationManager.SmtpConfig smtpConfig = configManager.getSmtpConfig();
            
            // Initialize email service
            EmailService emailService = new EmailService(smtpConfig);
            
            // Get active recipients and templates
            List<ConfigurationManager.Recipient> recipients = configManager.getActiveRecipients();
            List<ConfigurationManager.EmailTemplate> templates = configManager.getActiveTemplates();
            
            // Initialize template processor
            EmailTemplateProcessor processor = new EmailTemplateProcessor();
            
            // Send emails for each active template
            for (ConfigurationManager.EmailTemplate template : templates) {
                sendEmailsForTemplate(emailService, processor, recipients, template);
            }
            
            AsciiArt.printSuccessMessage();
            
        } catch (Exception e) {
            AsciiArt.printErrorMessage();
            System.err.println("Error details: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void sendEmailsForTemplate(EmailService emailService, EmailTemplateProcessor processor,
            List<ConfigurationManager.Recipient> recipients, ConfigurationManager.EmailTemplate template) 
            throws IOException, MessagingException {
        
        AsciiArt.printSendingEmail(template.name + " Email");
        
        // Load template content
        String templateContent = emailService.loadTemplate(template.path);
        
        // Process and send email for each recipient individually
        for (ConfigurationManager.Recipient recipient : recipients) {
            String processedTemplate = processTemplateForRecipient(processor, templateContent, template.name, recipient);
            
            // Send to individual recipient
            List<ConfigurationManager.Recipient> singleRecipient = List.of(recipient);
            emailService.sendEmailToRecipients(singleRecipient, template.subject, processedTemplate);
        }
    }
    
    private static String processTemplateForRecipient(EmailTemplateProcessor processor, String templateContent, 
            String templateName, ConfigurationManager.Recipient recipient) {
        
        // Extract first name from full name (simple approach)
        String firstName = extractFirstName(recipient.name);
        
        // Process template based on type with recipient-specific data
        switch (templateName.toLowerCase()) {
            case "welcome":
                return processor.processWelcomeTemplate(templateContent, firstName);
            case "trial-expiration":
                // Use generic/sample data for trial metrics - in real app this would come from database
                return processor.processTrialExpirationTemplate(
                    templateContent, 
                    firstName, 
                    "150", 
                    "3.2 hours", 
                    "4.5", 
                    "12.0"
                );
            case "product-update":
                return processor.processProductUpdateTemplate(templateContent);
            default:
                return templateContent;
        }
    }
    
    private static String extractFirstName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "Valued Customer";
        }
        String[] nameParts = fullName.trim().split("\\s+");
        return nameParts[0];
    }
} 
