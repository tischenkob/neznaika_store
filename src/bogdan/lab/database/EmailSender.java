package bogdan.lab.database;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

public class EmailSender {
	static String host = "smtp.gmail.com";
	static String user = "ya.laba.bogdana@gmail.com"; // логин
	static String pass = "pochtabogdana2019"; // пароль

	static Message msg;
	static Session mailSession;


	public static void connect() {
		try {
			String to = "receiver email ";
			String from = "ya.laba.bogdana@gmail.com"; // от кого

			boolean sessionDebug = false;

			Properties props = System.getProperties();

			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", host);
			props.put("mail.smtp.port", "587");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.required", "true");

			java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
			mailSession = Session.getDefaultInstance(props, null);
			mailSession.setDebug(sessionDebug);
			msg = new MimeMessage(mailSession);
			msg.setFrom(new InternetAddress(from));
		} catch (Exception ex) {
			System.out.println("Couldn't connect to the mail");
		}

	}

	public static void sendPassword(String password, String client) {
		try {
			String subject = "Confirm your registration";
			String messageText = "Here is your secret password: ";
			InternetAddress[] address = {new InternetAddress(client)};
			msg.setRecipients(Message.RecipientType.TO, address);
			msg.setSubject(subject);
			msg.setSentDate(new Date());
			msg.setText(messageText + password);

			Transport transport = mailSession.getTransport("smtp");
			transport.connect(host, user, pass);
			transport.sendMessage(msg, msg.getAllRecipients());
			transport.close();
		} catch (Exception exc) {
			exc.printStackTrace();
			System.out.println("Couldn't send password");
		}
	}

	public static void sendToken(String token, String client) {
		try {
			String subject = "Reset password";
			String messageText = "Here is your token: ";
			InternetAddress[] address = {new InternetAddress(client)};
			msg.setRecipients(Message.RecipientType.TO, address);
			msg.setSubject(subject);
			msg.setSentDate(new Date());
			msg.setText(messageText + token);

			Transport transport = mailSession.getTransport("smtp");
			transport.connect(host, user, pass);
			transport.sendMessage(msg, msg.getAllRecipients());
			transport.close();
		} catch (Exception exc) {
			System.out.println("Couldn't send password");
		}
	}
}
