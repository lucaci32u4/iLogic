package com.lucaci32u4.main;

public class ApplicationSettings {
	// Language constants
	public static final String[] LANGUAGE_OPTIONS = { "English", "Romanian", "German", "French" };
	
	// Rendering constants
	public static final String[] RENDERER_OPTIONS = { "j2d_def", "j2d_ogl", "j2d_d3d", "ogl_ogl" };
	public static final String[] ANTIALIASING_OPTIONS = { "AA1", "AA2", "AA4", "AA8", "AA16" };
	
	
	// Language
	public int language = 0;
	
	// Rendering
	public int renderMethod = 0;
	public int antialiasing = 0;
}
