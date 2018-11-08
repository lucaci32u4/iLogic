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

import com.lucaci32u4.IO.IOInterface;
import com.lucaci32u4.UI.Windows.MainWindow;

import com.lucaci32u4.UI.Windows.SettingsWindow;

import javax.swing.*;

public class Main {
	private static void setSystemLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		setSystemLookAndFeel();
		Main application = new Main();
		application.init(args);
		application.run(args);
	}
	
	
	
	private class UserEventListener implements MainWindow.UserInputListener {
		@Override public void onUserEvent(Type subject, int param1, String param2) {
			switch (subject) {
				case COPY:
			}
		}
	}
	
	private Main() {
		MainWindow window = new MainWindow(new UserEventListener());
		window.setVisible(true);
		SettingsWindow s = new SettingsWindow();
	}
	
	private void init(String [] args) {
		IOInterface.getInstance().init(System.err, Const.query("workspace.path"));
		LanguagePack.getInstance().init(System.err, IOInterface.getInstance().loadResourceString(Const.query("resource.language.english")));
	}
	
	private void run(String[] args) {
	
	}
}
