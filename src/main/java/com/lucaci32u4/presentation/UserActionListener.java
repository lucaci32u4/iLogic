package com.lucaci32u4.presentation;

public interface UserActionListener {
	
	enum Type {
		NEW, OPEN, SAVE, EXIT, UNDO, REDO, COPY,
		PASTE, DELETE, NEWCIRC, DELCIRC, RENAMECIRC,
		ZOOM, START, STOP, PAUSE, SELCOMPMODEL,
	}
	
	void notify(Type type, String param1, String param2, long param3, long param4);
}
