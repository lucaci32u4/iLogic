package com.lucaci32u4.presentation;

import com.lucaci32u4.model.ModelContainer;

import javax.swing.Icon;

public interface ViewControllerInterface {
	void init(ModelContainer model, boolean initialEditMode);
	void addSimulationComponentModel(String family, String name, Icon icon);
	void removeSimulationComponentModel(String family, String name);
	void deselectSimulationModel();
	void setUserActionListener(UserActionListener listener);
	void setUserPointerListener(UserPointerListener listener);
	void invalidateGraphics();
	ExitDialogResult showExitDialog();
	void destroy();

	class ExitDialogResult {
		public final boolean save;
		public final boolean exit;
		public ExitDialogResult(boolean save, boolean exit) {
			this.save = save;
			this.exit = exit;
		}
 	}
}
