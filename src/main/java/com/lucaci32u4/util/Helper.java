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

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.*;

public class Helper {
	
	public final static int Kilobyte = 1024;
	public final static int Megabyte = 1024 * Kilobyte;
	public final static int Gigabyte = 1024 * Megabyte;
	
	public static String freadText(InputStream res) {
		String str = null;
		try {
			if (res != null) {
				str = IOUtils.toString(res, "UTF-8");
				res.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str;
	}
	
	public static int step(int origin, int current, int stepSize) {
		return (int)(Math.round((origin - current) / (double)stepSize)) * stepSize;
	}
	
	public static Object[] resize(Object[] arr, int newSize) {
		Object[] loc = arr;
		if (arr.length != newSize) {
			loc = new Object[newSize];
			System.arraycopy(arr, 0, loc, 0, (arr.length > newSize ? newSize : arr.length));
		}
		return loc;
	}
	
	public static Object[] resize(Object[] arr, int newSize, double marginError) {
		double margin = Math.abs(((float)newSize / (float)arr.length) - 1.0);
		if (newSize > arr.length || margin > marginError) {
			return resize(arr, newSize);
		} else return arr;
	}

	public static void join(Thread thread) {
		boolean interrupted = false;
		while(thread.isAlive()) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				interrupted = true;
			}
		}
		if (interrupted) Thread.currentThread().interrupt();
	}
}
