package com.lucaci32u4.model;

import com.lucaci32u4.model.parts.Component;
import com.lucaci32u4.model.parts.Pin;
import com.lucaci32u4.model.parts.wiring.WireAdapter;
import com.lucaci32u4.main.Const;

public class Subcurcuit {
	private static final double wiresReallocMargin = Double.parseDouble(Const.query("subcircuit.partList.bufferOvershoot.wire"));
	private static final double adaptersReallocMargin = Double.parseDouble(Const.query("subcircuit.partList.bufferOvershoot.adapter"));
	private static final double pinsReallocMargin = Double.parseDouble(Const.query("subcircuit.partList.bufferOvershoot.pin"));
	private static final double componentsReallocMargin = Double.parseDouble(Const.query("subcircuit.partList.bufferOvershoot.component"));
	
	private Wire[] wires;
	private WireAdapter[] adapters;
	private Pin[] pins;
	private Component[] components;
	
	
	
	public void invalidateGraphics() {
	
	}
	
	public void removeWire(Wire wire) {
	
	}
}
