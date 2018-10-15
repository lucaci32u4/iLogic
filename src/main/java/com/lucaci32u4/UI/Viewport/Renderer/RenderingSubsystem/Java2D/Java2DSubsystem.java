package com.lucaci32u4.UI.Viewport.Renderer.RenderingSubsystem.Java2D;

import com.lucaci32u4.UI.Viewport.Renderer.Brushes.Brush;
import com.lucaci32u4.UI.Viewport.LogicViewport;
import com.lucaci32u4.UI.Viewport.Renderer.Brushes.OutlineBrush;
import com.lucaci32u4.UI.Viewport.Renderer.Brushes.SolidBrush;
import com.lucaci32u4.UI.Viewport.Renderer.Brushes.TextureBrush;
import com.lucaci32u4.UI.Viewport.Renderer.RenderAPI;
import com.lucaci32u4.UI.Viewport.Renderer.VisualArtifact;
import com.lucaci32u4.util.SimpleWorkerThread;
import org.jetbrains.annotations.Nullable;

import javax.swing.SwingUtilities;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;

public class Java2DSubsystem implements RenderAPI {
	
	static class DataUpdate {
		ArrayDeque<VisualArtifact> att = new ArrayDeque<>(40);
		ArrayDeque<VisualArtifact> det = new ArrayDeque<>(40);
		boolean crdValid;
		float ppu;
		int offX, offY;
	}
	
	private JPanel canvas;
	private ComponentListener canvasComponentListener;
	private Container parentContainer;
	private SimpleWorkerThread bufferWorker;
	
	// Channel
	public static class ChannelData {
		DataUpdate producer;
		DataUpdate consumer;
		VisualArtifact[] sprites;
		float ppu;
		int offX, offY;
	}
	
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
	private Brush brush;
	private Graphics2D g2d;
	
	// Per-sprite data
	private int primitiveOffsetX, primitiveOffsetY;
	
	public Java2DSubsystem() {
		pixelsPerUnit = 1;
		unitsOffsetX = 0;
		unitsOffsetY = 0;
		primitiveOffsetX = 0;
		primitiveOffsetY = 0;
		brush = null;
		g2d = null;
		canvas = null;
	}
	
	@Override public void init(JPanel panel, LogicViewport viewport) {
		parentContainer = panel;
		SwingUtilities.invokeLater(() -> {
			canvas = new JPanel() {
				@Override protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					onDraw((Graphics2D)g);
				}
			};
			canvas.setIgnoreRepaint(true);
			canvas.setMinimumSize(new Dimension(10, 10));
			canvasComponentListener = new ComponentListener() {
				@Override public void componentShown(ComponentEvent e) {
					requestRenderFrame();
				}
				@Override public void componentResized(ComponentEvent e) {
					requestRenderFrame();
				}
				@Override public void componentMoved(ComponentEvent e) {
					requestRenderFrame();
				}
				@Override public void componentHidden(ComponentEvent e) {
					requestRenderFrame();
				}
			};
			canvas.addComponentListener(canvasComponentListener);
			parentContainer.setLayout(new BorderLayout());
			parentContainer.add(canvas, BorderLayout.CENTER);
		});
		bufferWorker = new SimpleWorkerThread(this::runBufferJob);
		bufferWorker.start();
	}
	
	@Override public void destroy() {
		parentContainer.remove(canvas);
		canvas.removeComponentListener(canvasComponentListener);
		bufferWorker.exit(true);
	}
	
	@Override public void requestRenderFrame() {
		canvas.repaint();
	}

	@Override public JPanel getCanvas() {
		return canvas;
	}
	
	@Override public Object switchChannel(@Nullable Object newChannelContent) {
		ChannelData current = new ChannelData();
		ChannelData replace = (ChannelData)newChannelContent;
		synchronized (drawDataLock) {
			synchronized (updateDataLock) {
				current.sprites = sprites;
				current.consumer = consumer;
				current.producer = producer;
				current.offX = unitsOffsetX;
				current.offY = unitsOffsetY;
				current.ppu = pixelsPerUnit;
				if (replace != null) {
					sprites = replace.sprites;
					consumer = replace.consumer;
					producer = replace.producer;
					unitsOffsetX = replace.offX;
					unitsOffsetY = replace.offY;
					pixelsPerUnit = replace.ppu;
				}
			}
		}
		return current;
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
//		g2d.setPaint((Paint)(Brush.get(brush)));
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
		bufferWorker.finish(true);
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
			for (VisualArtifact sprite : consumer.att) {
				sprite.onAttach(this);
				if (sprite.checkIfOnScreen(unitsOffsetX, unitsOffsetY, unitWidth, unitHeight)) sprite.onDraw(this, this);
			}
			for (VisualArtifact sprite : sprites) {
				if (sprite.checkIfOnScreen(unitsOffsetX, unitsOffsetY, unitWidth, unitHeight)) sprite.onDraw(this, this);
			}
			for (VisualArtifact sprite : consumer.det) {
				sprite.onDetach(this);
			}
		}
		bufferWorker.submit();
	}

	private void runBufferJob() {
		synchronized (drawDataLock) {
			int currentSize = sprites.length;
			VisualArtifact[] newArray = sprites;
			if (consumer.det.size() != 0) {
				for (VisualArtifact oldSprite : consumer.det) {
					for (int i = 0; i < currentSize; i++) {
						if (sprites[i] == oldSprite) {
							System.arraycopy(sprites, i + 1, sprites, i, currentSize - i - 1);
							currentSize--;
							break;
						}
					}
				}
			}
			if (consumer.att.size() != 0) {
				int newLength = consumer.att.size() + currentSize;
				if (newLength > sprites.length) {
					newArray = new VisualArtifact[newLength];
				} else if (newLength < sprites.length / 2) {
					newArray = new VisualArtifact[newLength];
				}
				if (newArray != sprites) System.arraycopy(sprites, 0, newArray, 0, currentSize);
				for (VisualArtifact sprite : consumer.att) {
					newArray[currentSize++] = sprite;
				}
				sprites = newArray;
			}
			consumer.det.clear();
			consumer.att.clear();
		}
	}
	
	@Override public SolidBrush createSolidBrush(int r, int g, int b) {
		return new SolidBrush(new Color(r, g, b));
	}
	
	@Override public OutlineBrush createOutlineBrush(int r, int g, int b) {
		return new OutlineBrush(new Color(r, g, b));
	}
	
	@Override public TextureBrush createTextureBrush(BufferedImage image, int unitWidth, int unitHeight) {
		return new TextureBrush(new TexturePaint(image, new Rectangle(0, 0, unitWidth, unitHeight)));
	}
}
