package bogdan.lab.database;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.Random;

public class SecurityTools {

	public static String generatePassword(String salt) {
		return encryptPassword(generateString(10), salt);
	}

	public static String generateString(int length) {
		int leftLimit = 97; // letter 'a'
		int rightLimit = 122; // letter 'z'
		Random random = new Random();
		StringBuilder buffer = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			int randomLimitedInt = leftLimit + (int)
				(random.nextFloat() * (rightLimit - leftLimit + 1));
			buffer.append((char) randomLimitedInt);
		}
		String generatedString = buffer.toString();

		return generatedString;
	}

	public static String encryptPassword(String password, String salt) {
		String sha1 = "";
		try {
			MessageDigest crypt = MessageDigest.getInstance("SHA-1");
			crypt.reset();
			crypt.update((password + salt).getBytes("UTF-8"));
			sha1 = byteToHex(crypt.digest());
		} catch (NoSuchAlgorithmException e) {
			System.out.println("There's no algorithm to encrypt");
		} catch (UnsupportedEncodingException e) {
			System.out.println("Unsupported encoding");
		}
		return sha1;
	}

	private static String byteToHex(final byte[] hash) {
		Formatter formatter = new Formatter();
		for (byte b : hash) {
			formatter.format("%02x", b);
		}
		String result = formatter.toString();
		formatter.close();
		return result;
	}
	public static byte[] objectToBytes(Object obj) throws IOException {

		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		ObjectOutputStream so = new ObjectOutputStream(bo);

		so.writeObject(obj);
		so.flush();

		return bo.toByteArray();
	}
	public static Object deserializeObject(byte[] object) throws IOException, ClassNotFoundException {

		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(object);
		ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

		return objectInputStream.readObject();
	}
}
