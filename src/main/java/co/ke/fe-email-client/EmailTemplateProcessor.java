package co.ke.fe_email_client;

public class EmailTemplateProcessor {
    
    public String processWelcomeTemplate(String template, String firstName) {
        return template.replace("[First Name]", firstName);
    }
    
    public String processTrialExpirationTemplate(String template, String firstName, 
            String ticketCount, String responseTime, String satisfaction, String timeSaved) {
        return template
                .replace("[First Name]", firstName)
                .replace("[Number]", ticketCount)
                .replace("[Time]", responseTime)
                .replace("[Rating]", satisfaction)
                .replace("[Hours]", timeSaved);
    }
    
    public String processProductUpdateTemplate(String template) {
        // Product update template doesn't have placeholders in this version
        return template;
    }
}