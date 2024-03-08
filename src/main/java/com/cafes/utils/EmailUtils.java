package com.cafes.utils;

import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailUtils {
	@Autowired
    private JavaMailSender javaMailSender;
	
	public void sendSimpleMessage(String to, String subject, String text, List<String> ccList) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("aryanbrave2017@gmail.com");
	    message.setTo(to);
	    message.setSubject(subject);
	    message.setText(text);

	    // Set CC addresses
	    if (ccList != null && !ccList.isEmpty()) {
	        String[] ccArray = ccList.toArray(new String[ccList.size()]);
	        message.setCc(ccArray);
	    }

	    javaMailSender.send(message);
	}
	
	//
	public void  forgotMail(String to, String subject, String password) throws MessagingException {
		
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		helper.setFrom("aryanbrave2017@gmail.com");
		helper.setTo(to);
		helper.setSubject(subject);
		String htmlMsg = "<p><b>Your Login details for Cafe Management System</b><br><b>Email: </b> " + to + " <br><b>Password: </b> " + password + "<br><a href=\"http://localhost:4200/\">Click here to login</a></p>";
		message.setContent(htmlMsg, "text/html");
		
		javaMailSender.send(message);
		
		
	}
	
}
