package com.lucaci32u4.UI.Viewport.Picker;

public interface PickerAPI extends PickerManager {
	int CHANNEL_EDITOR          = 0;
	int CHANNEL_SIMULATION      = 1;
	void init();
	void pick(int pointerX, int pointerY, int pickerChannel);
	void destroy();
}

