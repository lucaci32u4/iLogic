package com.lucaci32u4.UI.Viewport.Picker;

import com.lucaci32u4.Circuit.CircuitComponent;

public interface HitboxManager {
	int CHANNEL_EDITOR          = 0;
	int CHANNEL_SIMULATION      = 1;
	int CHANNEL_CONNECTION		= 2;
	void attach(Hitbox hitbox, int pickerChannel);
	void detach(Hitbox hitbox, int pickerChannel);
}
