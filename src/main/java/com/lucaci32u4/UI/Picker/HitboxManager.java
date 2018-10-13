package com.lucaci32u4.UI.Picker;

public interface HitboxManager {
	int CHANNEL_EDITOR          = 0;
	int CHANNEL_SIMULATION      = 1;
	void attach(Hitbox hitbox, int pickerChannel);
	void detach(Hitbox hitbox, int pickerChannel);
	void pick(int pointerX, int pointerY, int pickerChannel);
	void destroy();
}

