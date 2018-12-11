package com.lucaci32u4.model;

import com.lucaci32u4.model.parts.Component;
import com.lucaci32u4.main.Const;
import com.lucaci32u4.model.parts.wiring.WireModel;

import java.util.ArrayList;
import java.util.Collection;


public class Subcurcuit {
	
	private Collection<Component> components;
	private Collection<WireModel> wires;
	private final ModelContainer mdl;
	
	// Pointer data
	private long pos = 0;
	private boolean editMode = false;
	
	// Selection data
	private boolean hasSelection = false;
	private boolean selecting = false;
	private boolean area = false;
	private long selEnd1 = 0, selEnd2 = 0;
	private Collection<Object> selObjects = new ArrayList<>();
	
	// Interact data
	private boolean interacting = false;
	private Object interactObj = null;
	
	
	public Subcurcuit(ModelContainer mdl) {
		this.mdl = mdl;
	}
	
	public void setPointerMode(boolean edit) {
		editMode = edit;
		if (interacting) {
			interact(pos, false, true);
		}
	}
	
	public void onPointer(long position) {
		pos = position;
		if (interacting) {
			interact(position, false, false);
		}
		if (selecting) {
			continueSelection(position);
		}
	}
	
	public void onMainButton(boolean isPressed) {
		if (editMode) {
			if (isPressed) {
				beginSelection(pos);
			} else {
				endSelection(pos);
			}
			selecting = isPressed;
		} else {
			interact(pos, isPressed, !isPressed);
		}
	}
	
	private void beginSelection(long begin) {
		selEnd1 = begin;
		area = false;
		hasSelection = false;
		continueSelection(begin);
	}
	
	private void continueSelection(long where) {
		selEnd2 = where;
		if (selEnd1 != selEnd2 && !area) area = true;
		if (area) {
			deselectAll();
			selObjects.clear();
			for (Component component : components) {
				if (CoordinateHelper.intersectDimension(selEnd1, selEnd2, component.getPosition(), component.getDimension())) {
					selObjects.add(component);
					component.select(true);
				}
			}
			for (WireModel wire : wires) {
				if (wire.selectArea(selEnd1, selEnd2)) {
					selObjects.add(wire);
				}
			}
		}
	}
	
	private void endSelection(long end) {
		continueSelection(end);
		hasSelection = !selObjects.isEmpty();
		selecting = false;
	}
	
	private void deselectAll() {
		if (!selObjects.isEmpty()) {
			for (Object obj : selObjects) {
				if (obj instanceof Component) {
					((Component)obj).select(false);
				}
				if (obj instanceof WireModel) {
					((WireModel)obj).deselect();
				}
			}
		}
	}
	
	private void interact(long pos, boolean begin, boolean end) {
		if (begin) {
			interactObj = null;
			for (Component component : components) {
				if (CoordinateHelper.inside(pos, component.getPosition(), component.getDimension())) {
					interactObj = component;
					break;
				}
			}
			interacting = interactObj != null;
		}
		if (interacting) {
			if (interactObj instanceof Component) {
				((Component) interactObj).interact(pos, begin, end);
			}
		}
		if (end) {
			interacting = false;
			interactObj = null;
		}
	}
	
	public void invalidateGraphics() {
		mdl.invalidateGraphics(this);
	}
	
	
}
