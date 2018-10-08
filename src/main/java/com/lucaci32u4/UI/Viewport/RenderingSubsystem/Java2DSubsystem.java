package com.lucaci32u4.UI.Viewport.RenderingSubsystem;

import com.lucaci32u4.UI.Viewport.LogicViewport;
import com.lucaci32u4.UI.Viewport.ViewportArtifact;
import com.lucaci32u4.util.JSignal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.concurrent.Semaphore;

public class Java2DSubsystem implements LogicViewport.RenderAPI {
	private ViewportArtifact[] sprites;
	private Semaphore painterFree;
	private Canvas canvas;
	private boolean readjust;
	private boolean shown;
	private boolean hidden;
	@Override public boolean initRenderer(JPanel panel, JSignal signalForceWake) {
		try {
			SwingUtilities.invokeAndWait(() -> {
				canvas = new Canvas() {
					@Override public void paint(Graphics g) { onDraw((Graphics2D)g); }
				};
				canvas.setIgnoreRepaint(true);
				canvas.setMinimumSize(new Dimension(10, 10));
				canvas.addComponentListener(new ComponentListener() {
					@Override public void componentShown(ComponentEvent e) {
						shown = true;
						signalForceWake.set(true);
					}
					@Override public void componentResized(ComponentEvent e) {
						readjust = true;
						signalForceWake.set(true);
					}
					@Override public void componentMoved(ComponentEvent e) {
						readjust = true;
						signalForceWake.set(true);
					}
					@Override public void componentHidden(ComponentEvent e) {
						hidden = true;
						signalForceWake.set(true);
					}
				});
				panel.add(canvas);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override public void destroyRenderer() {
	
	}
	
	@Override public void renderFrame(ViewportArtifact[] sprites) {
		if (painterFree.availablePermits() != 0) {
			painterFree.acquireUninterruptibly();
			this.sprites = sprites;
			SwingUtilities.invokeLater(() -> canvas.repaint());
		}
	}
	
	@Override public void adjustToCanvasSize() {
	
	}
	
	@Override public void setBackgroundColor(float r, float g, float b) {
	
	}
	
	@Override public void setCanvasOffsetUnits(int offsetX, int offsetY) {
	
	}
	
	@Override public void setLineThickness(int thicknessUnits) {
	
	}
	
	@Override public float pixelsToUnits(int pixels) {
		return 0;
	}
	
	@Override public float unitsToPixels(int units) {
		return 0;
	}
	
	@Override public void drawLine(int fromX, int fromY, int toX, int toY) {
	
	}
	
	@Override public void drawTriangle(int aX, int aY, int bX, int bY, int cX, int cY) {
	
	}
	
	@Override public void drawRectangle(int left, int top, int right, int bottom) {
	
	}
	
	@Override public void drawRectangle(int left, int top, int right, int bottom, int timesRollover) {
	
	}
	
	private void onDraw(Graphics2D g) {
		//TODO: Drawing code
		painterFree.release();
	}
}
