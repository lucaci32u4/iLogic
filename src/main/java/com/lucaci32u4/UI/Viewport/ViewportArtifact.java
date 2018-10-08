package com.lucaci32u4.UI.Viewport;

import com.lucaci32u4.UI.Viewport.LogicViewport;
import org.jetbrains.annotations.NotNull;

public interface ViewportArtifact {
	boolean isVisible();
	boolean checkIfOnScreen(int screenLeft, int screenTop, int screenWidth, int screenHeight);
	void onDraw(@NotNull LogicViewport.DrawAPI pen);
	void setPickID(int ID);
	int getPickID();
}