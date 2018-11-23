package com.lucaci32u4.presentation;

import javax.swing.Icon;

public interface ViewControllerInterface {
	void init();
	void addSimulationComponentModel(String family, String name, Icon icon);
	void removeSimulationComponentModel(String family, String name);
	void setUserActionListener(UserActionListener listener);
	void setUserPointerListener(UserPointerListener listener);
	void invalidateGraphics();
}
