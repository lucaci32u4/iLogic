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

package com.lucaci32u4.ui.windows;

import com.lucaci32u4.io.IOInterface;
import com.lucaci32u4.main.Const;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;

public class AboutWindow {
	
	private JFrame frame = new JFrame("About");
	private JEditorPane pane = new JEditorPane();
	AboutWindow() {
		pane.setEditorKit(new HTMLEditorKit());
		pane.setEditable(false);
		pane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
		pane.setFont(IOInterface.getInstance().loadResourceFont(Const.getInstance().get("resource.font.ocra")).deriveFont(16.0f));
		frame.setContentPane(pane);
		frame.setPreferredSize(new Dimension(250, 500));
		frame.pack();
	}
	
	public void setVisible(boolean visible) {
		frame.setVisible(visible);
	}
	
	public void setContent(@NotNull String htmlText) {
		pane.setText(htmlText);
	}
	
	public void destroy() {
		this.setVisible(false);
		frame.dispose();
		frame = null;
		pane = null;
	}
}
