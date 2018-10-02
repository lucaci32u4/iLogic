package com.lucaci32u4.core;

import java.util.UUID;

public abstract class LogicComponent {
    public static class Interrupter {
        private LogicComponent owner;
        public Interrupter(LogicComponent ownerComponent) { owner = ownerComponent; }
        public void prepareInterrupt() {
            LogicContainer.Sync.EventExternalInterrupt event = new LogicContainer.Sync.EventExternalInterrupt();
            event.interrupter = this;
            owner.container.getSync().postEvent(event);
        }
        void executeInterrupt() {
            owner.onSimulationInterrupt(this);
        }
    }
    
    private LogicContainer container;
    UUID uuid;

    protected LogicPin[] onBegin(LogicContainer container, UUID uuid) {
    	this.container = container;
    	this.uuid = uuid;
    	return new LogicPin[0];
	}
    protected abstract void onSimulationSignalEvent();
    protected abstract void onSimulationInterrupt(Interrupter interrupter);
    protected void onEnd() {
    
	}
	
	public UUID getUUID() {
    	return uuid;
	}
	@Override public String toString() {
    	return "c[" + LogicID.toString(uuid) + "] " + getClass().getName();
	}
}
