package com.lucaci32u4.Circuit;

import com.lucaci32u4.UI.Viewport.Picker.HitboxManager;
import com.lucaci32u4.UI.Viewport.Picker.PickerAPI;
import com.lucaci32u4.UI.Viewport.Renderer.RenderManager;

public interface CircuitComponent {
	void setHitboxMager(HitboxManager manager);
	void setRenderManager(RenderManager manager);
	
	void onForwardMouseAction(PickerAPI picker, boolean pressed, boolean released, int poxX, int posY);
}
