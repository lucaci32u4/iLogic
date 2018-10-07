package com.lucaci32u4.UI.Viewport;

import com.lucaci32u4.UI.ViewportArtifact;
import com.lucaci32u4.util.JSignal;
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
	private boolean readjust;
	private boolean running;
	private float backgroundR = 1, backgroundG = 1, backgroundB = 1;
	private JPanel circuitPanel;
	private Canvas canvas;
	private RenderAPI pencil;
	
	private void init(JPanel displayPanel) {
		circuitPanel = displayPanel;
		canvas = new Canvas() {
			@Override public void removeNotify() {
				super.removeNotify();
				running = false;
			}
		};
		if (SwingUtilities.isEventDispatchThread()) {
			initSwingCanvas(canvas, circuitPanel);
		} else {
			SwingUtilities.invokeLater(() -> initSwingCanvas(canvas, circuitPanel));
		}
		new Thread(() -> {
			pencil = new GL11Backend();
			pencil.setBackgroundColor(backgroundR, backgroundG, backgroundB);
			running = pencil.initRenderer(canvas);
			readjust = false;
			if (running) {
				SwingUtilities.invokeLater(() -> canvas.addComponentListener(new ComponentListener() {
					@Override public void componentShown(ComponentEvent e) {
						readjust = true;
					}
					@Override public void componentResized(ComponentEvent e) {
						readjust = true;
					}
					@Override public void componentMoved(ComponentEvent e) {
						readjust = true;
					}
					@Override public void componentHidden(ComponentEvent e) {
						readjust = true;
					}
				}));
			}
			while (running) {
				if (readjust) {
					pencil.adjustToCanvasSize();
				}
				pencil.renderFrame();
			}
			pencil.destroyRenderer();
		}).start();
	}
	
	private static void initSwingCanvas(Canvas canvas, JPanel circuitPanel) {
		canvas.setIgnoreRepaint(true);
		canvas.setMinimumSize(new Dimension(1, 1));
		circuitPanel.add(canvas);
	}

	private static class GL11Backend implements RenderAPI {
		private boolean begin = false;
		private Canvas canvas;
		private int width, height;
		private float backgroundR, backgroundG, backgroundB;
		
		// Drawing state variables
		boolean colorMapping;
		
		GL11Backend() {
			backgroundB = backgroundG = backgroundR = 1;
		}
		@Override public boolean initRenderer(Canvas surface){
			try {
				Display.setParent(surface);
				Display.create();
				if (Display.isCreated()) {
					begin = true;
					canvas = surface;
					adjustToCanvasSize();
				}
			} catch (LWJGLException e) {
				e.printStackTrace();
			}
			return begin;
		}

		@Override public void renderFrame() {
			GL11.glClearColor(backgroundR, backgroundG, backgroundB, 1);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

			Display.update();
		}

		@Override public void destroyRenderer () {
			if (Display.isCreated()) {
				Display.destroy();
			}
		}

		@Override public void adjustToCanvasSize() {
			if (begin) {
				Rectangle rect = canvas.getBounds();
				width = (int) rect.getWidth();
				height = (int) rect.getHeight();
				GL11.glViewport(0, 0, width, height);
				GL11.glMatrixMode(GL11.GL_PROJECTION);
				GL11.glLoadIdentity();
				GL11.glOrtho(0, width, height, 0, -1, 1);
			}
		}

		@Override public void setBackgroundColor(float r, float g, float b) {
			if (backgroundR != r || backgroundG != g || backgroundB != b) {
				backgroundR = r; backgroundG = g; backgroundB = b;
			}
		}
		
		@Override public int unitsToPixels(int units, boolean absolute) {
			// TODO: Write actual code
			return units;
		}
		
		@Override public int pixelsToUnits(int pixels, boolean absolute) {
			// TODO: Write actual code
			return pixels;
		}
		
		@Override public void setUsingPresetColors(boolean b) {
			colorMapping = b;
		}
		
		@Override public boolean getUsingPresetColors() {
			return colorMapping;
		}
	}

	private interface RenderAPI extends ControlAPI, DrawAPI { }
	private interface ControlAPI {
		boolean initRenderer(Canvas surface);
		void destroyRenderer();
		void adjustToCanvasSize();
		void setBackgroundColor(float r, float g, float b);
		void renderFrame();
	}
	public interface DrawAPI {
		int unitsToPixels(int units, boolean absolute);
		int pixelsToUnits(int pixels, boolean absolute);
		void setUsingPresetColors(boolean b);
		boolean getUsingPresetColors();
	}
}