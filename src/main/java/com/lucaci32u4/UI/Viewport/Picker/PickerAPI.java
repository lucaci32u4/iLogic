package com.lucaci32u4.UI.Viewport.Picker;

public interface PickerAPI extends HitboxManager {
	void init();
	Hitbox pick(int pointerX, int pointerY, int pickerChannel);
	void destroy();
}

