package com.lucaci32u4.UI.Viewport.Picker;

public interface PickerAPI extends PickerManager {
	void init();
	void pick(int pointerX, int pointerY, int pickerChannel);
	void destroy();
}

