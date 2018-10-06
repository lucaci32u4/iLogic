package com.lucaci32u4.main;

import com.lucaci32u4.UI.LogicViewport;
import com.lucaci32u4.UI.MainWindow;
import com.lucaci32u4.util.Helper;
import com.lucaci32u4.util.SimpleEventQueue;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.*;

public class Main {
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
		//LanguagePack.getInstance().begin(Helper.fread("C:/javaspace/pr.txt"));
		SimpleEventQueue<MainWindow.Event> queue = new SimpleEventQueue<>();
		MainWindow window = new MainWindow(queue);
		window.setVisible(true);
		//gl(window);
		boolean run = true;
		while (run) {
			MainWindow.Event e = queue.consume(true);
			System.out.println(e.subject);
			if (e.subject == MainWindow.Event.Type.EXIT) {
				if (window.showExitPopup() != MainWindow.EXIT_CANCEL) {
					run = false;
					window.close();
				}
			}
		}
	}
	public static void gl(MainWindow win) {

	}
}
