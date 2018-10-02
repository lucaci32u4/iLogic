package com.lucaci32u4.main;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LanguagePack {
	private static LanguagePack ourInstance = new LanguagePack();
	
	private boolean started = false;
	private Properties prop;
	
	public static LanguagePack getInstance() {
		return ourInstance;
	}
	public IOException begin(@NotNull InputStream stream) {
		IOException IOE = null;
		prop = new Properties();
		try {
			prop.load(stream);
		} catch (IOException io) {
			IOE = io;
		}
		if (IOE == null)started = true;
		return IOE;
	}
	public String get(String key) {
		return (started ? prop.getProperty(key) : null);
	}
}
