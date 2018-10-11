package com.lucaci32u4.UI.Viewport;

//import com.lucaci32u4.UI.Viewport.RenderingSubsystem.GL2Subsystem;
import com.lucaci32u4.UI.Viewport.Brushes.Brush;
import com.lucaci32u4.UI.Viewport.Brushes.ColorBrush;
import com.lucaci32u4.UI.Viewport.RenderingSubsystem.Java2DSubsystem;
import com.lucaci32u4.util.JSignal;

import javax.swing.*;
import java.awt.*;

public class LogicViewport {
	
	private ViewportArtifact[] sprites;
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
			pencil = new Java2DSubsystem();
			pencil.setBackgroundBrush(ColorBrush.BACKGROUND);
			running = pencil.initRenderer(circuitPanel, signalForceWake);
			while (running) {
				if (readjust) {
					pencil.adjustToCanvasSize();
				}
				pencil.renderFrame(sprites);
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
		void setBackgroundBrush(Brush bkgndBrush);
		void renderFrame(ViewportArtifact[] sprites);
		void setSpaceScale(float pixelsPerUnit);
		void setSpaceOffset(int offsetX, int offsetY);
	}
	public interface DrawAPI {
		float unitsToPixels(int units);
		float pixelsToUnits(int pixels);
		void setCanvasOffsetUnits(int offsetX, int offsetY);
		void setLineThickness(int thicknessPixels);
		void setBrush(Brush brush);
		void drawLine(int fromX, int fromY, int toX, int toY);
		void drawTriangle(int aX, int aY, int bX, int bY, int cX, int cY);
		void drawRectangle(int left, int top, int right, int bottom);
	}
	
	public interface ResourceAPI {
	
	}
}
