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
	private Component componentContainer = null;

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
		api.drawRectangle(posX, posY, posX + radius * 2, posY + radius * 2);
		api.setBrush(displayState ? insideBrushHigh : insideBrushLow);
		api.drawRectangle(posX + 1, posY + 1, posX + radius * 2 - 1, posY + radius * 2 - 1);
	}

	@Override public LogicPin[] onSimulationBegin() {
		return pinArray;
	}

	@Override public void onSimulationInterrupt(LogicComponent.Interrupter interrupter) {
		// Nothing: Outputs do not recieve interrupts
	}

	@Override public void onSimulationEnd() {
		// Nothing: No resources need allocation
	}

	@Override public Component.Termination[] getTerminations() {
		return termArray;
	}

	@Override public void onAttach(Component componentContainer) {
		this.componentContainer = componentContainer;
		pinArray[0] = new LogicPin();
		pinArray[0].setListening(true);
		pinArray[0].setDefaultValue(new Logic(Logic.LOW, false));
		termArray[0] = new Component.Termination(componentContainer, new LogicPin[] { pinArray[0] });
	}

	@Override public void onChangePosition(int x, int y) {
		posX = x;
		posY = y;
		termArray[0].setConnectPosiiton(x + radius, y + radius * 2);
	}

	@Override public void onInteractiveClick(int x, int y, boolean begin, boolean end) {
		// Nothing: Outputs do not interact
	}

	@Override public void onDetach() {
		// Nothing: Outputs do not release any resources
	}

	@Override public void onSimulationSignalEvent() {
		Logic state = pinArray[0].read();
		System.out.println("sth");
		if (displayState != (state.state && state.defined)) {
			displayState = (state.state && state.defined);
			componentContainer.invalidateGraphics();
		}
	}
}

class UserIOInput implements LibComponent {
	private final int radius = 10;
	private int posX = 0;
	private int posY = 0;
	private final Component.Termination[] termArray = new Component.Termination[1];
	private final LogicPin[] pinArray = new LogicPin[1];
	private LogicComponent.Interrupter interactChange = null;
	private Component componentContainer = null;

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
		api.drawRectangle(posX, posY, posX + radius * 2, posY + radius * 2);
		api.setBrush(displayState ? insideBrushHigh : insideBrushLow);
		api.drawRectangle(posX + 5, posY + 5, posX + radius * 2 - 5, posY + radius * 2 - 5);
	}

	@Override public LogicPin[] onSimulationBegin() {
		return pinArray;
	}

	@Override public void onSimulationInterrupt(LogicComponent.Interrupter interrupter) {
		if (interrupter == interactChange) {
			displayState = !displayState;
			pinArray[0].drive(Logic.FULL_DRIVER, 1, displayState);
			componentContainer.invalidateGraphics();
		}
	}

	@Override public void onSimulationEnd() {
		// Nothing: No resources need allocation
	}

	@Override public Component.Termination[] getTerminations() {
		return termArray;
	}

	@Override public void onAttach(Component componentContainer) {
		this.componentContainer = componentContainer.setWidth(radius * 2).setHeight(radius * 2);
		pinArray[0] = new LogicPin();
		pinArray[0].setListening(false);
		termArray[0] = new Component.Termination(componentContainer, new LogicPin[] { pinArray[0] });
		interactChange = componentContainer.createNewInterrupter();
	}

	@Override public void onChangePosition(int x, int y) {
		posX = x;
		posY = y;
		termArray[0].setConnectPosiiton(x + radius, y);
	}

	@Override public void onInteractiveClick(int x, int y, boolean begin, boolean end) {
		if (end) interactChange.prepareInterrupt();
	}

	@Override public void onDetach() {
		// Nothing: Inputs do not release any resources
	}

	@Override public void onSimulationSignalEvent() {
		// Nothing: No inputs to process
	}
}