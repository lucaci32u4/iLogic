package com.lucaci32u4.ljwglExample;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class SwingFrame_LWJGL {

	private static boolean setSystemLookAndFeel() {
		boolean[] success = { true, true, true, true };
		try {
			System.setProperty("sun.java2d.noddraw", "true");
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (ClassNotFoundException e) { success[0] = false; }
		catch (InstantiationException e) { success[1] = false; }
		catch (IllegalAccessException e) { success[2] = false; }
		catch (UnsupportedLookAndFeelException e) { success[3] = false; }
		return success[0] && success[1] && success[2] && success[3];
	}

	public static void main(String[] args) {
		setSystemLookAndFeel();
		EventQueue.invokeLater(new Runnable() {
			
			public void run() {
				try {
					SwingFrame_LWJGL window = new SwingFrame_LWJGL();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private volatile float[] vertices;
	private JFrame frame;
	private Canvas canvas;
	private Thread gameThread;
	private boolean running;
	private volatile boolean needValidation;
	private volatile boolean needUpdateViewport;
	
	public SwingFrame_LWJGL() {
		frame = new JFrame();
		frame.addWindowListener(new WindowListener() {
			public void windowOpened(WindowEvent arg0) {
			}
			public void windowIconified(WindowEvent arg0) {
			}
			public void windowDeiconified(WindowEvent arg0) {
			}
			public void windowDeactivated(WindowEvent arg0) {
			}
			public void windowClosing(WindowEvent arg0) {
			}
			public void windowClosed(WindowEvent arg0) {
			}
			public void windowActivated(WindowEvent arg0) {
			}
		});
		frame.setTitle("Swing + LWJGL");
		frame.setBounds(100, 100, 1024, 768);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		needValidation = true;
		MenuListener menuListener = new MenuListener() {
			public void menuSelected(MenuEvent e) {
				if (needValidation) {
					needValidation = false;
					frame.validate();
					System.out.println("Validate");
				}
			}
			public void menuDeselected(MenuEvent e) {
			}
			public void menuCanceled(MenuEvent e) {
			}
		};
		
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPane = new JSplitPane();
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);
		
		JPanel canvasPanel = new JPanel();
		canvasPanel.setLayout(new BorderLayout(0, 0));
		splitPane.setRightComponent(canvasPanel);
		
		canvas = new Canvas() {
			private static final long serialVersionUID = -1069002023468669595L;
			public void removeNotify() {
				stopOpenGL();
			}
		};
		canvas.addComponentListener(new ComponentListener() {
			public void componentShown(ComponentEvent e) {
				setNeedValidation();
			}
			public void componentResized(ComponentEvent e) {
				setNeedValidation();
			}
			public void componentMoved(ComponentEvent e) {
				setNeedValidation();
			}
			public void componentHidden(ComponentEvent e) {
				setNeedValidation();
			}
		});
		canvas.setIgnoreRepaint(true);
		canvas.setPreferredSize(new Dimension(800, 600));
		canvas.setMinimumSize(new Dimension(320, 240));
		canvas.setVisible(true);
		canvasPanel.add(canvas, BorderLayout.CENTER);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setMinimumSize(new Dimension(160, 160));
		splitPane.setLeftComponent(scrollPane_1);
		
		JTree tree = new JTree();
		scrollPane_1.setViewportView(tree);
		
		startOpenGL();
	}
	
	private void setNeedValidation() {
		needValidation = true;
		needUpdateViewport = true;
	}
	
	private void startOpenGL() {
		System.out.println("StartOpenGL");
		
		gameThread = new Thread() {
			public void run() {
				try {
					Display.create();
					Display.setParent(canvas);
					
					Rectangle rect = canvas.getBounds();
					int w = (int) rect.getWidth();
					int h = (int) rect.getHeight();
					
					System.out.println("Display Rectangle: "+rect);
					
					GL11.glClearColor(0.1f, 0.1f, 0.3f, 1);
					
					GL11.glMatrixMode(GL11.GL_PROJECTION);
					GL11.glLoadIdentity();
					GL11.glOrtho(0, w, h, 0, -1, 1);
					
					GL11.glViewport(0, 0, w, h);
					
					setupVertices();
					
					running = true;
				} catch (LWJGLException e) {
					e.printStackTrace();
				}
				while (running) {
					updateGL();
				}
				if (Display.isCreated()) {
					Display.destroy();
				}
			}
		};
		gameThread.start();
	}
	
	private void setupVertices() {
		vertices = new float[4 * 2];
		
		vertices[0] = 0.1f;
		vertices[1] = 0.3f;
		
		vertices[2] = 0.2f;
		vertices[3] = 0.8f;
		
		vertices[4] = 0.9f;
		vertices[5] = 0.6f;
		
		vertices[6] = 0.7f;
		vertices[7] = 0.05f;
	}
	
	private void updateGL() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		
		render();
		
		Display.update();
		Display.sync(60);
		
		if (needUpdateViewport) {
			needUpdateViewport = false;
			
			Rectangle rect = canvas.getBounds();
			int w = (int) rect.getWidth();
			int h = (int) rect.getHeight();
			
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glOrtho(0, w, h, 0, -1, 1);
			GL11.glViewport(0, 0, w, h);
		}
		
		int error = GL11.glGetError();
		if (error != GL11.GL_NO_ERROR) {
			String msg = "Unknown Error";
			switch (error) {
				case GL11.GL_INVALID_OPERATION:
					msg = "Invalid Operation"; break;
				case GL11.GL_INVALID_VALUE:
					msg = "Invalid Value"; break;
				case GL11.GL_INVALID_ENUM:
					msg = "Invalid Enum"; break;
				case GL11.GL_STACK_OVERFLOW:
					msg = "Stack Overflow"; break;
				case GL11.GL_STACK_UNDERFLOW:
					msg = "Stack Underflow"; break;
				case GL11.GL_OUT_OF_MEMORY:
					msg = "Out of memory"; break;
			}
			throw new RuntimeException(msg);
		}
	}
	
	private void render() {
		float scale = 100;
		
		GL11.glBegin(GL11.GL_QUADS);
		
		GL11.glColor4f(1, 0, 0, 1);
		GL11.glVertex3f(vertices[0] * scale, vertices[1] * scale, 0);
		
		GL11.glColor4f(1, 0, 0, 1);
		GL11.glVertex3f(vertices[2] * scale, vertices[3] * scale, 0);
		
		GL11.glColor4f(1, 0, 0, 1);
		GL11.glVertex3f(vertices[4] * scale, vertices[5] * scale, 0);
		
		GL11.glColor4f(1, 0, 0, 1);
		GL11.glVertex3f(vertices[6] * scale, vertices[7] * scale, 0);
		
		GL11.glEnd();
	}
	
	private void stopOpenGL() {
		System.out.println("StopOpenGL");
		
		running = false;
		try {
			gameThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
