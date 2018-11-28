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

import com.lucaci32u4.io.IOInterface;
import com.lucaci32u4.model.ModelContainer;
import com.lucaci32u4.presentation.PresentationContainer;
import com.lucaci32u4.ui.windows.MainWindow;


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
	
	private PresentationContainer presentation = null;
	private ModelContainer model = null;
	
	
	
	private void init(String [] args) {
		IOInterface.getInstance().init(System.err, Const.query("workspace.path"));
		LanguagePack.getInstance().init(System.err, IOInterface.getInstance().loadResourceString(Const.query("resource.language.english")));
	}
	
	private void run(String[] args) {
		model = new ModelContainer();
		presentation = new PresentationContainer();
		model.addViewController(presentation);
	}
}


/*

5+ 1) 1. vita de vie
2+ 2) 2. paie
6+ 3) 3. fructe de mare
1+ 4) 4. apa
3+ 5) 5. Almanahe
4+ 6) 6. 0venezuela




 */