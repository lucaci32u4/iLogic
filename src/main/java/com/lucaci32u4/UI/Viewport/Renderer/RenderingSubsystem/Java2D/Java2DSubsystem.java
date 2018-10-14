package com.lucaci32u4.UI.Viewport.Renderer.RenderingSubsystem.Java2D;

import com.lucaci32u4.UI.Viewport.Renderer.Brushes.Brush;
import com.lucaci32u4.UI.Viewport.LogicViewport;
import com.lucaci32u4.UI.Viewport.Renderer.RenderAPI;
import com.lucaci32u4.UI.Viewport.Renderer.VisualArtifact;
import com.lucaci32u4.util.JSignal;
import com.lucaci32u4.util.SimpleWorkerThread;

import javax.swing.SwingUtilities;
import javax.swing.JPanel;
import java.awt.Canvas;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayDeque;

public class Java2DSubsystem implements RenderAPI {

	static class DataUpdate {
		ArrayDeque<VisualArtifact> att = new ArrayDeque<>(40);
		ArrayDeque<VisualArtifact> det = new ArrayDeque<>(40);
		boolean crdValid;
		float ppu;
		int offX, offY;
	}

	private JSignal requestedPainting;
	private Canvas canvas;
	private ComponentListener canvasComponentListener;
	private Container parentContainer;
	private SimpleWorkerThread bufferWorker;

	// Update data
	private final Object updateDataLock = new Object();
	private DataUpdate producer = new DataUpdate();
	private DataUpdate consumer = new DataUpdate();

	// Draw data
	private final Object drawDataLock = new Object();
	private VisualArtifact[] sprites = new VisualArtifact[0];

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
		canvas = null;
		requestedPainting = new JSignal(false);
	}
	
	@Override public void init(JPanel panel, LogicViewport viewport) {
		parentContainer = panel;
		SwingUtilities.invokeLater(() -> {
			canvas = new Canvas() {
				@Override public void paint(Graphics g) { onDraw((Graphics2D)g); }
			};
			canvas.setIgnoreRepaint(true);
			canvas.setMinimumSize(new Dimension(10, 10));
			canvasComponentListener = new ComponentListener() {
				@Override public void componentShown(ComponentEvent e) {
					/*parentViewport.requestUpdate();*/
				}
				@Override public void componentResized(ComponentEvent e) {
					/*parentViewport.requestUpdate();*/
				}
				@Override public void componentMoved(ComponentEvent e) {
					/*parentViewport.requestUpdate();*/
				}
				@Override public void componentHidden(ComponentEvent e) {
					/*parentViewport.requestUpdate();*/
				}
			};
			canvas.addComponentListener(canvasComponentListener);
			parentContainer.add(canvas);
		});
		bufferWorker = new SimpleWorkerThread(this::runBufferJob);
		bufferWorker.start();
	}
	
	@Override public void destroy() {
		parentContainer.remove(canvas);
		canvas.removeComponentListener(canvasComponentListener);
	}
	
	@Override public boolean requestRenderFrame() {
		return false;
	}

	@Override public Canvas getCanvas() {
		return canvas;
	}

	@Override public void attach(VisualArtifact sprite) {
		synchronized (updateDataLock) {
			producer.att.add(sprite);
			producer.det.remove(sprite);
		}
	}

	@Override public void detach(VisualArtifact sprite) {
		synchronized (updateDataLock) {
			producer.det.add(sprite);
			producer.att.remove(sprite);
		}
	}

	@Override public void setCoordinates(int unitsOffsetX, int unitOffsetY, float pixelsPerUnit) {
		synchronized (updateDataLock) {
			producer.crdValid = true;
			producer.offX = unitsOffsetX;
			producer.offY = unitOffsetY;
			producer.ppu = pixelsPerUnit;
		}
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
		fromX += primitiveOffsetX + unitsOffsetX;	fromX *= pixelsPerUnit;
		fromY += primitiveOffsetY + unitsOffsetY;	fromY *= pixelsPerUnit;
		toX += primitiveOffsetX + unitsOffsetX; 	toX *= pixelsPerUnit;
		toY += primitiveOffsetY + unitsOffsetY; 	toY *= pixelsPerUnit;
		g2d.setPaint((Paint)(Brush.get(brush)));
		g2d.setStroke(new BasicStroke(thicknessPixels));
		g2d.drawLine(fromX, fromY, toX, toY);
	}
	
	@Override public void drawTriangle(int aX, int aY, int bX, int bY, int cX, int cY) {
		aX += primitiveOffsetX + unitsOffsetX;		aX *= pixelsPerUnit;
		aY += primitiveOffsetY + unitsOffsetY;		aY *= pixelsPerUnit;
		bX += primitiveOffsetX + unitsOffsetX;		bX *= pixelsPerUnit;
		bY += primitiveOffsetY + unitsOffsetY;		bY *= pixelsPerUnit;
		cX += primitiveOffsetX + unitsOffsetX;		cX *= pixelsPerUnit;
		cY += primitiveOffsetY + unitsOffsetY;		cY *= pixelsPerUnit;
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
		left += primitiveOffsetX + unitsOffsetX;	left *= pixelsPerUnit;
		top += primitiveOffsetY + unitsOffsetY;		top *= pixelsPerUnit;
		right += primitiveOffsetX + unitsOffsetX;	right *= pixelsPerUnit;
		bottom += primitiveOffsetY + unitsOffsetY;	bottom *= pixelsPerUnit;
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
		synchronized (updateDataLock) {
			DataUpdate aux = producer;
			producer = consumer;
			consumer = aux;
		}
		synchronized (drawDataLock) {
			if (consumer.crdValid) {
				unitsOffsetX = consumer.offX;
				unitsOffsetY = consumer.offY;
				pixelsPerUnit = consumer.ppu;
				consumer.crdValid = false;
			}
			int pixelWidth = canvas.getWidth();
			int pixelHeight = canvas.getHeight();
			int unitWidth = Math.round(pixelsToUnits(pixelWidth));
			int unitHeight = Math.round(pixelsToUnits(pixelHeight));
			setCanvasOffsetUnits(0, 0);
		}
	}

	private void runBufferJob() {
		synchronized (drawDataLock) {

		}
	}
}
