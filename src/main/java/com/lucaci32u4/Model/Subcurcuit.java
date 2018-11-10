package com.lucaci32u4.Model;

import com.lucaci32u4.Model.Parts.Component;
import com.lucaci32u4.Model.Parts.Pin;
import com.lucaci32u4.Model.Parts.Wire;
import com.lucaci32u4.Model.Parts.WireAdapter;
import com.lucaci32u4.main.Const;

import java.util.Collection;

public class Subcurcuit {
	private static final double wiresReallocMargin = Double.parseDouble(Const.query("subcircuit.partList.bufferOvershoot.wire"));
	private static final double adaptersReallocMargin = Double.parseDouble(Const.query("subcircuit.partList.bufferOvershoot.adapter"));
	private static final double pinsReallocMargin = Double.parseDouble(Const.query("subcircuit.partList.bufferOvershoot.pin"));
	private static final double componentsReallocMargin = Double.parseDouble(Const.query("subcircuit.partList.bufferOvershoot.component"));
	
	private Wire[] wires;
	private WireAdapter[] adapters;
	private Pin[] pins;
	private Component[] components;
	
	
}
