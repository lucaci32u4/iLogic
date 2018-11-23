package com.lucaci32u4.UI.Viewport;

import com.lucaci32u4.UI.Viewport.Renderer.DrawAPI;
import com.lucaci32u4.UI.Viewport.Renderer.RenderAPI;

public interface RenderCallback {
	void onDraw(DrawAPI draw, RenderAPI ctrl);
}
