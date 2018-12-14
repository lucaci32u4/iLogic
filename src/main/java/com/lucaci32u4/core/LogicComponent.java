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
