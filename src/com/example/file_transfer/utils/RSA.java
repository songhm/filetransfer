package com.example.file_transfer.utils;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

public class RSA {

	/** ָ�������㷨ΪRSA */
	private static final String ALGORITHM = "RSA";
	/** ��Կ���ȣ�������ʼ�� */
	private static final int KEYSIZE = 512;

	/**
	 * ����RSA��Կ��
	 * 
	 * @throws Exception
	 */
	public static Map<String, Key> generateKeyPair() throws Exception {

		Map<String, Key> keyMap = new HashMap<String, Key>();

		// RSA�㷨Ҫ����һ�������ε������Դ
		SecureRandom secureRandom = new SecureRandom();

		// ΪRSA�㷨����һ��KeyPairGenerator����
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);

		// ����������������Դ��ʼ�����KeyPairGenerator����
		keyPairGenerator.initialize(KEYSIZE, secureRandom);

		// �����ܳ׶�
		KeyPair keyPair = keyPairGenerator.generateKeyPair();

		// �õ���Կ
		Key publicKey = keyPair.getPublic();
		System.out.println(publicKey.getEncoded());
		X509EncodedKeySpec x509ek = new X509EncodedKeySpec(publicKey.getEncoded());
		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
		Key k = keyFactory.generatePublic(x509ek);
		System.out.println(k.getEncoded());

		// �õ�˽Կ
		Key privateKey = keyPair.getPrivate();

		keyMap.put("publicKey", k);
		keyMap.put("privateKey", privateKey);

		return keyMap;
	}

	/**
	 * RSA�����㷨
	 * 
	 * @param source
	 *            Դ����
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] source, Key publicKey) throws Exception {
		// �õ�Cipher������ʵ�ֶ�Դ���ݵ�RSA����
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		
		//byte[] b = source.getBytes();
		// ִ�м��ܲ���
		byte[] b1 = cipher.doFinal(source);
		return b1;
		//BASE64Encoder encoder = new BASE64Encoder();
		//return encoder.encode(b1);
	}

	/**
	 * RSA�����㷨
	 * 
	 * @param cryptograph
	 *            ����
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] cryptograph, Key privateKey) throws Exception {
		// �õ�Cipher��������ù�Կ���ܵ����ݽ���RSA����
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
//		BASE64Decoder decoder = new BASE64Decoder();
//		byte[] b1 = decoder.decodeBuffer(cryptograph);

		// ִ�н��ܲ���
//		byte[] b = cipher.doFinal(b1);
		byte[] b= cipher.doFinal(cryptograph);
		return b;
	}

	/**
	 * ���ݹ�Կ��ÿ��Դ����byte����
	 * 
	 * @param publicKey
	 * @return
	 */
	public static byte[] publicKeyToByte(PublicKey publicKey) {
		
		return publicKey.getEncoded();
	}

	/**
	 * ����byte�������ɹ�Կ
	 * 
	 * @param b
	 * @return
	 * @throws Exception
	 */
	public static Key byteToPublicKey(byte[] b) throws Exception {
		X509EncodedKeySpec x509ek = new X509EncodedKeySpec(b);
		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
		Key k = keyFactory.generatePublic(x509ek);
		return k;
	}


}