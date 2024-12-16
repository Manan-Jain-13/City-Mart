package com.ecom.util;
import com.ecom.entity.ProductOrder;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import java.io.UnsupportedEncodingException;

@Component
public class CommonUtil {
    @Autowired
    private JavaMailSender mailSender;

    public Boolean sendMail(String url, String recipientEmail) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom("mananjain132003@gmail.com", "City Mart");
        helper.setTo(recipientEmail);
        String content = "<p>Hello,</p>"
                + "<p>You have requested to change your password</p>"
                + "<p>Click the link given below to change your password:</p>"
                + "<p><a href=\"" + url + "\">Change My Password</a></p>";
        helper.setSubject("Password Reset");
        helper.setText(content, true);
        mailSender.send(message);
        return true;
    }

    public static String generateURL(HttpServletRequest request) {
        String siteUrl = request.getRequestURL().toString();
        return siteUrl.replace(request.getServletPath(), "");
    }

    String msg = null;
    // Sending Email to user for updates with enhanced styling
    public Boolean sendMailForProductOrder(ProductOrder order, String status) throws Exception {
        msg = """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f7f7f7;">
                <div style="background-color: #4CAF50; color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0;">
                    <h2 style="margin: 0; font-size: 24px;">City Mart</h2>
                </div>
                
                <div style="background-color: white; padding: 20px; border-radius: 0 0 5px 5px; box-shadow: 0 2px 5px rgba(0,0,0,0.1);">
                    <h3 style="color: #333; font-size: 18px;">Hello [[name]],</h3>
                    
                    <p style="color: #555; font-size: 16px;">
                        Thank you for shopping with us! Your order has been 
                        <span style="background-color: #4CAF50; color: white; padding: 5px 10px; border-radius: 15px; font-weight: bold;">[[orderStatus]]</span>
                    </p>
                    
                    <div style="margin: 20px 0; border: 1px solid #ddd; padding: 15px; border-radius: 5px;">
                        <h4 style="color: #2c3e50; margin-top: 0;">Order Details</h4>
                        
                        <div style="margin: 10px 0; padding: 8px 0; border-bottom: 1px solid #eee;">
                            <strong style="color: #2c3e50;">Product:</strong> 
                            <span style="color: #34495e; float: right;">[[productName]]</span>
                        </div>
                        
                        <div style="margin: 10px 0; padding: 8px 0; border-bottom: 1px solid #eee;">
                            <strong style="color: #2c3e50;">Category:</strong>
                            <span style="color: #34495e; float: right;">[[category]]</span>
                        </div>
                        
                        <div style="margin: 10px 0; padding: 8px 0; border-bottom: 1px solid #eee;">
                            <strong style="color: #2c3e50;">Quantity:</strong>
                            <span style="color: #34495e; float: right;">[[quantity]]</span>
                        </div>
                        
                        <div style="margin: 10px 0; padding: 8px 0; border-bottom: 1px solid #eee;">
                            <strong style="color: #2c3e50;">Price:</strong>
                            <span style="color: #34495e; float: right;">₹[[price]]</span>
                        </div>
                        
                        <div style="margin: 10px 0; padding: 8px 0;">
                            <strong style="color: #2c3e50;">Payment Type:</strong>
                            <span style="color: #34495e; float: right;">[[paymentType]]</span>
                        </div>
                    </div>
                    
                    <p style="color: #555; font-size: 16px; line-height: 1.5;">
                        If you have any questions about your order, please don't hesitate to contact our customer service team.
                    </p>
                </div>
                
                <div style="text-align: center; margin-top: 20px; padding-top: 20px; border-top: 1px solid #ddd; color: #666;">
                    <p style="font-size: 14px; color: #666; margin: 5px 0;">This is an automated message, please do not reply to this email.</p>
                    <p style="font-size: 12px; color: #888;">&copy; 2024 City Mart. All rights reserved.</p>
                </div>
            </div>
        """;

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom("mananjain132003@gmail.com", "City Mart");
        helper.setTo(order.getOrderAddress().getEmail());
        msg = msg.replace("[[name]]", order.getOrderAddress().getFirstName());
        msg = msg.replace("[[orderStatus]]", status);
        msg = msg.replace("[[productName]]", order.getProduct().getTitle());
        msg = msg.replace("[[category]]", order.getProduct().getCategory());
        msg = msg.replace("[[quantity]]", order.getQuantity().toString());
        msg = msg.replace("[[price]]", order.getPrice().toString());
        msg = msg.replace("[[paymentType]]", order.getPaymentType());
        helper.setSubject("Order Status Update - City Mart");
        helper.setText(msg, true);
        mailSender.send(message);
        return true;
    }
}