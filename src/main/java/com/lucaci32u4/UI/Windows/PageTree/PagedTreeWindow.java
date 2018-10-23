package com.lucaci32u4.UI.Windows.PageTree;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.util.HashMap;

public class PagedTreeWindow {
	
	private JFrame frame = new JFrame();
	private JSplitPane split = new JSplitPane();
	private JScrollPane treeScroll = new JScrollPane();
	private JScrollPane viewScroll = new JScrollPane();
	private JEditorPane contentPane = new JEditorPane();
	private DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("tree_root");
	private JTree contentTree;
	
	private HashMap<Object, Icon> icon = new HashMap<>();
	private HashMap<Object, String> content = new HashMap<>();
	
	public PagedTreeWindow(@NotNull Page[] roots, @NotNull String title) {
		Page.PageSet[] set = new Page.PageSet[roots.length];
		for (int i = 0; i < roots.length; i++) {
			set[i] = Page.createPageSet(roots[i]);
			rootNode.add(set[i].root);
			icon.putAll(set[i].icon);
			content.putAll(set[i].content);
		}
		contentTree = new JTree(rootNode);
		contentTree.setRootVisible(false);
		contentTree.setShowsRootHandles(true);
		contentTree.setExpandsSelectedPaths(true);
		contentTree.setSelectionModel(new DefaultTreeSelectionModel());
		contentTree.addTreeSelectionListener(new MyTreeSelectionListener());
		contentTree.setCellRenderer(new MyTreeCellRenderer());
		contentTree.expandRow(0);
		treeScroll.setLayout(new ScrollPaneLayout());
		treeScroll.setViewportView(contentTree);
		viewScroll.setLayout(new ScrollPaneLayout());
		viewScroll.setViewportView(contentPane);
		split.setLeftComponent(treeScroll);
		split.setRightComponent(viewScroll);
		frame.getContentPane().add(split);
		frame.setTitle(title);
		frame.setPreferredSize(new Dimension(400, 600));
		frame.pack();
	}
	
	public void setVisible(boolean visible) {
		if (frame != null) frame.setVisible(visible);
	}
	
	public void destroy() {
		if (frame != null) {
			frame.setVisible(false);
			frame.dispose();
		}
		frame = null;
		split = null;
		treeScroll = null;
		viewScroll = null;
		contentPane = null;
		rootNode = null;
		contentTree = null;
		icon = null;
		content = null;
	}
	
	private class MyTreeCellRenderer extends DefaultTreeCellRenderer implements TreeCellRenderer {
		private Icon closed = UIManager.getIcon("Tree.closedIcon");
		private Icon open = UIManager.getIcon("Tree.openIcon");
		private Icon leaf = UIManager.getIcon("Tree.leafIcon");
		@Override public Component getTreeCellRendererComponent(JTree tree, Object value, boolean bSelected, boolean bExpanded, boolean bLeaf, int iRow, boolean bHasFocus) {
			Icon nodeIcon = icon.get(value);
			nodeIcon = (nodeIcon != null ? nodeIcon : (bLeaf ? leaf : (bExpanded ? open : closed)));
			setIcon(nodeIcon);
			setBackgroundSelectionColor(Color.LIGHT_GRAY);
			setBorderSelectionColor(Color.BLACK);
			setTextSelectionColor(Color.BLACK);
			return super.getTreeCellRendererComponent(tree, value, bSelected, bExpanded, bLeaf, iRow, bHasFocus);
		}
	}
	
	private class MyTreeSelectionListener implements TreeSelectionListener {
		@Override public void valueChanged(TreeSelectionEvent e) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)contentTree.getLastSelectedPathComponent();
			if (node != null) {
				contentPane.setText(content.get(node));
			}
		}
	}
}
