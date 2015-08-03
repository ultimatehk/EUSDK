/**
 * MD5操作类
 * @author huangke
 */
package cn.eugames.extension.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {

	/**
	 * 签名字符串
	 * 
	 * @param text
	 *            需要签名的字符串
	 * @param KEY
	 *            密钥
	 * @param input_charset
	 *            编码格式
	 * @return 签名结果
	 * @throws NoSuchAlgorithmException
	 */
	public static String sign(String text, String key, String input_charset) {
		text = text + key;

		byte[] hash;
		try {

			hash = MessageDigest.getInstance("MD5").digest(
					text.getBytes(input_charset));
			StringBuilder hex = new StringBuilder(hash.length * 2);
			for (byte b : hash) {
				if ((b & 0xFF) < 0x10)
					hex.append("0");
				hex.append(Integer.toHexString(b & 0xFF));
			}

			// AppHelper.logD("Sign " + text + " to: " + hex.toString());

			return hex.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static byte[] encode(String origin) {

		try {
			MessageDigest md5;
			md5 = MessageDigest.getInstance("MD5");
			byte[] raw = origin.getBytes("UTF-8");
			return md5.digest(raw);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return null;

	}

	public static StringBuffer getDigest(String origin) {

		try {
			MessageDigest md5;
			md5 = MessageDigest.getInstance("MD5");
			byte[] raw = origin.getBytes("UTF-8");
			byte[] array = md5.digest(raw);
			StringBuffer md5StrBuff = new StringBuffer();
			for (int i = 0; i < array.length; i++) {
				if (Integer.toHexString(0xFF & array[i]).length() == 1)
					md5StrBuff.append("0").append(
							Integer.toHexString(0xFF & array[i]));
				else
					md5StrBuff.append(Integer.toHexString(0xFF & array[i]));
			}
			return md5StrBuff;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return null;

	}
}
