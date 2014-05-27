package com.example.itpappchallenge;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

public class SignatureHelper {

	public final static String SHARED_SECRET = "65FA673A-823E-49B7-B9DD-486F15F158FD";

	public static String getSignature(TreeMap<String, String> params){
		StringBuilder builder = new StringBuilder();
		
		for (Map.Entry<String, String> entry : params.entrySet())
		{
			builder.append(entry.getKey());
			builder.append('=');
			builder.append(entry.getValue());
			builder.append('&');
		}
		
		// Remove the last '&' between parameters and Shared secret
		if(builder.length() > 0){
			builder.setLength(builder.length() - 1);
		}
		
		builder.append(SHARED_SECRET);
		
		return md5(builder.toString());
	}

	public static final String md5(final String s) {
		final String MD5 = "MD5";
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuilder hexString = new StringBuilder();
			for (byte aMessageDigest : messageDigest) {
				String h = Integer.toHexString(0xFF & aMessageDigest);
				while (h.length() < 2)
					h = "0" + h;
				hexString.append(h);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}
}
