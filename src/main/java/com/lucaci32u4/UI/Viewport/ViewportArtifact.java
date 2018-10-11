package com.lucaci32u4.UI.Viewport;

import org.jetbrains.annotations.NotNull;

public interface ViewportArtifact {
	void onAttach();
	boolean checkIfOnScreen(int screenLeft, int screenTop, int screenWidth, int screenHeight);
	void onDraw(@NotNull LogicViewport.DrawAPI pen);
	void onDetach();
}
