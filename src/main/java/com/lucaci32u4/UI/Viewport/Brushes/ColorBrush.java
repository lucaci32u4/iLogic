package com.lucaci32u4.UI.Viewport.Brushes;

public class ColorBrush extends Brush {
	public static final ColorBrush BACKGROUND 	= new ColorBrush(null);
	public static final ColorBrush PATTERN 		= new ColorBrush(null);
	public static final ColorBrush TRISTATE 	= new ColorBrush(null);
	public static final ColorBrush LOW 			= new ColorBrush(null);
	public static final ColorBrush HIGH 		= new ColorBrush(null);
	public static final ColorBrush ERROR 		= new ColorBrush(null);
	ColorBrush(Object obj) {
		super(BrushType.COLOR, obj);
	}
}

