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

import javax.swing.filechooser.FileSystemView;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.util.Properties;

public class Const {
	private static Const ourInstance = new Const();
	public static Const getInstance() {
		return ourInstance;
	}
	
	private Properties prop = new Properties();
	
	private Const() {
		try {
			InputStream inputStream = getClass().getResourceAsStream("/Constants.properties");
			if (inputStream != null) {
				prop.load(inputStream);
				initProgrammaticConstants();
			} else throw new IOException("Resource Constants.properties was not found!");
		} catch (IOException e) {
			System.err.println("[ERROR at com.lucaci32u4.main.Const constructor]: Cannot open Constants.properties resource! Exception stack trace:");
			System.out.println("[ERROR at com.lucaci32u4.main.Const constructor]: Cannot open Constants.properties resource! Exception stack trace:");
			e.printStackTrace(System.err);
			e.printStackTrace(System.out);
		}
	}
	
	private void initProgrammaticConstants() {
		String separator = FileSystems.getDefault().getSeparator();
		StringBuilder workspace = new StringBuilder();
		if (System.getProperty("os.name").startsWith("Windows")) {
			workspace.append(FileSystemView.getFileSystemView().getDefaultDirectory().getPath());
		} else {
			workspace.append(System.getProperty("user.home"));
		}
		if (!workspace.toString().endsWith(separator)) workspace.append(separator);
		workspace.append(get("workspace.name"));
		prop.put("file.separator", separator);
		prop.put("workspace.path", workspace);
	}
	
	public String get(String key) {
		String result = prop.getProperty(key);
		return (result != null ? result : "");
	}
	
	public static String query(String key) {
		return getInstance().get(key);
	}
}
