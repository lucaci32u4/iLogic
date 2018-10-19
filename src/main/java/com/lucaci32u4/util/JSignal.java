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

package com.lucaci32u4.util;

public class JSignal {
    private boolean current;

    public JSignal(boolean initialState) {
        current = initialState;
    }
    public synchronized boolean get() {
        return current;
    }
    public synchronized void set(boolean state) {
        current = state;
        this.notifyAll();
    }
    public synchronized boolean waitFor(boolean finalState) {
        boolean immediateExit = true;
        if (current != finalState) {
            immediateExit = false;
            boolean interruptedExc = false;
            while (current != finalState) {
                try {
                    this.wait();
                } catch (InterruptedException exc) {
                    interruptedExc = true;
                }
            }
            if (interruptedExc) Thread.currentThread().interrupt();
        }
        return immediateExit;
    }
}
