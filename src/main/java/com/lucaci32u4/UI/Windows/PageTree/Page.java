package com.lucaci32u4.UI.Windows.PageTree;

import lombok.Getter;
import lombok.Setter;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Page {
	
	static class PageSet {
		DefaultMutableTreeNode root = null;
		HashMap<DefaultMutableTreeNode, Icon> icon = new HashMap<>();
		HashMap<DefaultMutableTreeNode, String> content = new HashMap<>();
	}
	
	@Getter private Icon icon = null;
	@Getter private String name = null;
	@Getter private String content = null;
	@Getter private Collection<Page> children = new ArrayList<>();
	
	public Page add(Page page) {
		children.add(page);
		return this;
	}
	
	public Page remove(Page page) {
		children.remove(page);
		return this;
	}
	
	public Page setIcon(Icon icon) {
		this.icon = icon;
		return this;
	}
	
	public Page setName(String name) {
		this.name = name;
		return this;
	}
	
	public Page setContent(String content) {
		this.content = content;
		return this;
	}
	
	private PageSet recursiveAdder(PageSet set) {
		DefaultMutableTreeNode root = set.root;
		set.root = new DefaultMutableTreeNode(name);
		set.icon.put(set.root, icon);
		set.content.put(set.root, content);
		for (Page page : children) {
			page.recursiveAdder(set);
		}
		if (root != null) {
			root.add(set.root);
			set.root = root;
		}
		return set;
	}
	
	static PageSet createPageSet(Page root) {
		return root.recursiveAdder(new PageSet());
	}
}
