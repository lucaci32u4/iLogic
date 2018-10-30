package com.lucaci32u4.UI.Windows;

import com.lucaci32u4.IO.IOInterface;
import com.lucaci32u4.main.ApplicationConstants;
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
		pane.setFont(IOInterface.getInstance().loadResourceFont(ApplicationConstants.getInstance().get("resource.font.ocra")).deriveFont(16.0f));
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
