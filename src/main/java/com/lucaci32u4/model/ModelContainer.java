package com.lucaci32u4.model;

import com.lucaci32u4.model.library.LibFactory;
import com.lucaci32u4.presentation.UserActionListener;
import com.lucaci32u4.presentation.UserPointerListener;
import com.lucaci32u4.presentation.ViewControllerInterface;
import com.lucaci32u4.main.Const;
import com.lucaci32u4.ui.viewport.RenderCallback;
import com.lucaci32u4.ui.viewport.renderer.RenderAPI;
import com.lucaci32u4.util.Helper;
import com.lucaci32u4.util.JSignal;

import java.awt.event.MouseEvent;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("squid:S1659") // Declaring on the same line
public class ModelContainer implements RenderCallback {
	
	private ModelThread modelThread = new ModelThread();
	
	private UAL ual = new UAL();
	private UPL upl = new UPL();
	
	private final Object wcixLock = new Object();
	private Collection<ViewControllerInterface> wcix = new ArrayDeque<>();
	
	private final Object reserveLock = new Object();
	private final Object queueLock = new Object();
	private volatile ListableAction reserveTopPtr = null, reserveBottomPtr = null;
	private volatile ListableAction topPtr = null, bottomPtr = null;
	
	private final Object pointerLock = new Object();
	private volatile int posX = 0, posY = 0;
	private volatile boolean left = false, right = false, middle = false;
	private volatile boolean inside = false;
	
	private Subcurcuit mainCirc = null;
	private final HashMap<String, LibFactory> libs = new HashMap<>();
	private LibFactory currentPlaceFactory = null;
	private String currentPlaceModel = null;
	private boolean ghosting = false;
	
	private final Object newLibsLock = new Object();
	private final Collection<LibFactory> newLibs = new ArrayList<>();
	
	public void init() {
		int bufferSize = Integer.parseInt(Const.query("userInput.buffersize"));
		synchronized (reserveLock) {
			reserveBottomPtr = new ListableAction();
			for (int i = 1; i < bufferSize; i++) {
				reserveTopPtr = new ListableAction();
				reserveBottomPtr.next = reserveTopPtr;
			}
		}
		modelThread.setName("ModelThread");
		modelThread.start();
		mainCirc = new Subcurcuit(this);
	}
	
	private class ModelThread extends Thread {
		private AtomicBoolean running = new AtomicBoolean(true);
		int lastX = 0, lastY = 0;
		Handler h = new Handler();
		@Override public void run() {
			boolean lastLeft = false, lastRight = false, lastMiddle = false;
			boolean lastInside = false;
			boolean posChange = false;
			boolean leftChange = false;
			boolean rightChange = false;
			boolean midChange = false;
			ListableAction currentAction, listStart;
			while (running.get()) {
				Sleeper.sleep();
				synchronized (newLibsLock) {
					for (LibFactory lib : newLibs) {
						libs.put(lib.getFamilyName(), lib);
						String[] compNames = lib.getComponentsName();
						for (ViewControllerInterface wci : wcix) {
							for (String name : compNames) {
								wci.addSimulationComponentModel(lib.getFamilyName(), name, lib.getComponentIcon(name));
							}
						}
					}
					newLibs.clear();
				}
				synchronized (pointerLock) {
					if (posX != lastX || posY != lastY) {
						lastX = posX;
						lastY = posY;
						posChange = true;
					}
					if (inside != lastInside) {
						lastInside = inside;
						if (currentPlaceFactory != null && currentPlaceModel != null) {
							if (lastInside) {
								mainCirc.addNewGhostComponent(currentPlaceFactory, currentPlaceModel, posX, posY);
								mainCirc.setGhostingVisibility(true);
								currentPlaceFactory = null;
								currentPlaceModel = null;
								ghosting = true;
								System.out.printf("Placing");
							} else {
								if (ghosting) {
									mainCirc.setGhostingVisibility(false);
									System.out.printf("Placing suspended");
								}
							}
						}
					}
					if (lastLeft != left) {
						lastLeft = left;
						leftChange = true;
					}
					if (lastRight != right) {
						lastRight = right;
						rightChange = true;
					}
					if (lastMiddle != middle) {
						lastMiddle = middle;
						midChange = true;
					}
				}
				if (posChange) h.pointerMoved(lastX, lastY);
				if (leftChange) h.mainPointer(lastLeft);
				if (rightChange) h.secondaryPointer(lastRight);
				if (midChange) h.auxButton(lastMiddle);
				posChange = leftChange = rightChange = midChange = false;
				synchronized (queueLock) {
					listStart = topPtr;
					topPtr = null;
					bottomPtr = null;
				}
				currentAction = listStart;
				while (currentAction != null) {
					h.userAction(currentAction.source, currentAction.type, currentAction.param1, currentAction.param2, currentAction.param3, currentAction.param4);
					currentAction = currentAction.next;
				}
				synchronized (reserveLock) {
					reserveTopPtr.next = listStart;
					while (reserveTopPtr.next != null) reserveTopPtr = reserveBottomPtr.next;
				}
			}
			for (ViewControllerInterface wci : wcix) {
				wci.destroy();
			}
			// TODO: Additional cleanup
		}
		class Handler {
			private int x = 0, y = 0;
			void pointerMoved(int x, int y) {
				this.x = x;
				this.y = y;
				mainCirc.onPointer(x, y);
			}
			
			void mainPointer(boolean pressed) {
				mainCirc.onMainButton(pressed);
			}
			
			void secondaryPointer(boolean pressed) {
			
			}
			
			void auxButton(boolean pressed) {
			
			}
			
			@SuppressWarnings("all")
			void userAction(ViewControllerInterface source, UserActionListener.Type type, String param1, String param2, long param3, long param4) {
				switch (type) {
					case NEW:
						break;
					case OPEN:
						break;
					case SAVE:
						break;
					case EXIT:
						ViewControllerInterface.ExitDialogResult result = source.showExitDialog();
						if (result.save) {
							userAction(source, UserActionListener.Type.SAVE, null, null, 0, 0);
						}
						if (result.exit) {
							ModelContainer.this.destroy();
						}
						break;
					case UNDO:
						break;
					case REDO:
						break;
					case COPY:
						break;
					case PASTE:
						break;
					case DELETE:
						break;
					case NEWCIRC:
						break;
					case DELCIRC:
						break;
					case RENAMECIRC:
						break;
					case ZOOM:
						break;
					case START:
						break;
					case STOP:
						break;
					case PAUSE:
						break;
					case SELCOMPMODEL:
						System.out.println("==============================COMPONENT===================MODEL=======" + param1 + param2);
						currentPlaceFactory = libs.get(param1);
						if (currentPlaceFactory != null) {
							String[] possibleModels = currentPlaceFactory.getComponentsName();
							for (String model : possibleModels) {
								if (model.equals(param2)) {
									possibleModels = null;
									break;
								}
							}
							if (possibleModels == null) {
								currentPlaceModel = param2;
							} else throw new IllegalArgumentException();
						}
						break;
				}
			}
		}
	}
	
	public void addViewController(ViewControllerInterface wci) {
		synchronized (wcixLock) {
			wci.init(this);
			wci.setUserActionListener(ual);
			wci.setUserPointerListener(upl);
			wcix.add(wci);
		}
	}
	
	public void destroy() {
		modelThread.running.set(false);
		Sleeper.wake();
		if (Thread.currentThread() != modelThread) {
			Helper.join(modelThread);
		}
	}
	
	void invalidateGraphics(Subcurcuit circ) {
		for (ViewControllerInterface wci : wcix) {
			wci.invalidateGraphics();
		}
	}
	
	@Override public void onDraw(RenderAPI draw, RenderAPI ctrl) {
		mainCirc.render(draw, false, false);
	}
	
	public void addLibrary(LibFactory lib) {
		synchronized (newLibsLock) {
			newLibs.add(lib);
		}
		Sleeper.wake();
	}
	
	public RenderCallback getRenderCallback() {
		return this;
	}
	
	public class UAL implements UserActionListener {
		@Override public void notify(ViewControllerInterface source, Type type, String param1, String param2, long param3, long param4) {
			ListableAction extracted = null;
			boolean hasSpace = false;
			synchronized (reserveLock) {
				if (reserveBottomPtr != null) {
					hasSpace = true;
					extracted = reserveBottomPtr;
					reserveBottomPtr = extracted.next;
				}
			}
			if (hasSpace) {
				extracted.next = null;
				extracted.source = source;
				extracted.type = type;
				extracted.param1 = param1;
				extracted.param2 = param2;
				extracted.param3 = param3;
				extracted.param4 = param4;
				synchronized (queueLock) {
					if (topPtr != null) topPtr.next = extracted;
					if (bottomPtr == null) bottomPtr = extracted;
					topPtr = extracted;
				}
				Sleeper.wake();
			}
		}
	}
	
	public class UPL implements UserPointerListener {
		
		@Override public void buttonDown(int x, int y, int button) {
			mkMask(x, y, button, true);
		}
		
		@Override public void buttonUp(int x, int y, int button) {
			mkMask(x, y, button, true);
		}
		
		@Override public void pointerMove(int x, int y) {
			mkMask(x, y, -1, false);
		}
		
		@Override public void perimeter(boolean inside) {
			ModelContainer.this.inside = inside;
		}
		
		private void mkMask(int x, int y, int button, boolean pressed) {
			synchronized (pointerLock) {
				posX = x;
				posY = y;
				switch (button) {
					case MouseEvent.BUTTON1:
						left = pressed;
						break;
					case MouseEvent.BUTTON2:
						middle = pressed;
						break;
					case MouseEvent.BUTTON3:
						right = pressed;
						break;
					case -1: // Movement
						break;
					default:
						throw new IllegalArgumentException();
				}
			}
			Sleeper.wake();
		}
	}
	
	private static class Sleeper {
		private static final JSignal sig = new JSignal(false);
		static void sleep() {
			sig.waitAndSet(true, false);
		}
		
		static void wake() {
			sig.set(true);
		}
	}
	
	private class ListableAction {
		ViewControllerInterface source;
		ListableAction next;
		UserActionListener.Type type;
		String param1, param2;
		long param3, param4;
	}
}
