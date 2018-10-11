package com.lucaci32u4.UI.Viewport.Brushes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Brush {
	public enum BrushType {
		COLOR, OUTLINE, TEXTURE,
	}
	
	private BrushType type;
	private Object obj;
	
	Brush(BrushType type, Object obj) {
		this.type = type;
		this.obj = obj;
	}
	
	public BrushType getType() {
		return type;
	}
	
	public static Object get(@NotNull Brush b) {
		return b.obj;
	}
	
	public static void get(@NotNull Brush b, @Nullable Object obj) {
		b.obj = obj;
	}
}
