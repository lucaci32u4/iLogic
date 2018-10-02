package com.lucaci32u4.core;

import java.util.UUID;

public class LogicPin {
    protected static class PinUpdate {
        LogicPin pin;
        Logic newState;
        int ticksRemaining;
        PinUpdate(LogicPin p, Logic state, int ticks) { pin = p; newState = state; ticksRemaining = ticks; }
		@Override public String toString() {
        	return "u p[" + LogicID.toString(pin.uuid) + "] " + ticksRemaining + " " + newState.state + " " + newState.defined;
		}
    }
    private LogicContainer container;
    LogicComponent component;
    LogicNode node;
    Logic externalState;
    boolean listening;
    private UUID uuid;
    
    public LogicPin(LogicComponent component) {
    	container = null;
    	node = null;
    	this.component = component;
    	externalState = new Logic(Logic.LOW, false);
    	listening = false;
	}
	void setContainer(LogicContainer container) {
    	this.container = container;
	}
    void setUUID(UUID uuid) {
    	this.uuid = uuid;
	}
	
	public void drive(int driverMode, int ticksDelay, boolean state) {
		boolean activeDriver = (driverMode & (state ? Logic.HIGH_DRIVER : Logic.LOW_DRIVER)) != 0;
		if (container.inboundPinUpdates != null) {
			PinUpdate update = new PinUpdate(this, new Logic(state, activeDriver), ticksDelay);
			container.inboundPinUpdates.push(update);
		} else {
			externalState.state = state;
			externalState.defined = activeDriver;
		}
	}
    public Logic read() {
    	if (node != null) {
			return (listening ? new Logic(node.logicState) : null);
		} else {
    		return (listening ? new Logic(Logic.LOW, false) : null);
		}
    }
    public void setListening(boolean isListening) {
        listening = isListening;
    }
    
    @Override public String toString() {
    	return "p[" + LogicID.toString(uuid) + "] c[" + LogicID.toString(component != null ? component.uuid : new UUID(0, 0)) + "] n[" + LogicID.toString(node != null ? node.uuid : new UUID(0, 0)) + "] " + externalState.toString() + " " + listening;
	}
}
