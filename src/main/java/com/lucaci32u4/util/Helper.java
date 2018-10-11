package com.lucaci32u4.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Helper {
	public static InputStream fread(String file) {
		InputStream res = null;
		try {
			res = new FileInputStream(new File(file));
		} catch (IOException e) {
			res = null;
		}
		return res;
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
}
