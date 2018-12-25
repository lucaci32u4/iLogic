package com.lucaci32u4.model.parts.wiring;

import com.lucaci32u4.ui.viewport.renderer.RenderAPI;

import java.awt.*;

public interface Connectable {
	int getConnectPositionX();
	int getConnectPositionY();
	int getConnectionRadius();
	void render(RenderAPI graphics);
	boolean isMutable();
}
