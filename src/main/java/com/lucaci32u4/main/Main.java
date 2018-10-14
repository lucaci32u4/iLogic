package com.lucaci32u4.main;

import com.lucaci32u4.UI.MainWindow;
import com.lucaci32u4.UI.Viewport.LogicViewport;
import com.lucaci32u4.UI.Viewport.Renderer.DrawAPI;
import com.lucaci32u4.UI.Viewport.Renderer.ResourceAPI;
import com.lucaci32u4.UI.Viewport.Renderer.VisualArtifact;
import com.lucaci32u4.util.Helper;
import com.lucaci32u4.util.SimpleEventQueue;
import org.jetbrains.annotations.NotNull;

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
		LanguagePack.getInstance().begin(Helper.fread("Resources\\Translations\\english.txt"));
		SimpleEventQueue<MainWindow.Event> queue = new SimpleEventQueue<>();
		MainWindow window = new MainWindow(queue);
		window.setCircuitViewport(new LogicViewport());
		window.getCircuitViewport().getRenderManager().attach(new VisualArtifact() {
			@Override
			public boolean checkIfOnScreen(int screenLeft, int screenTop, int screenWidth, int screenHeight) {
				return true;
			}

			@Override
			public void onAttach(@NotNull ResourceAPI resourceAPI) {
				int x = 0;
			}

			@Override
			public void onDraw(@NotNull DrawAPI pen, @NotNull ResourceAPI resourceAPI) {
				pen.drawLine(0, 0, 100, 100, 3.0f);
			}

			@Override
			public void onDetach(@NotNull ResourceAPI resourceAPI) {

			}
		});
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
