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

package com.lucaci32u4.ui.viewport.renderer.subsystem.opengl;

/*

public class GL2Subsystem implements LogicViewport.RenderAPI, GLEventListener {
	private GLProfile glProfile;
	private GLCapabilities glCapabilities;
	private GLCanvas canvas;
	private boolean readjust;
	private boolean shown;
	private boolean hidden;
	@Override public boolean initRenderer(JPanel panel, JSignal signalForceWake) {
		glProfile = GLProfile.getDefault();
		glCapabilities = new GLCapabilities(glProfile);
		glCapabilities.setHardwareAccelerated(true);
		glCapabilities.setDoubleBuffered(true);
		glCapabilities.setRedBits(8); glCapabilities.setGreenBits(8); glCapabilities.setBlueBits(8); glCapabilities.setAlphaBits(8);
		canvas = new GLCanvas(glCapabilities);
		canvas.addGLEventListener(this);
		try {
			SwingUtilities.invokeAndWait(() -> {
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
	
	@Override public void renderFrame(VisualArtifact[] sprites) {
	
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
	
	@Override public void init(GLAutoDrawable glAutoDrawable) {
	
	}
	
	@Override public void dispose(GLAutoDrawable glAutoDrawable) {
	
	}
	
	@Override public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {
	
	}
	
	@Override public void display(GLAutoDrawable glAutoDrawable) {
	
	}
}

*/