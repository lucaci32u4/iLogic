package com.lucaci32u4.UI.Viewport.Renderer;


import com.lucaci32u4.UI.Viewport.LogicViewport;

import javax.swing.JPanel;
import java.awt.Canvas;

public interface RenderAPI extends DrawAPI, ResourceAPI, RenderManager {
	void initRenderer(JPanel panel, LogicViewport viewport);
	void destroyRenderer();
	boolean requestRenderFrame(LogicViewport.ViewportData drawData);
	Canvas getCanvas();
}
