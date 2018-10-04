package com.lucaci32u4.UI;


import javax.media.opengl.*;

public class LogicViewport implements GLEventListener {
	private ViewportArtifact[] sprites;
	private int[] drawnIndexes;
	private int drawnIndexesCount;
	@Override
	public void init(GLAutoDrawable glAutoDrawable) {
	
	}
	
	@Override
	public void display(GLAutoDrawable glAutoDrawable) {
		final GL2 gl = glAutoDrawable.getGL().getGL2();
		gl.glFlush();
		glAutoDrawable.swapBuffers();
	}
	
	@Override
	public void dispose(GLAutoDrawable glAutoDrawable) {
	
	}
	
	@Override
	public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {
	
	}
	
	ViewportArtifact pick(int x, int y) {
		ViewportArtifact select = null;
		for (int i = 0, index = drawnIndexes[i]; i < drawnIndexesCount; index = drawnIndexes[++i]) {
			if (sprites[index].checkIfOnPoint(x, y)) {
				select = sprites[index];
				break;
			}
		}
		return select;
	}
}
