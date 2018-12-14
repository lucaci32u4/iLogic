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

public class Logic {
    public static final boolean LOW = false;
    public static final boolean HIGH = true;

    public static final int FULL_DRIVER = 0b11;
    public static final int HIGH_DRIVER = 0b01;
    public static final int LOW_DRIVER = 0b10;
    public static final int TRISTATE = 0b00;

    public boolean state;
    public boolean defined;
    public Logic(boolean initialState, boolean define) { state = initialState; defined = define; }
    public Logic(@NotNull Logic source) { state = source.state; defined = source.defined; }
    public void copy(@NotNull Logic src) { state = src.state; defined = src.defined; }
    @Override
    public String toString() {
    	return (defined ? (state ? "HIGH" : "LOW") : "TRISTATE");
    }

    public static boolean senseChange(@NotNull Logic from, @NotNull Logic to) { return ((from.defined) && (from.state != to.state)) || (from.defined != to.defined); }
}
