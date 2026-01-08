package com.org.bgv.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.org.bgv.service.EmailService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @GetMapping("/send-test")
    public String sendTestMail(@RequestParam(defaultValue = "test@example.com") String to) {
      //  emailService.sendSimpleMail(to, "MailHog Test", "Hello! This is a test email from Spring Boot.");
        return "Email sent to MailHog inbox!";
    }
}