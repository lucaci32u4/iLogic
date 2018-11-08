package com.lucaci32u4.main;

public class SaveFileHandler {
	String name;
	String sysPath;
	
	public void init(String location) {
		if (location == null) {
			sysPath = null;
			name = "Untitled";
		}
	}
	
	public void setSaveFilename(String filename) {
		sysPath = filename;
	}
	
	public void save() {
	
	}
}
