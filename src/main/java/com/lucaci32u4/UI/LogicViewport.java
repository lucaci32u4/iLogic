package com.lucaci32u4.UI;


import javax.media.opengl.*;

public class LogicViewport implements GLEventListener {
	@Override
	public void init(GLAutoDrawable glAutoDrawable) {
	
	}
	
	@Override
	public void display(GLAutoDrawable glAutoDrawable) {
		final GL2 gl = glAutoDrawable.getGL().getGL2();
		gl.glBlendFunc (GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable (GL2.GL_BLEND);
		
		gl.glEnable (GL2.GL_LINE_SMOOTH);
		gl.glHint (GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);
		
		//drawing the base
		gl.glBegin (GL2.GL_LINES);
		gl.glVertex3f(-0.50f, -0.50f, 0);
		gl.glVertex3f(0.50f, -0.50f, 0);
		gl.glEnd();
		
		//drawing the right edge
		gl.glBegin (GL2.GL_LINES);
		gl.glVertex3f(0f, 0.50f, 0);
		gl.glVertex3f(-0.50f, -0.50f, 0);
		gl.glEnd();
		
		//drawing the lft edge
		gl.glBegin (GL2.GL_LINES);
		gl.glVertex3f(0f, 0.50f, 0);
		gl.glVertex3f(0.50f, -0.50f, 0);
		gl.glEnd();
		gl.glFlush();
		glAutoDrawable.swapBuffers();
	}
	
	@Override
	public void dispose(GLAutoDrawable glAutoDrawable) {
	
	}
	
	@Override
	public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {
	
	}
}
