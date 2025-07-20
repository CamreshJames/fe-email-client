package co.ke.fe_email_client;

public class AsciiArt {
    
    public static void printTatuaLogo() {
        System.out.println("""
            ████████╗ █████╗ ████████╗██╗   ██╗ █████╗ 
            ╚══██╔══╝██╔══██╗╚══██╔══╝██║   ██║██╔══██╗
               ██║   ███████║   ██║   ██║   ██║███████║
               ██║   ██╔══██║   ██║   ██║   ██║██╔══██║
               ██║   ██║  ██║   ██║   ╚██████╔╝██║  ██║
               ╚═╝   ╚═╝  ╚═╝   ╚═╝    ╚═════╝ ╚═╝  ╚═╝
            """);
    }
    
    public static void printEmailIcon() {
        System.out.println("""
            ┌─────────────────────────────────────────┐
            │      E M A I L   M A R K E T I N G      │
            │                                         │
            │      Welcome Email                      │
            │      Trial Expiration                   │
            │      Product Newsletter                 │
            └─────────────────────────────────────────┘
            """);
    }
    
    public static void printSuccessMessage() {
        System.out.println("""
            
            ╔══════════════════════════════════════════╗
            ║      ALL EMAILS SENT SUCCESSFULLY!       ║
            ║                                          ║
            ║                                          ║
            ╚══════════════════════════════════════════╝
            """);
    }
    
    public static void printSendingEmail(String emailType) {
        System.out.println("\n┌" + "─".repeat(50) + "┐");
        System.out.println("│  Sending: " + String.format("%-35s", emailType) + "│");
        System.out.println("└" + "─".repeat(50) + "┘");
    }
    
    public static void printErrorMessage() {
        System.out.println("""
            
            ╔══════════════════════════════════════════╗
            ║       ERROR OCCURRED WHILE SENDING       ║
            ║      Please check your configuration     ║
            ╚══════════════════════════════════════════╝
            """);
    }
}