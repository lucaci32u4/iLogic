package com.lucaci32u4.Circuit;

import com.lucaci32u4.UI.Viewport.Picker.Hitbox;
import com.lucaci32u4.UI.Viewport.Picker.Implementation.SingleIterativeCollectionPicker;
import com.lucaci32u4.UI.Viewport.Picker.PickerAPI;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;

public class CircuitContainer {
	
	private ArrayDeque<CircuitComponent> discreteComponents;
	private ArrayDeque<CircuitComponent> interfacePins;
	private ArrayDeque<CircuitComponent> wires;
	
	private PickerAPI picker;
	
	public CircuitContainer() {
		discreteComponents = new ArrayDeque<>();
		interfacePins = new ArrayDeque<>();
		wires = new ArrayDeque<>();
		picker = new SingleIterativeCollectionPicker() {
			@Override public ArrayList<Collection<Hitbox>> createCollections(int collectionCount) {
				ArrayList<Collection<Hitbox>> res = new ArrayList<>();
				for (int i = 0; i < collectionCount; i++) res.add(new ArrayDeque<>());
				return res;
			}
		};
	}
	
	public void onMouseEvent(boolean onPress, boolean onRelease, boolean onMove, int posX, int posY) {
	
	}
	
	
}


interface CircuitContainerComponentInterface {
	void captureMouse(CircuitComponent captureComponent);
	void releaseMouse();
}