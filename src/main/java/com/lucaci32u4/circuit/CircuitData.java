package com.lucaci32u4.circuit;

import java.util.ArrayList;

public class CircuitData {

	private final ArrayList<CircuitLink> links = new ArrayList<>();
	private final ArrayList<CircuitComponent> components = new ArrayList<>();

	public Iterable<CircuitLink> iterLinks() {
		return links;
	}

	public Iterable<CircuitComponent> iterComponents() {
		return components;
	}

	public int countLinks() {
		return links.size();
	}

	public int countComponents() {
		return components.size();
	}

	public boolean contains(CircuitLink link) {
		return links.contains(link);
	}

	public boolean contains(CircuitComponent component) {
		return components.contains(component);
	}

	public void clearLinks() {
		links.clear();
	}

	public void clearComponents() {
		components.clear();
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

	public void addComponent(CircuitComponent component) {
		components.add(component);
	}

	public boolean removeComponent(CircuitComponent component) {
		return components.remove(component);
	}
}
