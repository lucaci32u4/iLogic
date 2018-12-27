/*
 * iSignal - Digital circuit simulator
 * Copyright (C) 2018-present Iercosan-Lucaci Alexandru
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 *    ||=============================================||
 *    ||     _  _____  _          _            _     ||
 *    ||    (_)/  ___|(_)       =)_)-         | |    ||
 *    ||     _ \ `--.  _   __ _  _ __    __ _ | |    ||
 *    ||    | | `--. \| | / _` || '_ \  / _` || |    ||
 *    ||    | |/\__/ /| || (_| || | | || (_| || |    ||
 *    ||    |_|\____/ |_| \__, ||_| |_| \__,_||_|    ||
 *    ||                   __/ |                     ||
 *    ||                  |___/  Digital Simulator   ||
 *    ||=============================================||
 */

package com.lucaci32u4.model;

import com.lucaci32u4.core.LogicContainer;
import com.lucaci32u4.core.LogicNode;
import com.lucaci32u4.core.LogicPin;
import com.lucaci32u4.model.library.LibFactory;
import com.lucaci32u4.model.parts.Component;
import com.lucaci32u4.model.parts.wiring.Connectable;
import com.lucaci32u4.model.parts.wiring.WireModel;
import com.lucaci32u4.ui.viewport.renderer.RenderAPI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Collection;


public class Subcurcuit {
	
	private Collection<Component> components = new ArrayDeque<>();
	private Collection<WireModel> wires = new ArrayDeque<>();
	private final LogicContainer simulator = new LogicContainer();
	private final ModelContainer mdl;

	// Pointer data
	private int pointerX, pointerY;
	private boolean editMode = false;
	
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

	// Drawing data
	private Connectable hoverConnectable = null;
	
	public Subcurcuit(ModelContainer mdl, boolean initialEditMode) {
		this.mdl = mdl;
		this.editMode = initialEditMode;
		simulator.startSimulation();
	}
	
	public void setPointerMode(boolean edit) {
		editMode = edit;
		if (interacting && edit) {
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
			if (editMode) {
				Object lastHoverConnectable = hoverConnectable;
				hoverConnectable = getConnectableAt(x, y);
				if (lastHoverConnectable != hoverConnectable) invalidateGraphics();
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
		Connectable connectable = getConnectableAt(x, y);
		if (connectable instanceof Component.Termination) {
			routeOriginTermination = (Component.Termination) connectable;
			wireOriginX = connectable.getConnectPositionX();
			wireOriginY = connectable.getConnectPositionY();
			wiring = true;
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
				if (routeOriginTermination != null) {
					wires.add(expandingWire);
					LogicPin[] logicPins = routeOriginTermination.getPins();
					LogicNode[] logicNodes = expandingWire.getSimulatorNode();
					CircuitHelper.linkPinArray(simulator, logicPins, logicNodes);
				}
				Connectable connectable = getConnectableAt(x, y);
				if (connectable instanceof Component.Termination) {
					LogicNode[] logicNodes = expandingWire.getSimulatorNode();
					LogicPin[] logicPins = ((Component.Termination) connectable).getPins();
					CircuitHelper.linkPinArray(simulator, logicPins, logicNodes);
				}
				if (connectable != null) {
					expandingWire.continueExpand(connectable.getConnectPositionX(), connectable.getConnectPositionY());
				}
				expandingWire.endExpand();
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
			interacting = (interactObj != null);
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

	@Nullable
	private Connectable getConnectableAt(int x, int y) {
		for (Component component : components) {
			for (Connectable connectable : component.getTerminations()) {
				int connectableRadius = connectable.getConnectionRadius();
				if (CoordinateHelper.inside(x, y,
						                    connectable.getConnectPositionX() - connectableRadius,
						                    connectable.getConnectPositionY() - connectableRadius,
						                    connectableRadius * 2,
						                    connectableRadius * 2)) {
					return connectable;
				}
			}
		}
		return null;
	}
	
	void addNewGhostComponent(LibFactory factory, String name, int enterX, int enterY) {
		ghost = new Component(factory.createComponent(name), this);
		ghost.move(enterX, enterY);
		invalidateGraphics();
	}
	
	void setGhostingVisibility(boolean visibility) {
		ghostingVisibility = visibility;
		invalidateGraphics();
	}

	void endGhosting(boolean place) {
		if (place) components.add(ghost);
		ghost = null;
		invalidateGraphics();
	}

	boolean isGhosting() {
		return (ghost != null);
	}

	public LogicContainer getSimulator() {
		return simulator;
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
		Connectable localHoverConnectable = hoverConnectable;
		if (localHoverConnectable != null) {
			localHoverConnectable.render(pencil);
		}
		WireModel localExpandingWire = expandingWire;
		if (localExpandingWire != null) {
			localExpandingWire.onDraw(pencil, false, false);
		}
		Component localGhost = ghost;
		if (ghostingVisibility && localGhost != null) {
			localGhost.render(pencil, false, false);
		}
	}
}


class CircuitHelper {

	static boolean linkPinArray(@NotNull LogicContainer container, @NotNull LogicPin[] pins, @NotNull LogicNode[] nodes) {
		int length = pins.length;
		boolean valid = (length == nodes.length);
		if (valid) {
			for (int i = 0; i < length; i++) {
				container.createLink(pins[i], nodes[i]);
			}
		}
		return valid;
	}

}