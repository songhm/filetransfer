package com.example.file_transfer.utils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.util.Log;

public class AES {

	
	private static final String ALGORITHM = "AES";

	private static final int KEYSIZE = 128;


	public static SecretKey getSecretKey() throws NoSuchAlgorithmException, IOException {
		KeyGenerator keyPairGenerator = KeyGenerator.getInstance(ALGORITHM);
		keyPairGenerator.init(KEYSIZE);
		SecretKey secretKey = keyPairGenerator.generateKey();
		return secretKey;
	}


	public static byte[] encrypt(byte[] text, SecretKey secretKey) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		byte[] ans = cipher.doFinal(text);
		return ans;
	}


	public static byte[] decrypt(byte[] text, SecretKey secretKey) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		byte[] ans = cipher.doFinal(text);
		return ans;
	}

	public static byte[] secretKeyToByte(SecretKey secretKey) {
		Log.v("AES","sk length:"+secretKey.getEncoded().length);
		return secretKey.getEncoded();
	}


	public static SecretKey byteToSecretKey(byte[] b) {
		SecretKeySpec s = new SecretKeySpec(b, ALGORITHM);
		return s;
	}

}