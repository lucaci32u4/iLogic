package com.lucaci32u4.UI.Viewport;

public interface UserInputListener {
	void mouseScroll(int direction);
	void mouseLeft(boolean state);
	void mouseRight(boolean state);
	void mouseMiddle(boolean state);
	void key(int vCode, boolean state);
}
