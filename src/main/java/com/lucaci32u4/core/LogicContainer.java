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

package com.lucaci32u4.core;

import com.lucaci32u4.util.JSignal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.Semaphore;

public class LogicContainer {
	
	public static class FinalState {
		public String stateDescriptor;
		public LogicComponent[] components;
		public LogicNode[] nodes;
	}
	
	static class Sync {
		static class Event {
			int eventID;
		}
		static class EventAddComponent extends Event {
			EventAddComponent() { eventID = ID; }
			static final int ID = 1;
			LogicComponent component;
		}
		static class EventAddNode extends Event {
			EventAddNode() { eventID = ID; }
			static final int ID = 2;
			LogicNode node;
		}
		static class EventAddLink extends Event {
			EventAddLink() { eventID = ID; }
			static final int ID = 3;
			LogicPin pin;
			LogicNode node;
		}
		static class EventDeleteLink extends Event {
			EventDeleteLink() { eventID = ID; }
			static final int ID = 4;
			LogicPin pin;
			LogicNode node;
		}
		static class EventDeleteComponent extends Event {
			EventDeleteComponent() { eventID = ID; }
			static final int ID = 5;
			LogicComponent component;
			
		}
		static class EventDeleteNode extends Event {
			EventDeleteNode() { eventID = ID; }
			static final int ID = 6;
			LogicNode node;
		}
		static class EventExternalInterrupt extends Event {
			EventExternalInterrupt() { eventID = ID; }
			static final int ID = 7;
			LogicComponent.Interrupter interrupter;
		}
		static class EventStopSimulation extends Event {
			EventStopSimulation() { eventID = ID; }
			static final int ID = 8;
		}
		static class EventSuspendSimulation extends Event {
			EventSuspendSimulation() { eventID = ID; }
			static final int ID = 9;
		}
		static class EventResumeSimulation extends Event {
			EventResumeSimulation() { eventID = ID; }
			static final int ID = 10;
		}
		static final int queueCapacity = 10000;
		private ArrayDeque<Event> requests;
		private Semaphore resourceLock;
		private JSignal newEvent;
		
		Sync() {
			requests = new ArrayDeque<>(queueCapacity);
			resourceLock = new Semaphore(1);
			newEvent = new JSignal(false);
		}
		
		Event retrieveEvent(boolean acceptModifications, boolean acceptStop, boolean acceptSuspendResume, boolean acceptExternalInterrupt, boolean wait) {
			Event preferred = null;
			resourceLock.acquireUninterruptibly();
			while (preferred == null) {
				if (requests.size() == 0 && wait) {
					resourceLock.release();
					newEvent.waitFor(true);
					newEvent.set(false);
					resourceLock.acquireUninterruptibly();
				}
				for (Event event : requests) {
					int evid = event.eventID;
					if (acceptModifications) {
						if (evid == EventAddComponent.ID || evid == EventAddNode.ID || evid == EventAddLink.ID || evid == EventDeleteLink.ID || evid == EventDeleteComponent.ID || evid == EventDeleteNode.ID) {
							preferred = event;
							break;
						}
					}
					if (acceptStop) {
						if (evid == EventStopSimulation.ID) {
							preferred = event;
							break;
						}
					}
					if (acceptSuspendResume) {
						if (evid == EventSuspendSimulation.ID || evid == EventResumeSimulation.ID) {
							preferred = event;
							break;
						}
					}
					if (acceptExternalInterrupt) {
						if (evid == EventExternalInterrupt.ID) {
							preferred = event;
							break;
						}
					}
				}
				if (!wait) break;
			}
			if (preferred != null) {
				requests.remove(preferred);
			}
			resourceLock.release();
			return preferred;
		}
		
		void postEvent(Event event) {
			resourceLock.acquireUninterruptibly();
			requests.offer(event);
			if (requests.size() == 0) {
				newEvent.set(true);
			}
			newEvent.set(true);
			resourceLock.release();
		}
	}
	
	private Sync sync;
	
	private Thread simulationThread;
	
	private long timeStamp;
	
	private LogicComponent[] components;
	private LogicPin[][] componentPins;
	private LogicNode[] nodes;
	
    private HashSet<LogicNode> incomingNodeUpdates;
    private HashSet<LogicComponent> incomingComponentUpdates;
    private ArrayDeque<LogicPin.PinUpdate> pinUpdateQueue;
	ArrayDeque<LogicPin.PinUpdate> inboundPinUpdates;
    
    private boolean started;
    private boolean suspended;
    private boolean stopped;
    
    String finalDescriptor;

    public LogicContainer() {
    	sync = new Sync();
		simulationThread = new Thread(this::mainSimulationEventLoop);
		timeStamp = 0;
		components = new LogicComponent[0];
		componentPins = new LogicPin[0][];
		nodes = new LogicNode[0];
        incomingNodeUpdates = new HashSet<>();
        incomingComponentUpdates = new HashSet<>();
		inboundPinUpdates = new ArrayDeque<>();
        pinUpdateQueue = new ArrayDeque<>();
        started = false;
        suspended = false;
        stopped = false;
        finalDescriptor = "";
    }
    
    private void mainSimulationEventLoop() {
    	while (!stopped) {
    		boolean stable = tickSimulation();
			Sync.Event event = sync.retrieveEvent(!suspended, true, true, true, stable);
    		if (event != null) {
				switch (event.eventID) {
					case Sync.EventAddComponent.ID:
						Sync.EventAddComponent eac = (Sync.EventAddComponent) event;
						handleAddComponent(eac.component);
						break;
					case Sync.EventAddNode.ID:
						Sync.EventAddNode ean = (Sync.EventAddNode) event;
						handleAddNode(ean.node);
						break;
					case Sync.EventDeleteComponent.ID:
						Sync.EventDeleteComponent edc = (Sync.EventDeleteComponent) event;
						handleRemoveComponent(edc.component);
						break;
					case Sync.EventDeleteNode.ID:
						Sync.EventDeleteNode edn = (Sync.EventDeleteNode) event;
						handleRemoveNode(edn.node);
						break;
					case Sync.EventAddLink.ID:
						Sync.EventAddLink eal = (Sync.EventAddLink) event;
						handleAddLink(eal.pin, eal.node);
						break;
					case Sync.EventDeleteLink.ID:
						Sync.EventDeleteLink edl = (Sync.EventDeleteLink) event;
						handleRemoveLink(edl.pin, edl.node);
						break;
					case Sync.EventExternalInterrupt.ID:
						Sync.EventExternalInterrupt eei = (Sync.EventExternalInterrupt) event;
						handleExternalInterrupt(eei.interrupter);
						break;
					case Sync.EventSuspendSimulation.ID:
						suspended = true;
						break;
					case Sync.EventResumeSimulation.ID:
						suspended = false;
						break;
					case Sync.EventStopSimulation.ID:
						stopped = true;
						handleStopSimulation();
						break;
				}
			}
		}
	}
	
	private boolean tickSimulation() {
		handlePinUpdates(pinUpdateQueue, incomingNodeUpdates);
		handleNodeUpdates(incomingNodeUpdates, incomingComponentUpdates);
		handleComponentUpdates(incomingComponentUpdates);
		handleTickCleanup(pinUpdateQueue, inboundPinUpdates, incomingNodeUpdates, incomingComponentUpdates);
		return (pinUpdateQueue.size() == 0);
	}
	
	private void handlePinUpdates(@NotNull Collection<LogicPin.PinUpdate> updates, @Nullable Collection<LogicNode> deposit) {
    	if (updates.size() != 0) {
			int nextTickDistance = Integer.MAX_VALUE;
			for (LogicPin.PinUpdate update : pinUpdateQueue) {
				if (nextTickDistance > update.ticksRemaining) nextTickDistance = update.ticksRemaining;
			}
			timeStamp += nextTickDistance;
			for (LogicPin.PinUpdate update : updates) {
				update.ticksRemaining -= nextTickDistance;
				if (update.ticksRemaining == 0) {
					if (Logic.senseChange(update.pin.externalState, update.newState)) {
						update.pin.externalState.copy(update.newState);
						if (update.pin.node != null && deposit != null) deposit.add(update.pin.node);
					}
				}
			}
		}
	}
	private void handleNodeUpdates(@NotNull Collection<LogicNode> updates, @NotNull Collection<LogicComponent> deposit) {
		for (LogicNode node : updates) {
			boolean change = node.update();
			if (change) {
				for (LogicPin pin : node.pins) {
					if (pin.listening) {
						deposit.add(pin.component);
					}
				}
			}
		}
	}
	private void handleComponentUpdates(@NotNull Collection<LogicComponent> updates) {
		for (LogicComponent component : updates) {
			component.onSimulationSignalEvent();
		}
	}
	private void handleTickCleanup(@NotNull Collection<LogicPin.PinUpdate> mainQueue, @NotNull Collection<LogicPin.PinUpdate> subsequentQueue, @Nullable Collection<LogicNode> incomingNodeUpdates, @Nullable Collection<LogicComponent> incomingComponentUpdates) {
    	mainQueue.removeIf((upd) -> upd.ticksRemaining == 0);
    	mainQueue.addAll(subsequentQueue);
    	subsequentQueue.clear();
    	if (incomingNodeUpdates != null) incomingNodeUpdates.clear();
    	if (incomingComponentUpdates != null) incomingComponentUpdates.clear();
	}
	
	private void handleExternalInterrupt(@NotNull LogicComponent.Interrupter interrupter) {
    	interrupter.executeInterrupt();
	}
	private void handleAddComponent(@NotNull LogicComponent component) {
    	ArrayDeque<LogicPin.PinUpdate> inbUpdates = inboundPinUpdates;
 		LogicComponent[] arrc = new LogicComponent[components.length + 1];
		LogicPin[][] arrp = new LogicPin[componentPins.length + 1][];
 		System.arraycopy(components, 0, arrc, 0, components.length);
 		System.arraycopy(componentPins, 0, arrp, 0, componentPins.length);
 		arrc[arrc.length - 1] = component;
 		components = arrc;
 		componentPins = arrp;
 		componentPins[componentPins.length - 1] = component.onBegin(this, UUID.randomUUID());
 		for (LogicPin pin : componentPins[componentPins.length - 1]) {
 			pin.setComponent(component);
 			pin.setUUID(UUID.randomUUID());
 			pin.setContainer(this);
		}
 		inboundPinUpdates = null;
 		component.onSimulationSignalEvent();
 		inboundPinUpdates = inbUpdates;
	}
	private void handleAddNode(@NotNull LogicNode node) {
    	LogicNode[] arr = new LogicNode[nodes.length + 1];
    	System.arraycopy(nodes, 0, arr, 0, nodes.length);
    	arr[arr.length - 1] = node;
    	nodes = arr;
    	node.setUUID(UUID.randomUUID());
    	node.container = this;
	}
	private void handleRemoveComponent(@NotNull LogicComponent component) {
    	int componentIndex = -1;
    	for (LogicComponent logicComponent : components) {
			componentIndex++;
			if (logicComponent == component) break;
		}
		if (componentIndex != -1) {
			component.onEnd();
			components[componentIndex] = components[components.length - 1];
			componentPins[componentIndex] = componentPins[componentPins.length - 1];
			LogicComponent[] arrc = new LogicComponent[components.length - 1];
			LogicPin[][] arrp = new LogicPin[componentPins.length - 1][];
			LogicPin[] throwawayPins = componentPins[componentPins.length - 1];
			System.arraycopy(components, 0, arrc, 0, arrc.length);
			System.arraycopy(componentPins, 0, arrp, 0, arrp.length);
			components = arrc;
			componentPins = arrp;
			for (LogicPin pin : throwawayPins) {
				handleRemoveLink(pin, pin.node);
			}
		}
	}
	private void handleRemoveNode(@NotNull LogicNode node) {
 		int nodeIndex = -1;
 		for (LogicNode logicNode : nodes) {
 			nodeIndex++;
 			if (logicNode == node) break;
		}
		if (nodeIndex != -1) {
			nodes[nodeIndex] = nodes[nodes.length - 1];
			LogicNode[] arrn = new LogicNode[nodes.length - 1];
			System.arraycopy(nodes, 0, arrn, 0, arrn.length);
			nodes = arrn;
			for (LogicPin pin : node.pins) {
				handleRemoveLink(pin, node);
			}
			node.container = null;
		}
	}
	private void handleAddLink(@NotNull LogicPin pin, @NotNull LogicNode node) {
    	if (pin.node == null && !node.pins.contains(pin)) {
    		pin.node = node;
    		node.pins.add(pin);
		}
		incomingNodeUpdates.add(node);
		incomingComponentUpdates.add(pin.component);
    	handleNodeUpdates(incomingNodeUpdates, incomingComponentUpdates);
    	handleComponentUpdates(incomingComponentUpdates);
    	handleTickCleanup(pinUpdateQueue, inboundPinUpdates, incomingNodeUpdates, incomingComponentUpdates);
	}
	private void handleRemoveLink(@NotNull LogicPin pin, @NotNull LogicNode node) {
    	if (pin.node == node && node.pins.contains(pin)) {
    		pin.node = null;
    		node.pins.remove(pin);
		}
		incomingNodeUpdates.add(node);
		incomingComponentUpdates.add(pin.component);
		handleNodeUpdates(incomingNodeUpdates, incomingComponentUpdates);
		handleComponentUpdates(incomingComponentUpdates);
		handleTickCleanup(pinUpdateQueue, inboundPinUpdates, incomingNodeUpdates, incomingComponentUpdates);
	}
	private void handleStopSimulation() {
 		stopped = true;
		finalDescriptor = parseState(pinUpdateQueue, inboundPinUpdates, incomingNodeUpdates, incomingComponentUpdates);
	}
	
	public void addComponent(@NotNull LogicComponent component) {
		Sync.EventAddComponent event = new Sync.EventAddComponent();
		event.component = component;
		component.scheduleContainerAheadOfTime(this);
		sync.postEvent(event);
	}
	public void removeComponent(@NotNull LogicComponent component) {
    	Sync.EventDeleteComponent event = new Sync.EventDeleteComponent();
    	event.component = component;
    	sync.postEvent(event);
	}
	public void addNode(@NotNull LogicNode node) {
    	Sync.EventAddNode event = new Sync.EventAddNode();
    	event.node = node;
    	sync.postEvent(event);
	}
	public void removeNode(@NotNull LogicNode node) {
		Sync.EventDeleteNode event = new Sync.EventDeleteNode();
		event.node = node;
		sync.postEvent(event);
	}
	public void createLink(@NotNull LogicPin pin, @NotNull LogicNode node) {
		Sync.EventAddLink event = new Sync.EventAddLink();
		event.pin = pin; event.node = node;
		sync.postEvent(event);
	}
	public void destroyLink(@NotNull LogicPin pin, @NotNull LogicNode node) {
		Sync.EventDeleteLink event = new Sync.EventDeleteLink();
		event.pin = pin; event.node = node;
		sync.postEvent(event);
	}
	public void suspendSimulation() {
    	sync.postEvent(new Sync.EventSuspendSimulation());
	}
	public void resumeSimulation() {
    	sync.postEvent(new Sync.EventResumeSimulation());
	}
	public void startSimulation() {
    	if (!simulationThread.isAlive()) {
			simulationThread.start();
		}
	}
	public FinalState stopSimulation(boolean getComponents, boolean getNodes) {
		sync.postEvent(new Sync.EventStopSimulation());
		boolean interrupted = false;
		while (simulationThread.isAlive()) {
			try {
				simulationThread.join();
			} catch (InterruptedException exc) {
				interrupted = true;
			}
		}
		if (interrupted) Thread.currentThread().interrupt();
		FinalState ret = new FinalState();
		ret.components = (getComponents ? components : null);
		ret.nodes = (getNodes ? nodes : null);
		ret.stateDescriptor = finalDescriptor;
		return ret;
	}
	
	public boolean isSuspended() {
    	return suspended;
    }
	public boolean isStarted() {
		return started;
	}
	public boolean isStopped() {
		return stopped;
	}
	
	private String parseState(@NotNull Collection<LogicPin.PinUpdate> mainQueue, @NotNull Collection<LogicPin.PinUpdate> subsequentQueue, @Nullable Collection<LogicNode> incomingNodeUpdates, @Nullable Collection<LogicComponent> incomingComponentUpdates) {
		String master = "";
		//master = master.concat("<" +  + ">\r\n");
		for (LogicComponent component : components) master = master.concat("<" + component.toString() + ">\r\n");
		for (LogicNode node : nodes) master = master.concat("<" + node.toString() + ">\r\n");
		for (LogicPin[] pins : componentPins) for (LogicPin pin : pins) master = master.concat("<" + pin.toString() + ">\r\n");
		for (LogicPin.PinUpdate update : mainQueue) master = master.concat("<" + update.toString() + ">\r\n");
		for (LogicPin.PinUpdate update : subsequentQueue) master = master.concat("<!" + update.toString() + ">\r\n");
		if (incomingNodeUpdates != null) for (LogicNode node : incomingNodeUpdates) master = master.concat("<u n" + LogicID.toString(node.getUUID()) + ">\r\n");
		if (incomingComponentUpdates != null) for (LogicComponent component : incomingComponentUpdates) master = master.concat("<u c" + LogicID.toString(component.getUUID()) + ">\r\n");
		return master;
	}
	long getTimeStamp() {
		return timeStamp;
	}
	Sync getSync() {
    	return sync;
    }
}
