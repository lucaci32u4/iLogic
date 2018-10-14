package com.lucaci32u4.UI.Viewport.Picker;

public interface PickerManager {
	int CHANNEL_EDITOR          = 0;
	int CHANNEL_SIMULATION      = 1;
	void attach(Hitbox hitbox, int pickerChannel);
	void detach(Hitbox hitbox, int pickerChannel);
}
