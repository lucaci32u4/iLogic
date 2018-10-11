package com.lucaci32u4.UI.Viewport;

//import com.lucaci32u4.UI.Viewport.RenderingSubsystem.GL2Subsystem;
import com.lucaci32u4.UI.Viewport.Brushes.Brush;
import com.lucaci32u4.UI.Viewport.RenderingSubsystem.Java2DSubsystem;
import com.lucaci32u4.util.JSignal;

import javax.swing.*;
import java.util.ArrayDeque;

public class LogicViewport {
	public static class DrawData {
		public final ArrayDeque<ViewportArtifact> pendingAttach = new ArrayDeque<>(100);
		public final ArrayDeque<ViewportArtifact> pendingDetach = new ArrayDeque<>(100);
		public ViewportArtifact[] sprites;
		public ViewportArtifact bkgndSprite;
	}
	
	private DrawData pendingData;
	private DrawData reserveData;
	private boolean running;
	private JPanel circuitPanel;
	private RenderAPI pencil;
	private JSignal signalForceWake;
	
	public void init(JPanel displayPanel, ViewportArtifact backgroundSprite) {
		circuitPanel = displayPanel;
		pendingData = new DrawData();
		reserveData = new DrawData();
		pendingData.bkgndSprite = backgroundSprite;
		reserveData.bkgndSprite = backgroundSprite;
		pendingData.sprites = new ViewportArtifact[0];
		reserveData.sprites = new ViewportArtifact[0];
		new Thread(() -> {
			signalForceWake = new JSignal(false);
			pencil = new Java2DSubsystem();
			running = pencil.initRenderer(circuitPanel, signalForceWake);
			while (running) {
				requestUpdate();
			}
			pencil.destroyRenderer();
		}).start();
	}
	
	public void attach(ViewportArtifact sprite) {
		pendingData.pendingAttach.offer(sprite);
		requestUpdate();
	}
	
	public void detach(ViewportArtifact sprite) {
		pendingData.pendingDetach.offer(sprite);
		requestUpdate();
	}
	
	private void requestUpdate() {
		boolean immediate = pencil.requestRenderFrame(pendingData);
		if (immediate) {
			// Swapping buffers
			DrawData aux = reserveData;
			reserveData = pendingData;
			pendingData = aux;
		}
	}

	public interface RenderAPI extends ControlAPI, DrawAPI, ResourceAPI { }
	public interface ControlAPI {
		boolean initRenderer(JPanel panel, JSignal signalForceWake);
		void destroyRenderer();
		boolean requestRenderFrame(DrawData drawData);
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
