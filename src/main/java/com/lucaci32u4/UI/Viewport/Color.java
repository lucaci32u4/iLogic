package com.lucaci32u4.UI.Viewport;

public class Color {
	public static Color BACKGROUND 	= new Color((short)256, (short)0, (short)0);
	public static Color PATTERN 	= new Color((short)257, (short)0, (short)0);
	public static Color TRISTATE 	= new Color((short)258, (short)0, (short)0);
	public static Color LOW 		= new Color((short)259, (short)0, (short)0);
	public static Color HIGH 		= new Color((short)260, (short)0, (short)0);
	public static Color ERROR 		= new Color((short)261, (short)0, (short)0);
	public short R, G, B;
	public Color(short R, short G, short B) {
		this.set(R, G, B);
	}
	public void set(short R, short G, short B) {
		this.R = R; this.G = G; this.B = B;
	}
}
