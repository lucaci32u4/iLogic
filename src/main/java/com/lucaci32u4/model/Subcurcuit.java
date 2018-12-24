package com.lucaci32u4.model;

import com.lucaci32u4.model.library.LibFactory;
import com.lucaci32u4.model.parts.Component;
import com.lucaci32u4.model.parts.wiring.WireModel;
import com.lucaci32u4.ui.viewport.renderer.RenderAPI;

import java.util.ArrayDeque;
import java.util.Collection;


public class Subcurcuit {
	
	private Collection<Component> components = new ArrayDeque<>();
	private Collection<WireModel> wires = new ArrayDeque<>();
	private final ModelContainer mdl;
	
	// Pointer data
	private int pointerX, pointerY;
	private boolean editMode = true;
	
	// Selection data
	private boolean selecting = false;
	private boolean area = false;
	private int selX1 = 0, selY1 = 0;
	private Collection<Object> selObjects = new ArrayDeque<>();
	private Component ghost;
	private boolean ghostingVisibility = false;

	// Wiring
	private boolean wiring = false;
	private Component.Termination routeOriginTermination = null;
	private WireModel expandingWire = null;
	private int wireOriginX = 0;
	private int wireOriginY = 0;

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
			if (selecting || wiring) {
				continuePointerDrag(x, y);
			}
		} else {
			ghost.move(x, y);
		}
	}
	
	public void onMainButton(boolean isPressed) {
		if (editMode) {
			if (isPressed) {
				beginPointerDrag(pointerX, pointerY);
			} else {
				endPointerDrag(pointerX, pointerY);
			}
		} else {
			interact(pointerX, pointerY, isPressed, !isPressed);
		}
	}
	
	private void beginPointerDrag(int x, int y) {
		selX1 = x;
		selY1 = y;
		area = false;
		for (Component component : components) {
			for (Component.Termination termination : component.getTerminations()) {
				if (CoordinateHelper.inside(x, y,
					                        termination.getConnectPositionX() - Component.Termination.TERMINATION_RADIUS,
						                    termination.getConnectPositionY() - Component.Termination.TERMINATION_RADIUS,
					                        Component.Termination.TERMINATION_RADIUS * 2,
					                        Component.Termination.TERMINATION_RADIUS * 2)) {
					routeOriginTermination = termination;
					wireOriginX = termination.getConnectPositionX();
					wireOriginY = termination.getConnectPositionY();
					wiring = true;
					break;
				}
			}
		}
		for (WireModel wire : wires) {
			if (wire.select(x, y)) {
				wire.deselect();
				wire.beginExpand(x, y);
				routeOriginTermination = null;
				expandingWire = wire;
				wireOriginX = x;
				wireOriginY = y;
				wiring = true;
				break;
			}
		}
		continuePointerDrag(x, y);
	}
	
	private void continuePointerDrag(int x, int y) {
		if (selX1 != x && selY1 != y && !area) area = true;
		if (wiring) {
			if (area) {
				if (expandingWire == null && routeOriginTermination != null) {
					expandingWire = new WireModel();
					expandingWire.attach(this);
					expandingWire.beginExpand(wireOriginX, wireOriginY);
					expandingWire.continueExpand(x, y);
				}
				if (expandingWire != null) {
					expandingWire.continueExpand(x, y);
				} else throw new IllegalStateException();
				invalidateGraphics();
			}
		} else {
			selecting = true;
			if (area) {
				deselectAll();
				selObjects.clear();
				for (Component component : components) {
					if (CoordinateHelper.intersectDimension(selX1, selY1, x, y, component.getPositionX(), component.getPositionY(), component.getWidth(), component.getHeight())) {
						selObjects.add(component);
						component.select(true);
					}
				}
				for (WireModel wire : wires) {
					if (wire.selectArea(selX1, selY1, x, y)) {
						selObjects.add(wire);
					}
				}
			}
		}
	}
	
	private void endPointerDrag(int x, int y) {
		continuePointerDrag(x, y);
		if (selecting) {
			selecting = false;
		} else if (wiring) {
			if (expandingWire != null) {
				expandingWire.endExpand();
				if (routeOriginTermination != null) {
					wires.add(expandingWire);
				}
			}
			wiring = false;
			expandingWire = null;
		}
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

	void render(RenderAPI pencil, boolean attach, boolean detach) {
		for (WireModel wire : wires) {
			wire.onDraw(pencil, false, false);
		}
		for (Component component : components) {
			component.render(pencil, attach, detach);
		}
		if (expandingWire != null) {
			expandingWire.onDraw(pencil, false, false);
		}
		if (ghostingVisibility && ghost != null) {
			ghost.render(pencil, false, false);
		}
	}
}
