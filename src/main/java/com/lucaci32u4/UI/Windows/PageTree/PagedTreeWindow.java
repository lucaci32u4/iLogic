package com.lucaci32u4.UI.Windows.PageTree;

import javax.swing.*;

public class PagedTreeWindow {
	
	private JFrame frame = new JFrame();
	private JSplitPane split = new JSplitPane();
	private JScrollPane treeScroll = new JScrollPane();
	private JScrollPane viewScroll = new JScrollPane();
	private JEditorPane content = new JEditorPane();
	private JTree contentTree = new JTree();
	
	private Page.PageSet set;
	
	PagedTreeWindow(Page root) {
		set = Page.createPageSet(root);
		
	}
	
	void setVisible() {
	
	}
	
}
