package com.ecommerce.basic.services;

import com.ecommerce.basic.models.OrderDetail;
import com.ecommerce.basic.models.OrderItem;
import com.ecommerce.basic.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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

	//send mail to admin about users checkout details
	public void sendMailToAdmin(OrderItem orderItem) {
		StringBuilder body = new StringBuilder("Dear Admin,\n\n");
		body.append("User "+orderItem.getUser().getUsername()+" has placed an new order on your platform and his/her order details are as below:\n\n");
		for(OrderDetail detail : orderItem.getOrderDetails()) {
			body.append(detail.shortenToString()+"\n");
		}
		body.append("\nAnd order item details are as below:\n").append(orderItem.shortenToString()).append("\n\nThanks");

		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setTo(adminMailAddress);
		mail.setFrom(mailSenderAddress);
		mail.setSubject(orderItem.getUser().getUsername()+" has placed an order.");
		mail.setText(body.toString());

		javaMailSender.send(mail);
	}
}
