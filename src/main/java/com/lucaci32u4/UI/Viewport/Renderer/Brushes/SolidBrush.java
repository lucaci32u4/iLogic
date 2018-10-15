package com.lucaci32u4.UI.Viewport.Renderer.Brushes;

public class SolidBrush extends Brush {
	public static final SolidBrush BACKGROUND 	= new SolidBrush(null);
	public static final SolidBrush PATTERN 		= new SolidBrush(null);
	public static final SolidBrush TRISTATE 	= new SolidBrush(null);
	public static final SolidBrush LOW 			= new SolidBrush(null);
	public static final SolidBrush HIGH 		= new SolidBrush(null);
	public static final SolidBrush ERROR 		= new SolidBrush(null);
	public SolidBrush(Object obj) {
		super(BrushType.COLOR, obj);
	}
}

