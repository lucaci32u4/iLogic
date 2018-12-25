package com.lucaci32u4.model.library.libuserio;

import com.lucaci32u4.core.Logic;
import com.lucaci32u4.core.LogicComponent;
import com.lucaci32u4.core.LogicPin;
import com.lucaci32u4.model.library.LibComponent;
import com.lucaci32u4.model.library.LibFactory;
import com.lucaci32u4.model.parts.Component;
import com.lucaci32u4.ui.viewport.renderer.RenderAPI;
import com.lucaci32u4.ui.viewport.renderer.brush.Brush;

import javax.swing.*;
import java.util.UUID;

public class LibUserIO implements LibFactory {
	private static final UUID LIB_UUID = UUID.randomUUID();
	private static final String LIB_NAME = "Input/Output";
	private static final String[] LIB_COMPONENTS = new String[] { "Output", "Input" };
	
	@Override public UUID getFamilyUUID() {
		return LIB_UUID;
	}

	@Override public String getFamilyName() {
		return LIB_NAME;
	}

	@Override public String[] getComponentsName() {
		return LIB_COMPONENTS;
	}

	@Override public Icon getComponentIcon(String name) {
		return null;
	}

	@Override public LibComponent createComponent(String name) {
		switch (name) {
			case "Output":
				return new UserIOOutput();
			case "Input":
				return new UserIOInput();
			default:
				throw new IllegalArgumentException();
		}
	}
}

class UserIOOutput implements LibComponent {
	private final int radius = 10;
	private int posX = 0;
	private int posY = 0;
	private final Component.Termination[] termArray = new Component.Termination[1];
	private final LogicPin[] pinArray = new LogicPin[1];

	private volatile boolean displayState = false;
	private static Brush outlineBrush = null;
	private static Brush insideBrushHigh = null;
	private static Brush insideBrushLow = null;
	private static boolean initBrushes = false;
	@Override public void onDraw(RenderAPI api) {
		if (!initBrushes) {
			outlineBrush = api.createOutlineBrush(0, 0, 0);
			insideBrushHigh = api.createSolidBrush(0, 255, 0);
			insideBrushLow = api.createSolidBrush(255, 0, 0);
			initBrushes = true;
		}
		api.setBrush(outlineBrush);
		api.drawRectangle(posX - radius, posY - radius, posX + radius, posY + radius);
		api.setBrush(displayState ? insideBrushHigh : insideBrushLow);
		api.drawRectangle(posX - radius + 1, posY - radius + 1, posX + radius - 1, posY + radius - 1);
	}

	@Override public LogicPin[] onBegin() {
		return pinArray;
	}

	@Override public void onSimulationInterrupt(LogicComponent.Interrupter interrupter) {
		// Nothing: Outputs do not recieve interrupts
	}

	@Override public void onEnd() {
		// Nothing: No resources need allocation
	}

	@Override public Component.Termination[] getTerminations() {
		return termArray;
	}

	@Override public void onAttach(Component componentContainer) {
		pinArray[0] = new LogicPin();
		termArray[0] = new Component.Termination(componentContainer, new LogicPin[] { pinArray[0] });
	}

	@Override public void onChangePosition(int x, int y) {
		posX = x;
		posY = y;
		termArray[0].setConnectPosiiton(x, y + radius);
	}

	@Override public void onChangeDimension(int width, int height) {
		// Nothing: Outputs do not change sizes
	}

	@Override public void onInteractiveClick(int x, int y) {
		// Nothing: Outputs do not interact
	}

	@Override public void onDetach() {
		// Nothing: Outputs do not release any resources
	}

	@Override public void onSimulationSignalEvent() {
		Logic state = pinArray[0].read();
		displayState = state.state && state.defined;
	}
}

class UserIOInput implements LibComponent {

	@Override public void onDraw(RenderAPI api) {
		
	}

	@Override public LogicPin[] onBegin() {
		return new LogicPin[0];
	}

	@Override public void onSimulationSignalEvent() {

	}

	@Override public void onSimulationInterrupt(LogicComponent.Interrupter interrupter) {

	}

	@Override public void onEnd() {

	}

	@Override public Component.Termination[] getTerminations() {
		return new Component.Termination[0];
	}

	@Override public void onAttach(Component componentContainer) {

	}

	@Override public void onChangePosition(int x, int y) {

	}

	@Override public void onChangeDimension(int width, int height) {

	}

	@Override public void onInteractiveClick(int x, int y) {

	}

	@Override public void onDetach() {

	}
}