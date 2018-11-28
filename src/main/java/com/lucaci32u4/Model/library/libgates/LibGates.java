package com.lucaci32u4.model.library.libgates;

import com.lucaci32u4.model.library.LibComponent;
import com.lucaci32u4.model.library.LibFactory;

import javax.swing.*;
import java.util.UUID;

public class LibGates implements LibFactory {
	private static final UUID LIB_UUID = UUID.randomUUID();
	private static final String LIB_NAME = "Gates";
	private static final String[] LIB_COMPONENTS = new String[] { "AND" , "OR", "NOT" };
	
	@Override
	public UUID getFamilyUUID() {
		return LIB_UUID;
	}
	
	@Override
	public String getFamilyName() {
		return LIB_NAME;
	}
	
	@Override
	public String[] getComponentsName() {
		return LIB_COMPONENTS.clone();
	}
	
	@Override
	public Icon getComponentIcon(String name) {
		return null;
	}
	
	@Override
	public LibComponent createComponent() {
		return null;
	}
}
