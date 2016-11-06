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

	/** 指定加密算法为RSA */
	private static final String ALGORITHM = "RSA";
	/** 密钥长度，用来初始化 */
	private static final int KEYSIZE = 512;

	/**
	 * 生成RSA密钥对
	 * 
	 * @throws Exception
	 */
	public static Map<String, Key> generateKeyPair() throws Exception {

		Map<String, Key> keyMap = new HashMap<String, Key>();

		// RSA算法要求有一个可信任的随机数源
		SecureRandom secureRandom = new SecureRandom();

		// 为RSA算法创建一个KeyPairGenerator对象
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);

		// 利用上面的随机数据源初始化这个KeyPairGenerator对象
		keyPairGenerator.initialize(KEYSIZE, secureRandom);

		// 生成密匙对
		KeyPair keyPair = keyPairGenerator.generateKeyPair();

		// 得到公钥
		Key publicKey = keyPair.getPublic();
		System.out.println(publicKey.getEncoded());
		X509EncodedKeySpec x509ek = new X509EncodedKeySpec(publicKey.getEncoded());
		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
		Key k = keyFactory.generatePublic(x509ek);
		System.out.println(k.getEncoded());

		// 得到私钥
		Key privateKey = keyPair.getPrivate();

		keyMap.put("publicKey", k);
		keyMap.put("privateKey", privateKey);

		return keyMap;
	}

	/**
	 * RSA加密算法
	 * 
	 * @param source
	 *            源数据
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] source, Key publicKey) throws Exception {
		// 得到Cipher对象来实现对源数据的RSA加密
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		
		//byte[] b = source.getBytes();
		// 执行加密操作
		byte[] b1 = cipher.doFinal(source);
		return b1;
		//BASE64Encoder encoder = new BASE64Encoder();
		//return encoder.encode(b1);
	}

	/**
	 * RSA解密算法
	 * 
	 * @param cryptograph
	 *            密文
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] cryptograph, Key privateKey) throws Exception {
		// 得到Cipher对象对已用公钥加密的数据进行RSA解密
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
//		BASE64Decoder decoder = new BASE64Decoder();
//		byte[] b1 = decoder.decodeBuffer(cryptograph);

		// 执行解密操作
//		byte[] b = cipher.doFinal(b1);
		byte[] b= cipher.doFinal(cryptograph);
		return b;
	}

	/**
	 * 根据公钥获得可以传输的byte数组
	 * 
	 * @param publicKey
	 * @return
	 */
	public static byte[] publicKeyToByte(PublicKey publicKey) {
		
		return publicKey.getEncoded();
	}

	/**
	 * 根据byte数组生成公钥
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