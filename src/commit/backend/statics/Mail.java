package commit.backend.statics;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Mail {
	public static int gmailSend(String mail) {
        String user = "commit.sysop@gmail.com"; 
        String password = "commit12345";   
        int random = 0;
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com"); //구글메일 사용      
        prop.put("mail.smtp.auth", "true"); 
        prop.put("mail.smtp.ssl.enable", "true"); 
        prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        
        Session session = Session.getDefaultInstance(prop, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });
       
        
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));

           
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(mail)); 

            // Subject
            message.setSubject("[commit] 인증 메일입니다."); 
            
            random = (int) ((Math.random()*( 9999-1000) ) + 1000);
            // Text
            message.setText("인증번호 : " + random);   

            // send the message
            Transport.send(message);            
            
        } catch (AddressException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            random = 0;
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            random = 0;
        }      
        return random;
    }
}
