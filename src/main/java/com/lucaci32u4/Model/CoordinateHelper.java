package com.lucaci32u4.model;

public class CoordinateHelper {
	public static final long X_MASK = 0xFFFFFFFF00000000L;
	public static final long Y_MASK = 0x00000000FFFFFFFFL;
	public static final long SHIFT = 32;
	
	public static boolean intersectRect(long ltBox, long rbBox, long ltObj, long rbObj) {
		long aux = 0;
		long lb = ltBox << SHIFT;
		long tb = ltBox & X_MASK;
		long rb = rbBox << SHIFT;
		long bb = rbBox & X_MASK;
		long lo = ltObj << SHIFT;
		long to = ltObj & X_MASK;
		long ro = rbObj << SHIFT;
		long bo = rbObj & X_MASK;
		return (lb <= lo && ro <= rb) && (tb <= to && bo <= bb);
	}
	
	public static boolean intersectDimension(long ltBox, long rbBox, long posObj, long dimObj) {
		return intersectRect(ltBox, rbBox, posObj, add(posObj, dimObj));
	}
	
	public static boolean inside(long pos, long posBox, long dimBox) {
		return intersectRect(posBox, add(posBox, dimBox), pos, pos);
	}
	
	public static long add(long pos, long dim) {
		return (((pos & X_MASK) + (dim & X_MASK)) & X_MASK) |
			   (((pos << SHIFT) + (dim << SHIFT)) >>> SHIFT);
	}
	public static long lt(long e1, long e2) {
		long l = e1 << SHIFT;
		long t = e2 & X_MASK;
		long r = e2 << SHIFT;
		long b = e2 & X_MASK;
		return (l > r ? r : l) | ((t > b ? b : t) >>> SHIFT);
	}
	
	public static long rb(long e1, long e2) {
		long l = e1 << SHIFT;
		long t = e2 & X_MASK;
		long r = e2 << SHIFT;
		long b = e2 & X_MASK;
		return (l < r ? r : l) | ((t < b ? b : t) >>> SHIFT);
	}
}
