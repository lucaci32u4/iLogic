/*
 * iSignal - Digital circuit simulator
 * Copyright (C) 2018-present Iercosan-Lucaci Alexandru
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 *    ||=============================================||
 *    ||     _  _____  _          _            _     ||
 *    ||    (_)/  ___|(_)       =)_)-         | |    ||
 *    ||     _ \ `--.  _   __ _  _ __    __ _ | |    ||
 *    ||    | | `--. \| | / _` || '_ \  / _` || |    ||
 *    ||    | |/\__/ /| || (_| || | | || (_| || |    ||
 *    ||    |_|\____/ |_| \__, ||_| |_| \__,_||_|    ||
 *    ||                   __/ |                     ||
 *    ||                  |___/  Digital Simulator   ||
 *    ||=============================================||
 */

package com.lucaci32u4.ui.viewport.renderer.subsystem.java2d;

import com.lucaci32u4.ui.viewport.RenderCallback;
import com.lucaci32u4.ui.viewport.renderer.brush.Brush;
import com.lucaci32u4.ui.viewport.LogicViewport;
import com.lucaci32u4.ui.viewport.renderer.brush.OutlineBrush;
import com.lucaci32u4.ui.viewport.renderer.brush.SolidBrush;
import com.lucaci32u4.ui.viewport.renderer.brush.TextureBrush;
import com.lucaci32u4.ui.viewport.renderer.RenderAPI;
import org.jetbrains.annotations.NotNull;

import javax.swing.SwingUtilities;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;

public class Java2DSubsystem implements RenderAPI {
	
	private JPanel canvas;
	private ComponentListener canvasComponentListener;
	private Container parentContainer;
	
	// Draw callback
	private RenderCallback renderFn;

	// Per-frame data
	private float pixelsPerUnit;
	private int unitsOffsetX;
	private int unitsOffsetY;
	private int surfaceWidth;
	private int surfaceHeight;
	private Brush brush;
	private Graphics2D g2d;
	
	// Per-sprite data
	private int primitiveOffsetX;
	private int primitiveOffsetY;
	
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
	
	@Override public void init(@NotNull JPanel panel, @NotNull LogicViewport viewport, @NotNull RenderCallback callback) {
		parentContainer = panel;
		renderFn = callback;
		SwingUtilities.invokeLater(() -> {
			canvas = new JPanel() {
				@Override protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					onDraw((Graphics2D)g);
				}
			};
			canvas.setIgnoreRepaint(false);
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
	}
	
	@Override public void destroy() {
		parentContainer.remove(canvas);
		canvas.removeComponentListener(canvasComponentListener);
	}
	
	@Override public void requestRenderFrame() {
		canvas.repaint();
	}

	@Override public JPanel getCanvas() {
		return canvas;
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
	
	@SuppressWarnings("squid:SwitchLastCaseIsDefaultCheck")
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
				g2d.fillPolygon(x, y, 3);
				break;
			case OUTLINE:
				g2d.drawPolygon(x, y, 3);
				break;
		}
	}
	
	@SuppressWarnings("squid:SwitchLastCaseIsDefaultCheck")
	@Override public void drawRectangle(int left, int top, int right, int bottom) {
		left += primitiveOffsetX + unitsOffsetX;	left *= pixelsPerUnit;
		top += primitiveOffsetY + unitsOffsetY;		top *= pixelsPerUnit;
		right += primitiveOffsetX + unitsOffsetX;	right *= pixelsPerUnit;
		bottom += primitiveOffsetY + unitsOffsetY;	bottom *= pixelsPerUnit;
		g2d.setPaint((Paint)(Brush.get(brush)));
		switch (brush.getType()) {
			case TEXTURE:
			case COLOR:
				g2d.fillRect(left, top, right - left, bottom - top);
				break;
			case OUTLINE:
				g2d.drawRect(left, top, right - left, bottom - top);
				break;
		}
	}

	@Override
	public int getSurfaceWidth() {
		return surfaceWidth;
	}

	@Override
	public int getSurfaceHeight() {
		return surfaceHeight;
	}

	private void onDraw(Graphics2D g) {
		surfaceWidth = canvas.getWidth();
		surfaceHeight = canvas.getHeight();
		g2d = g;
		setCanvasOffsetUnits(0, 0);
		renderFn.onDraw(this, this);
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
