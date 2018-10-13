package com.lucaci32u4.UI.Viewport.Renderer;


import com.lucaci32u4.UI.Viewport.LogicViewport;

import javax.swing.JPanel;
import java.awt.Canvas;

public interface RenderAPI extends DrawAPI, ResourceAPI, RenderManager {
	void init(JPanel panel, LogicViewport viewport);
	void destroy();
	boolean requestRenderFrame();
	Canvas getCanvas();

	class ArtifactLifetimeEvent {
		public enum Type {
			ATTACH, DETACH, EXIT,
		}
		public Type type;
		public VisualArtifact sprite;
		public ArtifactLifetimeEvent(Type type, VisualArtifact sprite) {
			this.type = type;
			this.sprite = sprite;
		}
	}
}

