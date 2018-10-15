package com.lucaci32u4.main;

import com.lucaci32u4.UI.MainWindow;

import com.lucaci32u4.UI.SettingsWindow;
import com.lucaci32u4.util.SimpleEventQueue;

import javax.swing.*;

public class Main {
	private static boolean setSystemLookAndFeel() {
		boolean[] success = { true, true, true, true };
		try {
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
		Main m = new Main();
	}
	private Main() {
		LanguagePack.getInstance().begin(getClass().getResourceAsStream("/Translations/english.txt"));
		SimpleEventQueue<MainWindow.Event> queue = new SimpleEventQueue<>();
		MainWindow window = new MainWindow(queue);
		window.setVisible(true);
		boolean run = true;
		SettingsWindow s = new SettingsWindow();
		while (run) {
			MainWindow.Event e = queue.consume(true);
			System.out.println(e.subject);
			if (e.subject == MainWindow.Event.Type.EXIT) {
				if (window.showExitPopup() != MainWindow.EXIT_CANCEL) {
					run = false;
					window.close();
				}
			}
			if (e.subject == MainWindow.Event.Type.SETTINGS) {
				s.create(LanguagePack.getInstance(), null, new ApplicationSettings());
			}
		}
	}
}
