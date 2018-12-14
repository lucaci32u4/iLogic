package com.lucaci32u4.model.library;

import com.lucaci32u4.core.LogicComponent;
import com.lucaci32u4.model.parts.Component;
import com.lucaci32u4.ui.viewport.renderer.DrawAPI;

public interface LibComponent extends LogicComponent.Handler, Component.BehaviourSpecification {
	void onDraw(DrawAPI api);
}
