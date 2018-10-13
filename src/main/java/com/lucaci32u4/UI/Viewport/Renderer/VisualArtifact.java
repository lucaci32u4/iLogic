package com.lucaci32u4.UI.Viewport.Renderer;

import org.jetbrains.annotations.NotNull;

public interface VisualArtifact {
	boolean checkIfOnScreen(int screenLeft, int screenTop, int screenWidth, int screenHeight);
	void onAttach(@NotNull ResourceAPI resourceAPI);
	void onDraw(@NotNull DrawAPI pen, @NotNull ResourceAPI resourceAPI);
	void onDetach(@NotNull ResourceAPI resourceAPI);
}
