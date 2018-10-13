package com.lucaci32u4.UI.Viewport;

import java.awt.event.MouseEvent;

public interface UserInputListener {
	void mouseButtonEvent(MouseEvent e);
	void mouseMotionEvent(MouseEvent e, boolean drag);
	void isInsidePerimeter(boolean inside);
}
