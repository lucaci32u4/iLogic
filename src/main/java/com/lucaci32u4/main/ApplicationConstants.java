package com.lucaci32u4.main;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApplicationConstants {
	private static ApplicationConstants ourInstance = new ApplicationConstants();
	public static ApplicationConstants getInstance() {
		return ourInstance;
	}
	
	private Properties prop = new Properties();
	
	private ApplicationConstants() {
		try {
			InputStream inputStream = getClass().getResourceAsStream("Constants.properties");
			if (inputStream != null) {
				prop.load(inputStream);
			} else throw new IOException("Resource Constants.properties was not found!");
		} catch (IOException e) {
			System.err.println("[ERROR at com.lucaci32u4.main.ApplicationConstants constructor]: Cannot open Constants.properties resource! Exception stack trace:");
			System.out.println("[ERROR at com.lucaci32u4.main.ApplicationConstants constructor]: Cannot open Constants.properties resource! Exception stack trace:");
			e.printStackTrace(System.err);
			e.printStackTrace(System.out);
		}
	}
	
	public String get(String key) {
		String result = prop.getProperty(key);
		return (result != null ? result : "");
	}
}
