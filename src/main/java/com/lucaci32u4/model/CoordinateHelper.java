package com.lucaci32u4.model;

import org.jetbrains.annotations.Contract;

public class CoordinateHelper {
	public static final long X_MASK = 0xFFFFFFFF00000000L;
	public static final long Y_MASK = 0x00000000FFFFFFFFL;
	public static final long SHIFT = 32;
	
	// LT & RB for Box and LT & RB for Obj
	@Contract(pure = true)
	public static boolean intersectRect(int lb, int tb, int rb, int bb, int lo, int to, int ro, int bo) {
		return (lb <= lo && ro <= rb) && (tb <= to && bo <= bb);
	}
	
	@Contract(pure = true)
	public static boolean intersectDimension(int lb, int tb, int rb, int bb, int objX, int objY, int width, int height) {
		return (lb <= objX && objX + width <= rb) && (tb <= objY && objY + height <= bb);
	}
	
	@Contract(pure = true)
	public static boolean inside(int x, int y, int boxX, int boxY, int height, int width) {
		return (boxX <= x && x <= boxX + width) && (boxY <= y && y <= boxX + height);
	}
	
}
