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

package com.lucaci32u4.UI.Windows;

import com.lucaci32u4.util.Helper;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class PagedTreeWindow {
	
	private JFrame frame = new JFrame();
	private JSplitPane split = new JSplitPane();
	private JScrollPane treeScroll = new JScrollPane();
	private JScrollPane viewScroll = new JScrollPane();
	private JEditorPane contentPane = new JEditorPane();
	private DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("tree_root");
	private JTree contentTree;
	
	private HashMap<DefaultMutableTreeNode, Icon> icon;
	private HashMap<DefaultMutableTreeNode, String> content;
	private String title;
	private Collection<DefaultMutableTreeNode> initRoots;
	
	private PagedTreeWindow(@NotNull Collection<DefaultMutableTreeNode> roots, @NotNull HashMap<DefaultMutableTreeNode, Icon> mapIcon, @NotNull HashMap<DefaultMutableTreeNode, String> mapContent, @NotNull String title) {
		icon = mapIcon;
		content = mapContent;
		this.title = title;
		initRoots = roots;
	}
	
	private void initSwing() {
		for (DefaultMutableTreeNode root : initRoots) {
			rootNode.add(root);
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
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
			Icon nodeIcon = icon.get(node);
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
	
	private static AtomicInteger atomicWorkerThreadCounter = new AtomicInteger(0);
	public static PagedTreeWindow invokeNew(@NotNull String[] treePaths, @NotNull String[] resourceHtml, @NotNull Icon[] icons, String title) {
		PagedTreeWindow window = null;
		int size = treePaths.length;
		if (size == resourceHtml.length && size == icons.length) {
			ArrayList<DefaultMutableTreeNode> root = new ArrayList<>();
			HashMap<DefaultMutableTreeNode, Icon> icon = new HashMap<>();
			HashMap<DefaultMutableTreeNode, String> content = new HashMap<>();
			window = new PagedTreeWindow(root, icon, content, title);
			Thread worker = new Thread(new Builder(treePaths, resourceHtml, icons, root, icon, content, window));
			worker.setName("PagedTreeWindowContentWorker-" + Integer.toString(atomicWorkerThreadCounter.getAndIncrement()));
			worker.start();
		}
		return window;
	}
	
	private static class Builder implements Runnable {
		ArrayList<DefaultMutableTreeNode> root;
		HashMap<DefaultMutableTreeNode, Icon> icon;
		HashMap<DefaultMutableTreeNode, String> content;
		String[] resourceHtml;
		String[] treePaths;
		Icon[] icons;
		PagedTreeWindow window;
		Builder(@NotNull String[] treePaths, @NotNull String[] resourceHtml, @NotNull Icon[] icons,
				@NotNull ArrayList<DefaultMutableTreeNode> root,
				@NotNull HashMap<DefaultMutableTreeNode, Icon> icon,
				@NotNull HashMap<DefaultMutableTreeNode, String> content,
				@NotNull PagedTreeWindow window
		) {
			this.treePaths = treePaths;
			this.resourceHtml = resourceHtml;
			this.icons = icons;
			this.root = root;
			this.content = content;
			this.icon = icon;
			this.window = window;
		}
		
		private String traceFullQualifiedName(@NotNull ArrayList<String[]> path, int i, int depth) {
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j <= depth; j++) {
				sb.append('\\').append(path.get(i)[j]);
			}
			return sb.toString();
		}
		
		@Override public void run() {
			ArrayList<String[]> path = new ArrayList<>();
			HashMap<String, DefaultMutableTreeNode> thisLevel = new HashMap<>();
			HashMap<String, DefaultMutableTreeNode> lastLevel = new HashMap<>();
			int size = treePaths.length;
			int initialSize = size;
			int maxDepth = 0;
			for (int i = 0; i < size; i++) {
				path.add(treePaths[i].split("\\\\"));
				if (maxDepth < path.get(i).length) maxDepth = path.get(i).length;
			}
			for (int i = 0; i < size; i++) {
				int parentDepth = path.get(i).length - 2;
				if (parentDepth >= 0) {
					String parentName = traceFullQualifiedName(path, i, parentDepth);
					boolean createParent = true;
					for (int j = 0; j < size && createParent; j++) {
						if (path.get(j).length == parentDepth + 1) if (traceFullQualifiedName(path, j, parentDepth).equals(parentName)) createParent = false;
					}
					if (createParent) {
						String[] parentPath = new String[parentDepth + 1];
						System.arraycopy(path.get(i), 0, parentPath, 0, parentDepth + 1);
						path.add(parentPath);
						size++;
					}
				}
			}
			for (int depth = 0; depth < maxDepth; depth++) {
				HashMap<String ,DefaultMutableTreeNode> aux = lastLevel;
				lastLevel = thisLevel;
				thisLevel = aux;
				aux.clear();
				for (int i = 0; i < size; i++) {
					if (path.get(i).length == depth + 1) { // This is the file definition
						DefaultMutableTreeNode node = new DefaultMutableTreeNode(" " + path.get(i)[depth] + " ");
						thisLevel.put(traceFullQualifiedName(path, i, depth), node); // Add to file definition pointer map
						icon.put(node, i < initialSize ? icons[i] : null);
						content.put(node, i < initialSize ? resourceHtml[i] : null);
						if (depth == 0) { // If root
							root.add(node); // Add to roots
						} else {
							lastLevel.get(traceFullQualifiedName(path, i, depth - 1)).add(node); // Add to parent node
						}
					}
				}
			}
			HashMap<DefaultMutableTreeNode, String> decodedContent = new HashMap<>();
			for (DefaultMutableTreeNode key : content.keySet()) {
				String contentLocation = content.get(key);
				String contentText = "";
				if (contentLocation != null) {
					InputStream contentStream = getClass().getResourceAsStream(contentLocation);
					if (contentStream != null) {
						contentText = Helper.freadText(contentStream);
					}
				}
				decodedContent.put(key, contentText);
			}
			content.clear();
			content.putAll(decodedContent);
			decodedContent.clear();
			thisLevel.clear();
			lastLevel.clear();
			path.clear();
			SwingUtilities.invokeLater(window::initSwing);
			root = null;
			icon = null;
			content = null;
			window = null;
			icons = null;
			treePaths = null;
			resourceHtml = null;
		}
	}
}

