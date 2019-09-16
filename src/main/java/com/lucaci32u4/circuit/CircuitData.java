package com.lucaci32u4.circuit;

import java.util.ArrayList;

public class CircuitData {

	private final ArrayList<CircuitLink> links = new ArrayList<>();
	private final ArrayList<CircuitNode> nodes = new ArrayList<>();

	public Iterable<CircuitLink> iterLinks() {
		return links;
	}

	public Iterable<CircuitNode> iterNodes() {
		return nodes;
	}

	public int countLinks() {
		return links.size();
	}

	public int countNodes() {
		return nodes.size();
	}

	public boolean contains(CircuitLink link) {
		return links.contains(link);
	}

	public boolean contains(CircuitNode node) {
		return nodes.contains(node);
	}

	public void clearLinks() {
		links.clear();
	}

	public void clearNodes() {
		nodes.clear();
	}

	public void addLink(CircuitLink link) {
		links.add(link);
	}

	public void addLinks(Iterable<CircuitLink> links) {
		links.forEach(this.links::add);
	}

	public boolean removeLink(CircuitLink link) {
		return links.remove(link);
	}

	public void removeLinks(Iterable<CircuitLink> links) {
		links.forEach(this.links::remove);
	}

	public void addNode(CircuitNode node) {
		nodes.add(node);
	}

	public boolean removeNode(CircuitNode node) {
		return nodes.remove(node);
	}
}
