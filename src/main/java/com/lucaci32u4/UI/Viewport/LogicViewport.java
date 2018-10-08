package com.lucaci32u4.UI.Viewport;

import com.lucaci32u4.UI.Viewport.RenderingSubsystem.GL2Subsystem;
import com.lucaci32u4.util.JSignal;

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
	private boolean running;
	private boolean readjust;
	private float backgroundR = 1, backgroundG = 1, backgroundB = 1;
	private JPanel circuitPanel;
	private RenderAPI pencil;
	private JSignal signalForceWake;
	
	private void init(JPanel displayPanel) {
		circuitPanel = displayPanel;
		new Thread(() -> {
			signalForceWake = new JSignal(false);
			pencil = new GL2Subsystem();
			pencil.setBackgroundColor(backgroundR, backgroundG, backgroundB);
			running = pencil.initRenderer(circuitPanel, signalForceWake);
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
		
		circuitPanel.add(canvas);
	}

	public interface RenderAPI extends ControlAPI, DrawAPI { }
	public interface ControlAPI {
		boolean initRenderer(JPanel panel, JSignal signalForceWake);
		void destroyRenderer();
		void adjustToCanvasSize();
		void setBackgroundColor(float r, float g, float b);
		void renderFrame();
	}
	public interface DrawAPI {
		float unitsToPixels(int units);
		float pixelsToUnits(int pixels);
		void setCanvasOffsetUnits(int offsetX, int offsetY);
		void setLineThickness(int thicknessUnits);
		void drawLine(int fromX, int fromY, int toX, int toY);
		void drawTriangle(int aX, int aY, int bX, int bY, int cX, int cY);
		void drawRectangle(int left, int top, int right, int bottom);
		void drawRectangle(int left, int top, int right, int bottom, int timesRollover);
	}
}
