package com.lucaci32u4.core;

import org.jetbrains.annotations.NotNull;

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
    
    public interface Handler {
		LogicPin[] onBegin();
		void onSimulationSignalEvent();
		void onSimulationInterrupt(Interrupter interrupter);
		void onEnd();
	}
    
    private LogicContainer container;
    private Handler handler;
    private UUID uuid;
    
    public LogicComponent(@NotNull Handler handler) {
    	this.handler = handler;
	}
    LogicPin[] onBegin(@NotNull LogicContainer container, @NotNull UUID uuid) {
    	this.container = container;
    	this.uuid = uuid;
    	return handler.onBegin();
	}
    void onSimulationSignalEvent() {
    	handler.onSimulationSignalEvent();
	}
    void onSimulationInterrupt(Interrupter interrupter) {
    	handler.onSimulationInterrupt(interrupter);
	}
    void onEnd() {
    	handler.onEnd();
	}
	
	void setUUID(@NotNull UUID uuid) {
    	this.uuid = uuid;
	}
	public UUID getUUID() {
    	return uuid;
	}
	@Override public String toString() {
    	return "c[" + LogicID.toString(uuid) + "] " + getClass().getName();
	}
}
