package com.lucaci32u4.UI;


import javax.media.opengl.*;

public class LogicViewport implements GLEventListener {
	private ViewportArtifact[] sprites;
	private ViewportArtifact[] drawn;
	private int drawnCount;
	private int pixelWidth, pixelHeight;
	private int unitWidth, unitHeight;
	private int unitOffsetX, unitOffsetY;
	@Override
	public void init(GLAutoDrawable glAutoDrawable) {
	
	}
	
	@Override
	public void display(GLAutoDrawable glAutoDrawable) {
		GL2 gl = glAutoDrawable.getGL().getGL2();
		drawnCount = 0;
		for (ViewportArtifact sprite : sprites) {
			if (sprite.isVisible()) {
				if (sprite.checkIfOnScreen(unitOffsetX, unitOffsetY, unitWidth, unitHeight)) {
					drawn[drawnCount++] = sprite;
					sprite.onDraw(gl);
				}
			}
		}
		gl.glFlush();
		glAutoDrawable.swapBuffers();
	}
	
	@Override
	public void dispose(GLAutoDrawable glAutoDrawable) {
	
	}
	
	@Override
	public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int w, int h) {
		pixelWidth = w;
		pixelHeight = h;
	}
	
	ViewportArtifact pick(int x, int y) {
		ViewportArtifact select = null;
		
		return select;
	}
}
