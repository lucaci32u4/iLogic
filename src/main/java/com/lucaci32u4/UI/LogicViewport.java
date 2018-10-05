package com.lucaci32u4.UI;


import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import javax.media.opengl.*;

public class LogicViewport implements GLEventListener {
	private ViewportArtifact[] sprites;
	private ViewportArtifact[] drawn;
	private Texture pickSurface;
	private int drawnCount;
	private int pixelWidth, pixelHeight;
	private int unitWidth, unitHeight;
	private int unitOffsetX, unitOffsetY;
	@Override
	public void init(GLAutoDrawable glAutoDrawable) {
		pickSurface = TextureIO.newTexture(GL.GL_TEXTURE_2D);
	}
	
	@Override
	public void display(GLAutoDrawable glAutoDrawable) {
		GL2 gl = glAutoDrawable.getGL().getGL2();
		// clear buffers
		// draw checkers
		drawnCount = 0;
		for (ViewportArtifact sprite : sprites) {
			if (sprite.isVisible()) {
				if (sprite.checkIfOnScreen(unitOffsetX, unitOffsetY, unitWidth, unitHeight)) {
					sprite.setPickID(drawnCount);
					sprite.onDraw(gl);
					drawn[drawnCount++] = sprite;
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
