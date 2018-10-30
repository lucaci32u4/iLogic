package com.lucaci32u4.main;

import javax.swing.filechooser.FileSystemView;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.util.Properties;

public class ApplicationConstants {
	private static ApplicationConstants ourInstance = new ApplicationConstants();
	public static ApplicationConstants getInstance() {
		return ourInstance;
	}
	
	private Properties prop = new Properties();
	
	private ApplicationConstants() {
		try {
			InputStream inputStream = getClass().getResourceAsStream("/Constants.properties");
			if (inputStream != null) {
				prop.load(inputStream);
				initProgrammaticConstants();
			} else throw new IOException("Resource Constants.properties was not found!");
		} catch (IOException e) {
			System.err.println("[ERROR at com.lucaci32u4.main.ApplicationConstants constructor]: Cannot open Constants.properties resource! Exception stack trace:");
			System.out.println("[ERROR at com.lucaci32u4.main.ApplicationConstants constructor]: Cannot open Constants.properties resource! Exception stack trace:");
			e.printStackTrace(System.err);
			e.printStackTrace(System.out);
		}
	}
	
	private void initProgrammaticConstants() {
		String separator = FileSystems.getDefault().getSeparator();
		StringBuilder workspace = new StringBuilder();
		if (System.getProperty("os.name").startsWith("Windows")) {
			//workspace = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
			workspace.append(FileSystemView.getFileSystemView().getDefaultDirectory().getPath());
		} else {
			workspace.append(System.getProperty("user.home"));
		}
		if (!workspace.toString().endsWith(separator)) workspace.append(separator);
		workspace.append(get("workspace.name"));
		prop.put("file.separator", separator);
		prop.put("workspace.path", workspace);
	}
	
	public String get(String key) {
		String result = prop.getProperty(key);
		return (result != null ? result : "");
	}
}
