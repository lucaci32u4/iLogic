package com.lucaci32u4.UI.Viewport.Renderer;

import com.lucaci32u4.UI.Viewport.Renderer.Brushes.Brush;

public interface DrawAPI {
	float unitsToPixels(int units);
	float pixelsToUnits(int pixels);
	void setCanvasOffsetUnits(int offsetX, int offsetY);
	void setBrush(Brush brush);
	void drawLine(int fromX, int fromY, int toX, int toY, float thicknessPixels);
	void drawTriangle(int aX, int aY, int bX, int bY, int cX, int cY);
	void drawRectangle(int left, int top, int right, int bottom);
}
