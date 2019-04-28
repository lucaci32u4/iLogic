package com.lucaci32u4.util;

public class Vector2 {
	public static final Vector2 ZERO = new Vector2(0, 0);

	public final int x;
	public final int y;

	public Vector2(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Vector2(Vector2 src) {
		x = src.x;
		y = src.y;
	}

	public Vector2 add(Vector2 adder) {
		return new Vector2(x + adder.x, y + adder.y);
	}

	public Vector2 sub(Vector2 subster) {
		return new Vector2(x - subster.x, y - subster.y);
	}

	public Vector2 mul(int fac) {
		return new Vector2(x * fac, y * fac);
	}

	public int dot(Vector2 fac) {
		return x * fac.x + y * fac.y;
	}
}
