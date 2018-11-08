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

package com.lucaci32u4.Core;

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
    	return "p[" + LogicID.toString(uuid) + "] c[" + LogicID.toString(component != null ? component.getUUID() : new UUID(0, 0)) + "] n[" + LogicID.toString(node != null ? node.uuid : new UUID(0, 0)) + "] " + externalState.toString() + " " + listening;
	}
}
