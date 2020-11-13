package com.Util;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Cryptor {
	public static String getSHA1Hash(String plaintext){
		String hashText = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			byte[] messageDigest = md.digest(plaintext.getBytes());
			hashText = messageDigest.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return hashText;
	}
	
	public static String getAESEncrypt(String data){
		String encrypted = null;
		String SECRET_KEY = "_secretbaitapltm";
		SecretKeySpec skeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			byte[] byteEncrypted = cipher.doFinal(data.getBytes());
			encrypted = Base64.getEncoder().encodeToString(byteEncrypted);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return encrypted;
	}
	
	public static String getAESDecrypt(String data){
		String decrypted = null;
		String SECRET_KEY = "_secretbaitapltm";
		SecretKeySpec skeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);

			byte[] byteDecrypted = cipher.doFinal(Base64.getDecoder().decode(data));
			decrypted = new String(byteDecrypted);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return decrypted;
	}
	
}
