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

import com.lucaci32u4.main.ApplicationSettings;
import com.lucaci32u4.main.LanguagePack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SettingsWindow {
	
	public interface ApplyListener {
		void apply(ApplicationSettings settings, boolean closing);
	}
	
	// Language
	private LanguagePack lang;
	
	// Swing objects
	private JFrame window;
	private JTabbedPane tabs;
	private JPanel buttonPanel;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JButton buttonApply;
	
	// Swing tabs
	private JPanel languageTab;
	private JPanel graphicsTab;
	
	// Language tab
	private JComboBox<String> languageDropdown;
	
	// Graphics tab
	private JPanel rendererPanel;
	private JLabel rendererLabel;
	private JComboBox<String> rendererDropdown;
	private JPanel aaPanel;
	private JLabel aaLabel;
	private JComboBox<String> aaDropdown;
	
	// Listener
	private ApplyListener listener;
	
	// Current settings
	private ApplicationSettings current;
	
	// Button listener
	MainButtonListener buttonListener;
	
	public void create(LanguagePack languagePack, ApplyListener apply, ApplicationSettings current) {
		lang = languagePack;
		listener = apply;
		this.current = current;
		window = new JFrame();
		tabs = new JTabbedPane();
		buttonOK = new JButton();
		buttonApply = new JButton();
		buttonCancel = new JButton();
		buttonPanel = new JPanel();
		languageTab = new JPanel();
		graphicsTab = new JPanel();
		languageDropdown = new JComboBox<>();
		rendererPanel = new JPanel();
		rendererLabel = new JLabel();
		rendererDropdown = new JComboBox<>();
		aaPanel = new JPanel();
		aaLabel = new JLabel();
		aaDropdown = new JComboBox<>();
		buttonListener = new MainButtonListener();
		SwingUtilities.invokeLater(this::onCreate);
	}
	
	void destroy() {
	
	}
	
	private void onCreate() {
		window.getContentPane().setLayout(new BorderLayout());
		window.getContentPane().add(tabs, BorderLayout.CENTER);
		window.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		buttonPanel.add(buttonOK);
		buttonPanel.add(buttonApply);
		buttonPanel.add(buttonCancel);
		buttonOK.setText(lang.get("OK"));
		buttonApply.setText(lang.get("apply"));
		buttonCancel.setText(lang.get("cancel"));
		buttonOK.addActionListener(buttonListener);
		buttonApply.addActionListener(buttonListener);
		buttonCancel.addActionListener(buttonListener);
		tabs.addTab(lang.get("language"), languageTab);
		tabs.addTab(lang.get("graphics"), graphicsTab);
		languageTab.setLayout(new FlowLayout());
		languageTab.add(languageDropdown);
		for (String val : ApplicationSettings.LANGUAGE_OPTIONS) languageDropdown.addItem(val);
		languageDropdown.setSelectedIndex(current.language);
		graphicsTab.setLayout(new BoxLayout(graphicsTab, BoxLayout.PAGE_AXIS));
		graphicsTab.add(rendererPanel);
		rendererPanel.add(rendererLabel);
		rendererPanel.add(rendererDropdown);
		for (String val : ApplicationSettings.RENDERER_OPTIONS) rendererDropdown.addItem(lang.get(val));
		rendererDropdown.setSelectedIndex(current.renderMethod);
		graphicsTab.add(aaPanel);
		aaPanel.add(aaLabel);
		aaPanel.add(aaDropdown);
		for (String val : ApplicationSettings.ANTIALIASING_OPTIONS) aaDropdown.addItem(lang.get(val));
		aaDropdown.setSelectedIndex(current.antialiasing);
		rendererLabel.setText(lang.get("graphicsaccel"));
		aaLabel.setText(lang.get("antialiasing"));
		window.setTitle(lang.get("settings"));
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}
	
	private class MainButtonListener implements ActionListener {
		@Override public void actionPerformed(ActionEvent e) {
			boolean closing = e.getSource() == buttonCancel || e.getSource() == buttonOK;
			boolean saving = e.getSource() == buttonApply || e.getSource() == buttonOK;
			if (saving) {
				ApplicationSettings newState = new ApplicationSettings();
				newState.antialiasing = aaDropdown.getSelectedIndex();
				newState.renderMethod = rendererDropdown.getSelectedIndex();
				newState.language = languageDropdown.getSelectedIndex();
				if (listener != null) listener.apply(newState, closing);
			}
			if (closing) {
				destroy();
			}
		}
	}
}
