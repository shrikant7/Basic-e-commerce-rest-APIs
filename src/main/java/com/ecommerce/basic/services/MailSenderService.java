package com.ecommerce.basic.services;

import com.ecommerce.basic.models.OrderDetail;
import com.ecommerce.basic.models.OrderItem;
import com.ecommerce.basic.models.Otp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * @author Shrikant Sharma
 */

@Service
public class MailSenderService {
	@Autowired
	private JavaMailSender javaMailSender;

	@Value("${mail.admin}")
	private String adminMailAddress;
	@Value("${spring.mail.username}")
	private String mailSenderAddress;
	@Value("${mail-sendgrid-password}")
	private String sendGridPassword;

	//send mail to admin about users checkout details
	public void sendMailToAdminLocal(OrderItem orderItem) {
		StringBuilder body = new StringBuilder("Dear Admin,\n\n");
		body.append("User "+orderItem.getUser().getUsername()+" has placed an new order on your platform and his/her order details are as below:\n\n");
		for(OrderDetail detail : orderItem.getOrderDetails()) {
			body.append(detail.shortenToString()+"\n");
		}
		body.append("\nAnd order item details are as below:\n").append(orderItem.shortenToString()).append("\n\nThanks\nEcommerce Backend");

		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setTo(adminMailAddress);
		mail.setFrom(mailSenderAddress);
		mail.setSubject(orderItem.getUser().getUsername()+" has placed an order.");
		mail.setText(body.toString());

		javaMailSender.send(mail);
	}

	public void sendSimpleMailToAdminCloud(OrderItem orderItem) {
		StringBuilder body = new StringBuilder("Dear Admin,\n\n");
		body.append("User "+orderItem.getUser().getUsername()+" has placed an new order on your platform and his/her order details are as below:\n\n");
		for(OrderDetail detail : orderItem.getOrderDetails()) {
			body.append(detail.shortenToString()+"\n");
		}
		body.append("\nAnd order item details are as below:\n").append(orderItem.shortenToString()).append("\n\nThanks\nEcommerce Backend");

		Session session = getSendGridSession();
		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(mailSenderAddress, "Ecommerce Backend"));
			msg.addRecipient(Message.RecipientType.TO,
					new InternetAddress(adminMailAddress, "Mr. Admin"));
			msg.setSubject(orderItem.getUser().getUsername()+" has placed an order.");
			msg.setText(body.toString());
			Transport.send(msg);
		} catch (MessagingException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void sendSimpleMailOtpToUserCloud(Otp otp) {
		StringBuilder body = new StringBuilder("Dear "+otp.getUser().getUsername()+",\n\n")
								.append("Your OTP is \""+otp.getOtp()+"\" and will be valid only for next 10 minutes.\n")
								.append("If you have not requested this, please ignore.\n\n")
								.append("Thanks\nEcommerce Backend");

		Session session = getSendGridSession();
		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(mailSenderAddress, "Ecommerce Backend"));
			msg.addRecipient(Message.RecipientType.TO,
					new InternetAddress(otp.getUser().getUserInfo().getEmail(), "Mr. "+otp.getUser().getUsername()));
			msg.setSubject("Your otp from Basic Ecommerce.");
			msg.setText(body.toString());
			Transport.send(msg);
		} catch (MessagingException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private Session getSendGridSession() {
		Properties prop = new Properties();
		prop.put("mail.smtp.auth", true);
		prop.put("mail.smtp.starttls.enable", "true");
		prop.put("mail.smtp.host", "smtp.sendgrid.net");
		prop.put("mail.smtp.port", "587");
		prop.put("mail.smtp.ssl.trust", "smtp.sendgrid.net");
		return Session.getInstance(prop, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("apikey", sendGridPassword);
			}
		});
	}
}
