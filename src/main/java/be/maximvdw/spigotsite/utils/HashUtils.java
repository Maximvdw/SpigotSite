package be.maximvdw.spigotsite.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {
	public static String getHash(String message, String algorithm) {
		try {
			if (message == null)
				message = "";
			StringBuffer hexString = new StringBuffer();
			MessageDigest md = MessageDigest.getInstance(algorithm);
			byte[] hash = md.digest(message.getBytes());

			for (int i = 0; i < hash.length; i++) {
				if ((0xff & hash[i]) < 0x10) {
					hexString.append("0"
							+ Integer.toHexString((0xFF & hash[i])));
				} else {
					hexString.append(Integer.toHexString(0xFF & hash[i]));
				}
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String getMD5(String message) {
		return getHash(message, "MD5");
	}

	public static String getSHA256(String message) {
		return getHash(message, "SHA-256");
	}

	public static String getMD5Hash(File file) {
		try {
			InputStream fis = new FileInputStream(file);

			byte[] buffer = new byte[1024];
			MessageDigest complete = MessageDigest.getInstance("MD5");
			int numRead;

			do {
				numRead = fis.read(buffer);
				if (numRead > 0) {
					complete.update(buffer, 0, numRead);
				}
			} while (numRead != -1);

			fis.close();
			StringBuffer hexString = new StringBuffer();
			byte[] hash = complete.digest();
			for (int i = 0; i < hash.length; i++) {
				if ((0xff & hash[i]) < 0x10) {
					hexString.append("0"
							+ Integer.toHexString((0xFF & hash[i])));
				} else {
					hexString.append(Integer.toHexString(0xFF & hash[i]));
				}
			}
			return hexString.toString();
		} catch (Exception ex) {

		}
		return "";
	}
}
