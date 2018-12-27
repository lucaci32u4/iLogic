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

package com.lucaci32u4.model;

import org.jetbrains.annotations.Contract;

@SuppressWarnings("squid:S00107")
public class CoordinateHelper {
	
	// LT & RB for Box and LT & RB for Obj
	@Contract(pure = true)
	public static boolean intersectRect(int lb, int tb, int rb, int bb, int lo, int to, int ro, int bo) {
		return (lb <= lo && ro <= rb) && (bb <= to && bo <= tb);
	}
	
	@Contract(pure = true)
	public static boolean intersectDimension(int lb, int tb, int rb, int bb, int objX, int objY, int width, int height) {
		return (lb <= objX && objX + width <= rb) && (tb <= objY && objY + height <= bb);
	}
	
	@Contract(pure = true)
	public static boolean inside(int x, int y, int boxX, int boxY, int height, int width) {
		return (boxX <= x && x <= boxX + width) && (boxY <= y && y <= boxY + height);
	}
	
}
