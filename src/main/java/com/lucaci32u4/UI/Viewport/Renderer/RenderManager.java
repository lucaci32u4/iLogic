package com.lucaci32u4.UI.Viewport.Renderer;

import com.lucaci32u4.UI.Viewport.Renderer.VisualArtifact;

public interface RenderManager {
	void attach(VisualArtifact sprite);
	void detach(VisualArtifact sprite);
}
