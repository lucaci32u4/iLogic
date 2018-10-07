package com.lucaci32u4.UI;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import javax.swing.*;
import java.awt.*;

public class LogicViewport {
	private ViewportArtifact[] sprites;
	private ViewportArtifact[] drawn;
	private int drawnCount;
	private int pixelWidth, pixelHeight;
	private int unitWidth, unitHeight;
	private int unitOffsetX, unitOffsetY;
	private float backgroudR, backgroundG, backgroundB;
	private JPanel circuitPanel;
	private Canvas canvas;
	private RenderAPI pencil;


	private void init() {
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
			pencil.initRenderer(canvas);
		}).start();
	}
	
	private static void initSwingCanvas(Canvas canvas, JPanel circuitPanel) {
		canvas.setIgnoreRepaint(true);
		circuitPanel.add(canvas);
	}

	private static class GL11Backend implements RenderAPI {
		private int width, height;
		private float backgroundR, backgroundG, backgroundB;
		private Canvas canvas;
		@Override public void initRenderer(Canvas surface){
			try {
				Display.create();
				Display.setParent(surface);
				setBackgroundColor(1, 1, 1);
				adjustToCanvasSize();
				GL11.glClearColor(backgroundR, backgroundG, backgroundB, 1);
			} catch (LWJGLException e) {
				e.printStackTrace();
			}
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
			Rectangle rect = canvas.getBounds();
			width = (int)rect.getWidth();
			height = (int)rect.getHeight();
			GL11.glViewport(0, 0, width, height);
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glOrtho(0, width, height, 0, -1, 1);
		}

		@Override public void setBackgroundColor(float r, float g, float b) {
			backgroundR = r; backgroundG = g; backgroundB = b;
		}
	}

	private interface RenderAPI extends DrawAPI {
		void initRenderer(Canvas surface);
		void destroyRenderer();
		void adjustToCanvasSize();
		void setBackgroundColor(float r, float g, float b);
	}
	public interface DrawAPI {
	
	}
}
