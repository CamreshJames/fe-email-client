# Tatua Email Marketing Client

A secure, enterprise-grade Java email marketing client with NIS-level encryption and professional HTML email templates.

## 🚀 Features

### Security & Encryption
- **AES-256-GCM Encryption** with PBKDF2 key derivation (100,000 iterations)
- **Automatic Configuration Encryption** on first run
- **Secure Password Generation** with cryptographically secure random
- **NIS-Level Security Standards** implementation

### Email Marketing
- **Professional HTML Templates** with dark theme and orange accents
- **Responsive Email Design** optimized for all email clients
- **Template Processing** with dynamic content replacement
- **Multi-recipient Support** with individual personalization

### Email Templates Included
- **Welcome Email** - Onboard new users with style
- **Trial Expiration** - Convert trial users to paid plans
- **Product Newsletter** - Keep users informed about updates

### Technical Features
- **SMTP Configuration** with SSL/TLS support
- **Audit Logging** with timestamps and detailed tracking
- **Retry Mechanism** with exponential backoff
- **Connection Timeout Management**
- **XML-based Configuration** with encryption support

## 📋 Prerequisites

- **Java 17** or higher
- **Maven 3.6.3** or higher
- **SMTP Server Access** (Gmail, Outlook, etc.)

## 🛠️ Installation & Setup

### 1. Clone the Repository
```bash
git clone <repository-url>
cd fe-email-client
```

### 2. Build the Project
```bash
mvn clean compile
```

### 3. Configure Email Settings
Edit `email-config.xml` with your SMTP settings:
```xml
<smtpSettings>
    <host>smtp.gmail.com</host>
    <port>587</port>
    <username>your-email@gmail.com</username>
    <password>your-app-password</password>
    <useSSL>false</useSSL>
    <useTLS>true</useTLS>
</smtpSettings>
```

### 4. Add Recipients
Configure your email recipients in `email-config.xml`:
```xml
<recipients>
    <recipient>
        <name>John Doe</name>
        <email>john@example.com</email>
        <type>PRIMARY</type>
        <active>true</active>
    </recipient>
</recipients>
```

### 5. Run the Application
```bash
mvn exec:java -Dexec.mainClass="co.ke.fe_email_client.Main"
```

Or build and run the JAR:
```bash
mvn package
java -jar target/Main-fat.jar
```

## 🔐 Security Features

### First Run Encryption
On first execution, the application will:
1. Detect clear-text configuration
2. Prompt for master password (or auto-generate)
3. Encrypt sensitive data (SMTP credentials)
4. Save encrypted configuration

### Subsequent Runs
- Prompts for master password to decrypt configuration
- All sensitive data remains encrypted at rest
- Uses industry-standard encryption algorithms

### Encryption Specifications
- **Algorithm**: AES-256-GCM
- **Key Derivation**: PBKDF2WithHmacSHA256
- **Iterations**: 100,000
- **Salt Length**: 32 bytes
- **IV Length**: 12 bytes (GCM)

## 📧 Email Templates

### Template Customization
Templates are located in `src/main/resources/email-marketing/`:
- `welcome-email.html` - User onboarding
- `trial-expiration-email.html` - Trial conversion
- `product-update-newsletter.html` - Product updates

### Template Variables
The system supports dynamic content replacement:
- `[First Name]` - Recipient's first name
- `[Number]` - Ticket count (trial emails)
- `[Time]` - Response time metrics
- `[Rating]` - Satisfaction ratings
- `[Hours]` - Time saved metrics

### Design Features
- **Dark Theme** with professional appearance
- **Orange Accents** for calls-to-action
- **Mobile Responsive** design
- **Email Client Compatible** (Outlook, Gmail, etc.)
- **Semantic HTML** structure

## 🏗️ Project Structure

```
fe-email-client/
├── src/main/java/co/ke/fe-email-client/
│   ├── Main.java                    # Application entry point
│   ├── EmailService.java            # Email sending service
│   ├── ConfigurationManager.java    # XML config management
│   ├── EncryptionUtil.java         # Encryption utilities
│   ├── EmailTemplateProcessor.java  # Template processing
│   └── AsciiArt.java               # CLI interface art
├── src/main/resources/email-marketing/
│   ├── welcome-email.html
│   ├── trial-expiration-email.html
│   └── product-update-newsletter.html
├── email-config.xml                 # Main configuration
├── .email-master.key               # Master key hint
└── pom.xml                         # Maven configuration
```

## 🔧 Configuration Reference

### SMTP Settings
```xml
<smtpSettings>
    <host>smtp.gmail.com</host>           <!-- SMTP server -->
    <port>587</port>                      <!-- SMTP port -->
    <username>ENC:...</username>          <!-- Encrypted username -->
    <password>ENC:...</password>          <!-- Encrypted password -->
    <useSSL>false</useSSL>               <!-- SSL encryption -->
    <useTLS>true</useTLS>                <!-- TLS encryption -->
    <connectionTimeout>30000</connectionTimeout>
    <readTimeout>30000</readTimeout>
</smtpSettings>
```

### Template Configuration
```xml
<templates>
    <template>
        <name>welcome</name>
        <path>email-marketing/welcome-email.html</path>
        <subject>Welcome to Tatua - Let's Get Started!</subject>
        <active>true</active>
    </template>
</templates>
```

## 📊 Logging & Monitoring

### Audit Trail
The application provides comprehensive logging:
- Email send attempts and results
- Configuration access events
- Encryption/decryption operations
- Error tracking with timestamps

### Log Format
```
[2025-01-20 10:30:45] INFO: Starting email send operation to 5 recipients
[2025-01-20 10:30:46] INFO: [OK] Email sent successfully to: John Doe (john@example.com)
[2025-01-20 10:30:47] ERROR: [XX] Failed to send email to: Jane Smith (jane@example.com)
```

## 🚨 Troubleshooting

### Common Issues

**"Configuration file not found"**
- Ensure `email-config.xml` exists in the project root
- Check file permissions

**"Authentication failed"**
- Verify SMTP credentials
- For Gmail: Use App Passwords, not regular password
- Check if 2FA is enabled

**"Template not found"**
- Verify template paths in configuration
- Ensure HTML files exist in `src/main/resources/email-marketing/`

**"Decryption failed"**
- Check master password
- Ensure configuration wasn't corrupted

### Gmail Setup
1. Enable 2-Factor Authentication
2. Generate App Password: Google Account → Security → App Passwords
3. Use App Password in configuration, not regular password

## 🔒 Security Best Practices

### Configuration Security
- Never commit unencrypted credentials
- Use strong master passwords
- Regularly rotate SMTP credentials
- Keep master password secure and backed up

### Deployment Security
- Run with minimal privileges
- Secure configuration files (600 permissions)
- Monitor audit logs
- Use secure SMTP connections (TLS/SSL)

## 🛣️ Roadmap & Future Improvements

### Planned Enhancements
- [ ] Unit test coverage (target: 80%+)
- [ ] Async email processing
- [ ] Connection pooling
- [ ] Rate limiting
- [ ] Template editor UI
- [ ] Email analytics
- [ ] Bounce handling
- [ ] A/B testing support

### Architecture Improvements
- [ ] Dependency injection framework
- [ ] Builder pattern implementation
- [ ] Repository pattern for configuration
- [ ] Proper exception hierarchy
- [ ] Configuration validation

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Add comprehensive tests
4. Follow existing code style
5. Submit a pull request

## 📞 Support

For issues and questions:
- Create an issue in the repository
- Check troubleshooting section
- Review configuration examples

---

**⚠️ Important**: This is a development version. Additional testing and security review recommended before production use.
