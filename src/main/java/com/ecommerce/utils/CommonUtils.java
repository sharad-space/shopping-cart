package com.ecommerce.utils;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.ecommerce.model.ProductOrder;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class CommonUtils {
	
	@Autowired
	private  JavaMailSender mailSender;
	
	public  Boolean sendMail(String url,String reciepentEmail) throws UnsupportedEncodingException, MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper=new MimeMessageHelper(message);
		helper.setFrom("sharadkm1997@gmail.com", "Shopping cart");
		helper.setTo(reciepentEmail);
		
		String subject = "Password Reset Request";
	    String content = "<p>Hello ,</p>"
	            + "<p>We received a request to reset your password. Click the link below to reset your password:</p>"
	            + "<p><a href=\"" + url + "\">Reset Password</a></p>"
	            + "<p>If you didn't request a password reset, you can ignore this email. Your password will remain unchanged.</p>"
	            + "<p>Thank you,<br>Your Company Support Team</p>";
	    helper.setSubject(subject);
	    helper.setText(content, true);
	    mailSender.send(message);
		
		return true;
	}

	public static  String generateUrl(HttpServletRequest request) {
		
//		http://localhost:8080/forgot-password
		
		String siteUrl = request.getRequestURL().toString();
	
		return siteUrl.replace(request.getServletPath(), "");
	}
	
	String msg=null;
	         
	          
	public Boolean sendMailForProductOrder(ProductOrder orders,String status) throws Exception {
		msg="<p>Hello [[name]], </p><br><p>Thank you Order [[orderStatus]].</p>"
		         +"<p>Product Details: </p>"
				 +"<p>Name: [[productName]] </p>"
		         +"<p>Category: [[category]] </p>"
				 +"<p>Quantity: [[quantity]] </p>"
		         +"<p>Price: [[price]] </p>"
				 +"<p>Payment type: [[paymentType]]</p>";
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper=new MimeMessageHelper(message);
		helper.setFrom("sharadkm1997@gmail.com", "Shopping cart");
		helper.setTo(orders.getOrderAdress().getEmail());
		msg=msg.replace("[[name]]", orders.getOrderAdress().getFirstName());
		msg=msg.replace("[[orderStatus]]", status);
		msg=msg.replace("[[productName]]", orders.getProduct().getTitle());
		msg=msg.replace("[[category]]", orders.getProduct().getCategory());
		msg=msg.replace("[[quantity]]", orders.getQuantity().toString());
		msg=msg.replace("[[price]]", orders.getPrice().toString());
		msg=msg.replace("[[paymentType]]", orders.getPaymentType());
		
		
		
		String subject = "Product Status";
	   
	    helper.setSubject(subject);
	    helper.setText(msg, true);
	    mailSender.send(message);
	    
	    return true;
		
	}

}
