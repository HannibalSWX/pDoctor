package com.owen.pDoctor.util;

public final class ByteUtil {
	public static String bytes2hex(byte[] p_bytes, boolean p_upperCase) {
		if (p_bytes == null) {
			throw new NullPointerException("要转换的 byte 数组不能为 null。");
		}
		StringBuilder sb = new StringBuilder(p_bytes.length);
		byte[] arrayOfByte = p_bytes;
		int j = p_bytes.length;
		for (int i = 0; i < j; i++) {
			byte b = arrayOfByte[i];

			sb.append(String.format("%02x", new Object[] { Byte.valueOf(b) }));
		}
		return p_upperCase ? sb.toString().toUpperCase() : sb.toString();
	}

	public static byte[] hex2Bytes(String p_string) {
		if (p_string == null) {
			throw new NullPointerException("要转换的十六进制字符串不能为 null。");
		}
		p_string = p_string.trim();
		int length = p_string.length();
		if (length == 0) {
			return null;
		}
		if (length % 2 != 0) {
			throw new MobileRuntimeException("要转换的十六进制字符串必须为偶数位。");
		}
		byte[] result = new byte[length / 2];
		try {
			for (int i = 0; i < length; i += 2) {
				result[(i / 2)] = ((byte) Integer.parseInt(p_string.substring(i, i + 2), 16));
			}
		} catch (Exception e) {
			throw new MobileRuntimeException("要转换的十六进制字符串格式不正确，请参考: " + e);
		}
		return result;
	}
}
