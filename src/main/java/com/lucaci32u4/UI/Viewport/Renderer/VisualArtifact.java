package com.lucaci32u4.UI.Viewport.Renderer;

import com.lucaci32u4.UI.Viewport.LogicViewport;
import org.jetbrains.annotations.NotNull;

public interface VisualArtifact {
	boolean checkIfOnScreen(int screenLeft, int screenTop, int screenWidth, int screenHeight);
	void onAttach(@NotNull LogicViewport.ResourceAPI resourceAPI);
	void onDraw(@NotNull LogicViewport.DrawAPI pen, @NotNull LogicViewport.ResourceAPI resourceAPI);
	void onDetach(@NotNull LogicViewport.ResourceAPI resourceAPI);
}
