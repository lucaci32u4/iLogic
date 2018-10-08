package com.lucaci32u4.UI.Viewport;

public class Color {
	public static final int COLOR_LEVEL_TRISTATE = 0b00;
	public final static int COLOR_LEVEL_LOW = 0b01;
	public final static int COLOR_LEVEL_HIGH = 0b10;
	public static final int COLOR_LEVEL_ERROR = COLOR_LEVEL_LOW | COLOR_LEVEL_HIGH | COLOR_LEVEL_TRISTATE;
	public short R, G, B;
	public Color(short R, short G, short B) {
		this.set(R, G, B);
	}
	public void set(short R, short G, short B) {
		this.R = R; this.G = G; this.B = B;
	}
}
