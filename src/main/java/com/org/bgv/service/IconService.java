package com.org.bgv.service;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class IconService {
    
    public String getIconForVerification(String verificationName) {
        if (verificationName == null) return "📝";
        
        log.info("IconService::::::::::::::::::verificationName::{}",verificationName);
        
        return switch (verificationName.toLowerCase()) {
            case "work experience" -> "💼";
            case "education" -> "🎓";
            case "address" -> "🏠";
            case "court", "court check" -> "⚖️";
            case "identity" -> "🆔";
            case "reference check" -> "👥";
            case "drug test" -> "💊";
            case "credit check" -> "💳";
            case "professional license" -> "📜";
            case "social media" -> "🌐";
            case "global database" -> "🌍";
            case "sanctions" -> "🚫";
            case "politically exposed person" -> "👑";
            case "adverse media" -> "📰";
            default -> "✅";
        };
    }
    
    public String getIconForStatus(String status) {
        return switch (status.toLowerCase()) {
            case "verified", "completed" -> "✅";
            case "in_progress", "pending" -> "⏳";
            case "failed", "rejected" -> "❌";
            case "awaiting" -> "⏰";
            default -> "📝";
        };
    }
    
    public String getIconForActivity(String activityType) {
        return switch (activityType.toLowerCase()) {
            case "profile_created" -> "👤";
            case "package_selected" -> "📦";
            case "document_uploaded" -> "📄";
            case "verification_started" -> "🚀";
            case "check_completed" -> "✅";
            case "status_changed" -> "🔄";
            case "note_added" -> "📝";
            case "email_sent" -> "📧";
            case "sms_sent" -> "📱";
            default -> "📝";
        };
    }
}