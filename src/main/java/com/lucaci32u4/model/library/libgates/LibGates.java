package com.lucaci32u4.model.library.libgates;

import com.lucaci32u4.core.Logic;
import com.lucaci32u4.core.LogicComponent;
import com.lucaci32u4.core.LogicPin;
import com.lucaci32u4.model.library.LibComponent;
import com.lucaci32u4.model.library.LibFactory;
import com.lucaci32u4.model.parts.Component;
import com.lucaci32u4.ui.viewport.renderer.DrawAPI;
import com.lucaci32u4.ui.viewport.renderer.brush.Brush;
import com.lucaci32u4.ui.viewport.renderer.brush.OutlineBrush;

import javax.swing.*;
import java.util.UUID;

public class LibGates implements LibFactory {
	private static final UUID LIB_UUID = UUID.randomUUID();
	private static final String LIB_NAME = "Gates";
	private static final String[] LIB_COMPONENTS = new String[] { "AND" , "OR", "NOT" };
	
	@Override
	public UUID getFamilyUUID() {
		return LIB_UUID;
	}
	
	@Override
	public String getFamilyName() {
		return LIB_NAME;
	}
	
	@Override
	public String[] getComponentsName() {
		return LIB_COMPONENTS.clone();
	}
	
	@Override
	public Icon getComponentIcon(String name) {
		return null;
	}
	
	@Override
	public LibComponent createComponent(String name) {
		switch (name) {
			case "AND":
				return new GateAnd();
			case "OR":
				return new GateOr();
			case "NOT":
				return new GateNot();
			default:
				return null;
		}
	}
	
	private static Brush gateOutlineBrush = null;
}




class GateAnd implements LibComponent {
	private final Component.Termination[] arrayTerminations = new Component.Termination[3];
	private final LogicPin[] arrayPins = new LogicPin[3];
	private int posX, posY, width, height;
	private Component component = null;

	@Override
	public void onAttach(Component componentContainer) {
		component = componentContainer;
		posX = 0;
		posY = 0;
		width = 80;
		height = 60;
		for (int i = 0; i < 3; i++) {
			arrayPins[i] = new LogicPin();
			arrayTerminations[i] = new Component.Termination(componentContainer, new LogicPin[]{ arrayPins[i]});
		}
	}
	
	@Override
	public Component.Termination[] getTerminations() {
		return arrayTerminations;
	}
	
	@Override
	public LogicPin[] onBegin() {
		return arrayPins;
	}
	
	@Override
	public void onSimulationSignalEvent() {
		Logic p1 = arrayPins[0].read();
		Logic p2 = arrayPins[1].read();
		arrayPins[2].drive(Logic.FULL_DRIVER, 1, p1.defined && p1.state && p2.defined && p2.state);
	}
	
	@Override
	public void onSimulationInterrupt(LogicComponent.Interrupter interrupter) {
		// Nothing: Gates do not take interrupts
	}
	
	@Override
	public void onEnd() {
		// Nothing: There are no resources allocated
	}
	
	@Override
	public void onChangePosition(int x, int y) {
		posX = x;
		posY = y;
		arrayTerminations[0].setConnectPosiiton(x, y + (height) / 3);
		arrayTerminations[1].setConnectPosiiton(x, y + (height * 2) / 3);
		arrayTerminations[2].setConnectPosiiton(x + width, y + height / 2);
	}
	
	@Override
	public void onChangeDimension(int width, int height) {
		// Nothing: Gates do not change dimensions.
	}
	
	@Override
	public void onInteractiveClick(int x, int y) {
		// Nothing: Gates do not interact.
	}
	
	@Override
	public void onDraw(DrawAPI api) {
	
	}
	
	@Override
	public void onDetach() {
	
	}
}

class GateOr implements LibComponent {
	@Override
	public void onDraw(DrawAPI api) {
	
	}
	
	@Override
	public LogicPin[] onBegin() {
		return new LogicPin[0];
	}
	
	@Override
	public void onSimulationSignalEvent() {
	
	}
	
	@Override
	public void onSimulationInterrupt(LogicComponent.Interrupter interrupter) {
	
	}
	
	@Override
	public void onEnd() {
	
	}
	
	@Override
	public void onAttach(Component componentContainer) {
	
	}
	
	@Override
	public void onChangePosition(int x, int y) {
	
	}
	
	@Override
	public void onChangeDimension(int width, int height) {
	
	}
	
	@Override
	public void onInteractiveClick(int x, int y) {
	
	}
	
	@Override
	public Component.Termination[] getTerminations() {
		return new Component.Termination[0];
	}
	
	@Override
	public void onDetach() {
	
	}
}

class GateNot implements LibComponent {
	@Override
	public void onDraw(DrawAPI api) {
	
	}
	
	@Override
	public LogicPin[] onBegin() {
		return new LogicPin[0];
	}
	
	@Override
	public void onSimulationSignalEvent() {
	
	}
	
	@Override
	public void onSimulationInterrupt(LogicComponent.Interrupter interrupter) {
	
	}
	
	@Override
	public void onEnd() {
	
	}
	
	@Override
	public void onAttach(Component componentContainer) {
	
	}
	
	@Override
	public void onChangePosition(int x, int y) {
	
	}
	
	@Override
	public void onChangeDimension(int width, int height) {
	
	}
	
	@Override
	public void onInteractiveClick(int x, int y) {
	
	}
	
	@Override
	public Component.Termination[] getTerminations() {
		return new Component.Termination[0];
	}
	
	@Override
	public void onDetach() {
	
	}
}

