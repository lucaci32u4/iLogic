package com.lucaci32u4.UI;

import org.jetbrains.annotations.NotNull;

import javax.media.opengl.GL2;

public interface ViewportArtifact {
	boolean isVisible();
	boolean checkIfOnScreen(int screenLeft, int screenTop, int screenWidth, int screenHeight);
	boolean checkIfOnPoint(int xPoint, int yPoint);
	void onDraw(@NotNull LogicViewport.DrawAPI pen);
	void setPickID(int ID);
	int getPickID();
}
