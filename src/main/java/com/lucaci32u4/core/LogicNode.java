package com.lucaci32u4.core;

import java.util.ArrayDeque;
import java.util.UUID;

public class LogicNode {
    public interface Listener {
        int TRISTATE = 0b00;
        int LEVEL_LOW = 0b10;
        int LEVEL_HIGH = 0b01;
        int CONFLICT = 0b11;
        void detect(int state, long timestamp);
    }
    
    private Listener changeListener;
	private boolean conflict;
	
	LogicContainer container;
    ArrayDeque<LogicPin> pins;
	Logic logicState;
	UUID uuid;


    public LogicNode() {
        this(null);
    }
    public LogicNode(Listener listener) {
        changeListener = listener;
        pins = new ArrayDeque<>(5);
        logicState = new Logic(Logic.LOW, false);
    }

    boolean update() {
        boolean change = false;
        int highDrivers = 0, lowDrivers = 0;
        for (LogicPin pin : pins) {
             if (pin.externalState.defined) {
                 if (pin.externalState.state) highDrivers++;
                 else lowDrivers++;
             }
        }
        Logic newState = new Logic(Logic.LOW, false);
        boolean newConflict = false;
        if (highDrivers + lowDrivers == 0) {
            newState.state = Logic.LOW;
            newState.defined = false;
        } else if (highDrivers + lowDrivers > 1) {
            newState.state = Logic.LOW;
            newState.defined = false;
            newConflict = true;
        } else {
            if (highDrivers == 1) newState.state = Logic.HIGH;
            else newState.state = Logic.LOW;
            newState.defined = true;
        }
        if ((newConflict != conflict) || Logic.senseChange(logicState, newState)) {
            change = Logic.senseChange(logicState, newState);
            conflict = newConflict;
            logicState.copy(newState);
            notifyChange();
        }
        return change;
    }
    private void notifyChange() {
        if (changeListener != null) {
            changeListener.detect(conflict ? Listener.CONFLICT : (logicState.defined ? (logicState.state ? Listener.LEVEL_HIGH : Listener.LEVEL_LOW) : Listener.TRISTATE), container.getTimeStamp());
        }
    }
	
    UUID getUUID() {
        return uuid;
    }
	void setUUID(UUID uuid) {
		this.uuid = uuid;
	}
    
    @Override public String toString() {
        return "n[" + LogicID.toString(uuid) + "] " + logicState.toString() + " " + conflict + " L(" + (changeListener != null ? changeListener.toString() : "null") + ")";
    }
}
