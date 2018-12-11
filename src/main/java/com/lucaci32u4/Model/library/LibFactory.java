package com.lucaci32u4.model.library;

import javax.swing.Icon;
import java.util.UUID;

public interface LibFactory {
	UUID getFamilyUUID();
	String getFamilyName();
	String[] getComponentsName();
	Icon getComponentIcon(String name);
	LibComponent createComponent(String name);
}
