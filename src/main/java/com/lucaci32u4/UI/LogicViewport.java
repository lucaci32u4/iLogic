package com.lucaci32u4.UI;

import com.lucaci32u4.util.JSignal;
import com.lucaci32u4.util.SimpleEventQueue;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class LogicViewport {
	
	private ViewportArtifact[] sprites;
	private ViewportArtifact[] drawn;
	private int drawnCount;
	private int pixelWidth, pixelHeight;
	private int unitWidth, unitHeight;
	private int unitOffsetX, unitOffsetY;
	private float backgroudR = 1, backgroundG = 1, backgroundB = 1;
	private JPanel circuitPanel;
	private Canvas canvas;
	private RenderAPI pencil;
	
	private void init(JPanel displayPanel) {
		circuitPanel = displayPanel;
		canvas = new Canvas() {
			@Override public void removeNotify() {
				super.removeNotify();
				pencil.destroyRenderer();
			}
		};
		if (SwingUtilities.isEventDispatchThread()) {
			initSwingCanvas(canvas, circuitPanel);
		} else {
			SwingUtilities.invokeLater(() -> initSwingCanvas(canvas, circuitPanel));
		}
		new Thread(() -> {
			pencil = new GL11Backend();
			pencil.setBackgroundColor(backgroudR, backgroundG, backgroundB);
			pencil.initRenderer(canvas);
			SwingUtilities.invokeLater(() -> canvas.addComponentListener(new ComponentListener() {
				public void componentShown(ComponentEvent e) {
					pencil.adjustToCanvasSize();
				}
				public void componentResized(ComponentEvent e) {
					pencil.adjustToCanvasSize();
				}
				public void componentMoved(ComponentEvent e) {
					pencil.adjustToCanvasSize();
				}
				public void componentHidden(ComponentEvent e) {
					pencil.adjustToCanvasSize();
				}
			}));
			
		}).start();
	}
	
	private static void initSwingCanvas(Canvas canvas, JPanel circuitPanel) {
		canvas.setIgnoreRepaint(true);
		circuitPanel.add(canvas);
	}

	private static class GL11Backend implements RenderAPI {
		private JSignal renderThreadFree;
		private Thread renderThread;
		private int width, height;
		private float backgroundR, backgroundG, backgroundB;
		private Canvas canvas;
		
		public GL11Backend() {
			backgroundB = backgroundG = backgroundR = 1;
		}
		@Override public void initRenderer(Canvas surface){
			renderThreadFree = new JSignal(false);
			renderThread = Thread.currentThread();
			try {
				Display.create();
				Display.setParent(surface);
				adjustToCanvasSize();
				GL11.glClearColor(backgroundR, backgroundG, backgroundB, 1);
			} catch (LWJGLException e) {
				e.printStackTrace();
			}
			renderThreadFree.setState(true);
		}

		public void render() {
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

			Display.update();
		}

		@Override public void destroyRenderer () {
			if (Display.isCreated()) {
				Display.destroy();
			}
		}

		@Override public void adjustToCanvasSize() {
			if (Thread.currentThread() == renderThread) {
				render_adjustToCanvasSize();
			} else {
				renderThreadFree.waitForState(true);
				renderThreadFree.setState(false);
			}
		}

		@Override public void setBackgroundColor(float r, float g, float b) {
			backgroundR = r; backgroundG = g; backgroundB = b;
		}
		
		@Override public int unitsToPixels(int units) {
			// TODO: Write actual code
			return units;
		}
		
		@Override public int pixelsToUnits(int pixels) {
			// TODO: Write actual code
			return pixels;
		}
		
		@Override public void setUsingPresetColors(boolean b) {
			// TODO: Write actual code
		}
		
		@Override public boolean getUsingPresetColors() {
			// TODO: Write actual code
			return false;
		}
		
		private void render_adjustToCanvasSize() {
			Rectangle rect = canvas.getBounds();
			width = (int)rect.getWidth();
			height = (int)rect.getHeight();
			GL11.glViewport(0, 0, width, height);
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glOrtho(0, width, height, 0, -1, 1);
		}
	}

	private interface RenderAPI extends ControlAPI, DrawAPI { }
	private interface ControlAPI {
		void initRenderer(Canvas surface);
		void destroyRenderer();
		void adjustToCanvasSize();
		void setBackgroundColor(float r, float g, float b);
	}
	public interface DrawAPI {
		int unitsToPixels(int units);
		int pixelsToUnits(int pixels);
		void setUsingPresetColors(boolean b);
		boolean getUsingPresetColors();
	}
}
