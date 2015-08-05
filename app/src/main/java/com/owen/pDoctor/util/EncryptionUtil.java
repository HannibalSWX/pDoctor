package com.owen.pDoctor.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

public final class EncryptionUtil {
	private static final String SUFFIX = "PIEncrypt";
	private static final byte[] KEY = { 84, 94, -60, 118, 67, -20, -55, -70 };
	private static final byte[] IV = { 79, 76, -120, -36, -61, -72, 91, 50 };

	public static String piEncrypt(String p_string) {
		if (p_string == null) {
			throw new NullPointerException("要加密的字符串不能为 null。");
		}
		try {
			SecretKey secretKey = SecretKeyFactory.getInstance("DES").generateSecret(new DESKeySpec(KEY));

			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			cipher.init(1, secretKey, new IvParameterSpec(IV));

			return Base64.encodeToString(cipher.doFinal(p_string.getBytes("UTF8")), 0).trim() + "PIEncrypt";
		} catch (Exception e) {
			throw new MobileRuntimeException("使用“PIEncrypt”算法加密字符串时发生错误，请参考: ", e);
		}
	}

	public static String piDecrypt(String p_string) {
		if (p_string == null) {
			throw new NullPointerException("要解密的字符串不能为 null。");
		}
		try {
			SecretKey secretKey = SecretKeyFactory.getInstance("DES").generateSecret(new DESKeySpec(KEY));

			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			cipher.init(2, secretKey, new IvParameterSpec(IV));

			p_string = p_string.substring(0, p_string.length() - "PIEncrypt".length());
			return new String(cipher.doFinal(Base64.decode(p_string, 0)), "UTF8");
		} catch (Exception e) {
			throw new MobileRuntimeException("使用“PIEncrypt”算法解密字符串时发生错误，请参考: ", e);
		}
	}

	public static String aesEncrypt(String p_string, String p_key) {
		if (p_string == null) {
			throw new NullPointerException("要加密的字符串不能为 null。");
		}
		try {
			SecretKeySpec key = new SecretKeySpec(md5EncryptToBytes(p_key), "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(1, key);
			return ByteUtil.bytes2hex(cipher.doFinal(p_string.getBytes("UTF-8")), false);
		} catch (Exception e) {
			throw new MobileRuntimeException("使用“AES”算法加密字符串时发生错误，请参考: ", e);
		}
	}

	public static String aesDecrypt(String p_string, String p_key) {
		if (p_string == null) {
			throw new NullPointerException("要加密的字符串不能为 null。");
		}
		try {
			SecretKeySpec key = new SecretKeySpec(md5EncryptToBytes(p_key), "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(2, key);
			return new String(cipher.doFinal(ByteUtil.hex2Bytes(p_string)));
		} catch (Exception e) {
			throw new MobileRuntimeException("使用“AES”算法加密字符串时发生错误，请参考: ", e);
		}
	}

	public static String md5EncryptToString(String p_string) {
		return ByteUtil.bytes2hex(md5EncryptToBytes(p_string), false);
	}

	public static byte[] md5EncryptToBytes(String p_string) {
		if (p_string == null) {
			throw new NullPointerException("要加密的字符串不能为 null。");
		}
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.reset();
			digest.update(p_string.getBytes("UTF-8"));
			return digest.digest();
		} catch (NoSuchAlgorithmException e) {
			throw new MobileRuntimeException("当前设备不支持 MD5 哈希算法。");
		} catch (Exception e) {
			throw new MobileRuntimeException("使用“MD5”算法哈希字符串时发生错误，请参考: ", e);
		}
	}
}
