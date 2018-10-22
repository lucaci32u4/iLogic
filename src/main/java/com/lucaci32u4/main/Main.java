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

package com.lucaci32u4.main;

import com.lucaci32u4.UI.Windows.MainWindow;

import com.lucaci32u4.UI.Windows.SettingsWindow;
import com.lucaci32u4.util.Helper;

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
	
	
	
	private class UserEvent implements MainWindow.UserInputListener {
		@Override public void onUserEvent(Type subject, int param1, String param2) {
		
		}
	}
	
	private Main() {
		LanguagePack.getInstance().begin(getClass().getResourceAsStream("/Translations/english.txt"));
		String aboutText = Helper.freadText(getClass().getResourceAsStream("/about.html"));
		MainWindow window = new MainWindow(new UserEvent(), aboutText);
		window.setVisible(true);
		boolean run = true;
		SettingsWindow s = new SettingsWindow();
		
	}
}
