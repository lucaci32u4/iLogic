package com.lucaci32u4.UI.Viewport.RenderingSubsystem.Java2D;

import com.lucaci32u4.UI.Viewport.Brushes.Brush;
import com.lucaci32u4.UI.Viewport.LogicViewport;
import com.lucaci32u4.UI.Viewport.ViewportArtifact;
import com.lucaci32u4.util.JSignal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.Semaphore;

public class Java2DSubsystem implements LogicViewport.RenderAPI {
	private LogicViewport.DrawData drawData;
	private Semaphore painter;
	private JSignal requestedPainting;
	private Canvas canvas;
	private ComponentListener canvasComponentListener;
	private Container parentContainer;
	private LogicViewport parentViewport;
	
	// Per-frame data
	private float pixelsPerUnit;
	private int unitsOffsetX, unitsOffsetY;
	private int primitiveOffsetX, primitiveOffsetY;
	private Brush brush;
	private Graphics2D g2d;
	
	public Java2DSubsystem() {
		pixelsPerUnit = 1;
		unitsOffsetX = 0;
		unitsOffsetY = 0;
		primitiveOffsetX = 0;
		primitiveOffsetY = 0;
		brush = null;
		g2d = null;
		drawData = null;
		canvas = null;
		painter = new Semaphore(1);
		requestedPainting = new JSignal(false);
	}
	
	@Override public void initRenderer(JPanel panel, LogicViewport viewport) {
		parentContainer = panel;
		parentViewport = viewport;
		SwingUtilities.invokeLater(() -> {
			canvas = new Canvas() {
				@Override public void paint(Graphics g) { onDraw((Graphics2D)g); }
			};
			canvas.setIgnoreRepaint(true);
			canvas.setMinimumSize(new Dimension(10, 10));
			canvasComponentListener = new ComponentListener() {
				@Override public void componentShown(ComponentEvent e) {
					parentViewport.requestUpdate();
				}
				@Override public void componentResized(ComponentEvent e) {
					parentViewport.requestUpdate();
				}
				@Override public void componentMoved(ComponentEvent e) {
					parentViewport.requestUpdate();
				}
				@Override public void componentHidden(ComponentEvent e) {
					parentViewport.requestUpdate();
				}
			};
			canvas.addComponentListener(canvasComponentListener);
			parentContainer.add(canvas);
		});
	}
	
	@Override public void destroyRenderer() {
		parentContainer.remove(canvas);
		canvas.removeComponentListener(canvasComponentListener);
	}
	
	@Override public boolean requestRenderFrame(LogicViewport.DrawData drawData) {
		boolean immediate = painter.availablePermits() != 0;
		if (immediate) {
			painter.acquireUninterruptibly();
			this.drawData  = drawData;
			SwingUtilities.invokeLater(() -> canvas.repaint());
			immediate = true;
		} else requestedPainting.set(true);
		return immediate;
	}
	
	@Override public void setSpaceScale(float pixelsPerUnit) {
		this.pixelsPerUnit = pixelsPerUnit;
	}
	
	@Override public void setSpaceOffset(int offsetX, int offsetY) {
		unitsOffsetX = offsetX;
		unitsOffsetY = offsetY;
	}
	
	@Override public void setCanvasOffsetUnits(int offsetX, int offsetY) {
		primitiveOffsetX = offsetX;
		primitiveOffsetY = offsetY;
	}
	
	@Override public void setBrush(Brush brush) {
		this.brush = brush;
	}
	
	@Override public float pixelsToUnits(int pixels) {
		return pixels / pixelsPerUnit;
	}
	
	@Override public float unitsToPixels(int units) {
		return units * pixelsPerUnit;
	}
	
	@Override public void drawLine(int fromX, int fromY, int toX, int toY, float thicknessPixels) {
		fromX += primitiveOffsetX;	fromX *= pixelsPerUnit;
		fromY += primitiveOffsetY;	fromY *= pixelsPerUnit;
		toX += primitiveOffsetX;	toX *= pixelsPerUnit;
		toY += primitiveOffsetY;	toY *= pixelsPerUnit;
		g2d.setPaint((Paint)(Brush.get(brush)));
		g2d.setStroke(new BasicStroke(thicknessPixels));
		g2d.drawLine(fromX, fromY, toX, toY);
	}
	
	@Override public void drawTriangle(int aX, int aY, int bX, int bY, int cX, int cY) {
		aX += primitiveOffsetX;		aX *= pixelsPerUnit;
		aY += primitiveOffsetY;		aY *= pixelsPerUnit;
		bX += primitiveOffsetX;		bX *= pixelsPerUnit;
		bY += primitiveOffsetY;		bY *= pixelsPerUnit;
		cX += primitiveOffsetX;		cX *= pixelsPerUnit;
		cY += primitiveOffsetY;		cY *= pixelsPerUnit;
		int[] x = { aX, bX, cX };
		int[] y = { aY, bY, cY };
		g2d.setPaint((Paint)(Brush.get(brush)));
		switch (brush.getType()) {
			case TEXTURE:
			case COLOR:
				g2d.drawPolygon(x, y, 3);
				break;
			case OUTLINE:
				g2d.fillPolygon(x, y, 3);
				break;
		}
	}
	
	@Override public void drawRectangle(int left, int top, int right, int bottom) {
		left += primitiveOffsetX;		left *= pixelsPerUnit;
		top += primitiveOffsetY;		top *= pixelsPerUnit;
		right += primitiveOffsetX;		right *= pixelsPerUnit;
		bottom += primitiveOffsetY;		bottom *= pixelsPerUnit;
		g2d.setPaint((Paint)(Brush.get(brush)));
		switch (brush.getType()) {
			case TEXTURE:
			case COLOR:
				g2d.drawRect(left, top, right - left, bottom - top);
				break;
			case OUTLINE:
				g2d.fillRect(left, top, right - left, bottom - top);
				break;
		}
	}
	
	private void onDraw(Graphics2D g) {
		g2d = g;
		ViewportArtifact[] sprites = drawData.sprites;
		Collection<ViewportArtifact> pendingAttach = drawData.pendingAttach;
		Collection<ViewportArtifact> pendingDetach = drawData.pendingDetach;
		int widthPixels = canvas.getWidth();
		int heightPixels = canvas.getHeight();
		int widthUnits = (int)pixelsToUnits(widthPixels);
		int heightUnits = (int)pixelsToUnits(heightPixels);
		for (ViewportArtifact sprite : pendingAttach) {
			sprite.onAttach(this);
		}
		drawData.bkgndSprite.onDraw(this, this);
		for (ViewportArtifact sprite : sprites) {
			if (sprite.checkIfOnScreen(unitsOffsetX, unitsOffsetY, widthUnits, heightUnits)) {
				sprite.onDraw(this, this);
			}
		}
		for (ViewportArtifact sprite : pendingDetach) {
			sprite.onDetach(this);
		}
		painter.release();
	}
}
