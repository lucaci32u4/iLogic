package com.lucaci32u4.UI.Viewport.Renderer;


import com.lucaci32u4.UI.Viewport.LogicViewport;

import javax.swing.JPanel;
import java.awt.Canvas;

public interface RenderAPI extends DrawAPI, ResourceAPI, RenderManager, RenderCoordinateManager {
	void init(JPanel panel, LogicViewport viewport);
	void destroy();
	boolean requestRenderFrame();
	Canvas getCanvas();
}

