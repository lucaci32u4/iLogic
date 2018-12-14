package com.lucaci32u4.ui.viewport;

import com.lucaci32u4.ui.viewport.renderer.DrawAPI;
import com.lucaci32u4.ui.viewport.renderer.RenderAPI;

public interface RenderCallback {
	void onDraw(DrawAPI draw, RenderAPI ctrl);
}
