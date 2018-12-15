package com.lucaci32u4.presentation;

import com.lucaci32u4.model.ModelContainer;
import com.lucaci32u4.ui.viewport.LogicViewport;
import com.lucaci32u4.ui.viewport.UserInputListener;
import com.lucaci32u4.ui.viewport.renderer.subsystem.java2d.Java2DSubsystem;
import com.lucaci32u4.ui.windows.MainWindow;

import javax.swing.*;
import java.awt.event.MouseEvent;

public class PresentationContainer implements ViewControllerInterface {
	private final PresentationContainer pThis = this;
	private ModelContainer model = null;
	private UserActionListener fwdUal = null;
	private UserPointerListener fwdUpl = null;
	private LogicViewport viewport = null;
	private UserInputListener viewportListener = null;
	private MainWindow mainWindow = null;
	private MainWindowListener windowListener = null;
	
	@Override public void init(ModelContainer model) {
		this.model = model;
		viewport = new LogicViewport();
		windowListener = new MainWindowListener();
		mainWindow = new MainWindow(windowListener);
		viewportListener = new ViewportListener();
		viewport.init(mainWindow.getCircuitPanel(), new Java2DSubsystem(), viewportListener, model.getRenderCallback());
		mainWindow.setVisible(true);
	}
	
	@Override
	public void addSimulationComponentModel(String family, String name, Icon icon) {
	
	}
	
	@Override
	public void removeSimulationComponentModel(String family, String name) {
	
	}
	
	@Override
	public void setUserActionListener(UserActionListener listener) {
		fwdUal = listener;
	}
	
	@Override
	public void setUserPointerListener(UserPointerListener listener) {
		fwdUpl = listener;
	}
	
	@Override
	public void invalidateGraphics() {
		viewport.requestNewFrame();
	}
	
	@SuppressWarnings({"squid:S1871", "squid:S1135"}) // Identical switch cases
	private class MainWindowListener implements MainWindow.UserInputListener {
		@Override public void onUserEvent(Type subject, int param1, String param2) {
			switch (subject) {
				case NEW:
					// TODO: (lucaci32u4, 26/11/18): Implement save mechanism
					break;
				case OPEN:
					// TODO: (lucaci32u4, 26/11/18): Implement save mechanism
					break;
				case SAVE:
					// TODO: (lucaci32u4, 26/11/18): Implement save mechanism
					break;
				case EXPORT:
					// TODO: (lucaci32u4, 26/11/18): Implement exporting
					break;
				case SETTINGS:
					// TODO: (lucaci32u4, 26/11/18): Saving window
					break;
				case EXIT:
					fwdUal.notify(pThis, UserActionListener.Type.EXIT, null, null, 0, 0);
					break;
				case UNDO:
					// TODO: (lucaci32u4, 26/11/18): Implement change history remembering
					break;
				case REDO:
					// TODO: (lucaci32u4, 26/11/18): Implement change history remembering
					break;
				case COPY:
					// TODO: (lucaci32u4, 26/11/18): Implement copying
					break;
				case PASTE:
					// TODO: (lucaci32u4, 26/11/18): Implement copying
					break;
				case DELETE:
					// TODO: (lucaci32u4, 26/11/18): Implement deleting
					break;
				case SELECTALL:
					// TODO: (lucaci32u4, 26/11/18): Link selecting
					break;
				case EDITSEL:
					// TODO: (lucaci32u4, 26/11/18): Implement selection customization
					break;
				case NEWCIRCUIT:
					// TODO: (lucaci32u4, 26/11/18): Implement Saving
					break;
				case DELETECIRCUIT:
					// TODO: (lucaci32u4, 26/11/18): Implement subcircuits
					break;
				case LIBRARIES:
					// TODO: (lucaci32u4, 26/11/18): Implement extenrla libraries dialog and system
					break;
				case STARTSTOP:
					// TODO: (lucaci32u4, 26/11/18): Implement simulation status
					break;
				case RESET:
					// TODO: (lucaci32u4, 26/11/18): Implement simulation status
					break;
				case ACTIVESIMULATION:
					// TODO: (lucaci32u4, 26/11/18): Implement simulation status
					break;
			}
		}
	}
	
	@Override
	public void destroy() {
		mainWindow.close();
	}
	
	@Override
	public ViewControllerInterface.ExitDialogResult showExitDialog() {
		return mainWindow.showExitPopup();
	}
	
	private class ViewportListener implements UserInputListener {
		private boolean[] pressed = new boolean[4];
		@Override public void mouseButtonEvent(MouseEvent e) {
			pressed[e.getButton()] = (e.getModifiersEx() & (1 << (9 + e.getButton()))) != 0;
			if (fwdUpl != null) {
				if (e.getID() == MouseEvent.MOUSE_PRESSED) fwdUpl.buttonDown(e.getX(), e.getY(), e.getButton());
				if (e.getID() == MouseEvent.MOUSE_RELEASED) fwdUpl.buttonUp(e.getX(), e.getY(), e.getButton());
			}
		}
		
		@Override public void mouseMotionEvent(MouseEvent e, boolean drag) {
			if (fwdUpl != null) {
				fwdUpl.pointerMove(e.getX(), e.getY());
			}
		}
		
		@Override public void notifyPerimeter(boolean inside) {
			for (int i = 1; i < 4; i++) {
				pressed[i] = inside;
			}
		}
	}
}
