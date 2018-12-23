package com.lucaci32u4.model;

import com.lucaci32u4.model.library.LibComponent;
import com.lucaci32u4.model.library.LibFactory;
import com.lucaci32u4.model.parts.Component;
import com.lucaci32u4.model.parts.wiring.WireModel;
import com.lucaci32u4.ui.viewport.renderer.RenderAPI;
import com.lucaci32u4.ui.viewport.renderer.brush.Brush;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;


public class Subcurcuit {
	
	private Collection<Component> components = new ArrayDeque<>();
	private Collection<WireModel> wires = new ArrayDeque<>();
	private final ModelContainer mdl;
	
	// Pointer data
	private int pointerX, pointerY;
	private boolean editMode = false;
	
	// Selection data
	private boolean hasSelection = false;
	private boolean selecting = false;
	private boolean area = false;
	private int selX1 = 0, selY1 = 0;
	private Collection<Object> selObjects = new ArrayDeque<>();
	private Component ghost;
	private boolean ghostingVisibility = false;
	
	// Interact data
	private boolean interacting = false;
	private Object interactObj = null;
	
	
	public Subcurcuit(ModelContainer mdl) {
		this.mdl = mdl;
	}
	
	public void setPointerMode(boolean edit) {
		editMode = edit;
		if (interacting) {
			interact(pointerX, pointerY, false, true);
		}
	}
	
	public void onPointer(int x, int y) {
		pointerX = x;
		pointerY = y;
		if (ghost == null) {
			if (interacting) {
				interact(x, y, false, false);
			}
			if (selecting) {
				continueSelection(x, y);
			}
		} else {
			ghost.move(x, y);
		}
	}
	
	public void onMainButton(boolean isPressed) {
		if (editMode) {
			if (isPressed) {
				beginSelection(pointerX, pointerY);
			} else {
				endSelection(pointerX, pointerY);
			}
			selecting = isPressed;
		} else {
			interact(pointerX, pointerY, isPressed, !isPressed);
		}
	}
	
	private void beginSelection(int x, int y) {
		selX1 = x;
		selY1 = y;
		area = false;
		hasSelection = false;
		continueSelection(x, y);
	}
	
	private void continueSelection(int x, int y) {
		int selX2 = x;
		int selY2 = y;
		if (selX1 != selX2 && selY1 != selY2 && !area) area = true;
		if (area) {
			deselectAll();
			selObjects.clear();
			for (Component component : components) {
				if (CoordinateHelper.intersectDimension(selX1, selY1, selX2, selY2, component.getPositionX(), component.getPositionY(), component.getWidth(), component.getHeight())) {
					selObjects.add(component);
					component.select(true);
				}
			}
			for (WireModel wire : wires) {
				if (wire.selectArea(selX1, selY1, selX2, selY2)) {
					selObjects.add(wire);
				}
			}
		}
	}
	
	private void endSelection(int x, int y) {
		continueSelection(x, y);
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
	
	private void interact(int x, int y, boolean begin, boolean end) {
		if (begin) {
			interactObj = null;
			for (Component component : components) {
				if (CoordinateHelper.inside(x, y, component.getPositionX(), component.getPositionY(), component.getWidth(), component.getHeight())) {
					interactObj = component;
					break;
				}
			}
			interacting = interactObj != null;
		}
		if (interacting) {
			if (interactObj instanceof Component) {
				((Component) interactObj).interact(x, y, begin, end);
			}
		}
		if (end) {
			interacting = false;
			interactObj = null;
		}
	}
	
	public void addNewGhostComponent(LibFactory factory, String name, int enterX, int enterY) {
		ghost = new Component(factory.createComponent(name), this);
		ghost.move(enterX, enterY);
		invalidateGraphics();
	}
	
	public void setGhostingVisibility(boolean visibility) {
		ghostingVisibility = visibility;
		invalidateGraphics();
	}

	public void endGhosting(boolean place) {
		if (place) components.add(ghost);
		ghost = null;
	}
	
	public void invalidateGraphics() {
		mdl.invalidateGraphics(this);
	}

	public void render(RenderAPI pencil, boolean attach, boolean detach) {
		for (WireModel wire : wires) {
			wire.onDraw(pencil, false, false);
		}
		for (Component component : components) {
			component.render(pencil, attach, detach);
		}
		if (ghostingVisibility && ghost != null) ghost.render(pencil, false, false);
	}
}
