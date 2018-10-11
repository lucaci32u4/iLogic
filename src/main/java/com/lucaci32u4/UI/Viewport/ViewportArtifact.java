package com.lucaci32u4.UI.Viewport;

import org.jetbrains.annotations.NotNull;

public interface ViewportArtifact {
	boolean checkIfOnScreen(int screenLeft, int screenTop, int screenWidth, int screenHeight);
	boolean clickedOnScreen(int xUnits, int yUnits);
	void onAttach(@NotNull LogicViewport.ResourceAPI resourceAPI);
	void onDraw(@NotNull LogicViewport.DrawAPI pen, @NotNull LogicViewport.ResourceAPI resourceAPI);
	void onDetach(@NotNull LogicViewport.ResourceAPI resourceAPI);
}
