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
