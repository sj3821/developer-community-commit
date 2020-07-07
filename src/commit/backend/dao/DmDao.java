package commit.backend.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import commit.backend.statics.SMTPAuthenticator;

public class DmDao {

	private Connection getConnection() throws Exception {
		Context ctx = new InitialContext();
		DataSource ds = (DataSource)ctx.lookup("java:comp/env/dbcp");
		return ds.getConnection();
	}

	public String findSenderEmailAddr(String sender_id) throws Exception {
		String sql="select email from member where id = ?";

		try(
				Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);
				){
			pstat.setString(1, sender_id);
			ResultSet rs = pstat.executeQuery();
			rs.next();
			String email = rs.getString("email");

			return email;

		}
//		String email="commit.sysop@gmail.com";
//		return email;
	}



	public String findReceiverEmailAddr(String receiver_id) throws Exception {
		String sql="select email from member where id = ?";

		try(
				Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);
				){
			pstat.setString(1, receiver_id);
			ResultSet rs = pstat.executeQuery();
			rs.next();
			String email = rs.getString("email");

			return email;

		}
	}

	public void sendDm(String sender_id, String senderAddr, String receiverAddr, String title, String contents) {
		String send_to="";
		String mail_title="";
		String mail_content="";

		System.out.println("발신용 Gmail계정에 접속합니다. ");
		String username = SMTPAuthenticator.id;
		System.out.print("ID : " + username); 
		String password = SMTPAuthenticator.pw;
		System.out.print("PW : "+password);


		System.out.print("받을 사람 메일 :" + receiverAddr);
		send_to=receiverAddr;
		System.out.print("제목 : " +title);
		mail_title=title;
		System.out.println("내용 : "+contents);
		mail_content=contents;

		System.out.println();

		String to = send_to;
		String from = username+"@gmail.com";
		String host = "smtp.gmail.com";

		//mail서버(gmail) 설정
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true"); //TLS

		//mail서버(gmail) 접속
		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username+"@gmail.com", password);
			}
		});

		//mail 발송 준비
		try {
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(from));
			InternetAddress[] address = {new InternetAddress(to)};
			msg.setRecipients(Message.RecipientType.TO, address);
			msg.setSubject(mail_title);
			msg.setSentDate(new Date());
			msg.setContent("<h2><font color=blue> 개발자 커뮤니티 Commit 에서 발송된 메일입니다. </font></h2> " + "<br<br> 발송자 : "+ sender_id + "( " + senderAddr + ")" + mail_content, "text/html;charset=utf-8");

			Transport.send(msg); //mail발송
			System.out.println("전송완료");


		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
