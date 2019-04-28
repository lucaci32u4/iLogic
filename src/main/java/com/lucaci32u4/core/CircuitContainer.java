package com.lucaci32u4.core;

import com.lucaci32u4.util.Vector2;

import java.util.ArrayList;

public class CircuitContainer {
	ArrayList<CircuitNode> nodes;
	ArrayList<CircuitLink> links;
	/**
	 * Add a new node/component to the circuit
	 * @param node the node
	 */
	public void addCircuitNode(CircuitNode node) {
		nodes.add(node);
	}

	/**
	 * Add a new link according to link's coordinates
	 * @param link the link
	 */
	public void addCircuitLink(CircuitLink link) {
		if (!CircuitLink.isValid(link)) {
			throw new IllegalArgumentException();
		}
		Vector2 pos1 = link.getPos1(), pos2 = link.getPos2();
		boolean vertical = (pos1.x == pos2.x);
		CircuitLink[] subdividedLink = new CircuitLink[2];
		ArrayList<CircuitLink> concurrentLink = new ArrayList<>();
		for (CircuitLink tl : links) {
			Vector2 tl1 = tl.getPos1(), tl2 = tl.getPos2();
			boolean probeVertical = (tl1.x == tl2.x);
			if (probeVertical ^ vertical) {
				// subdivide
				// TODO: detect subdivides
			} else {
				// concurrent
				if (vertical) {
					if (tl1.x == pos1.x && Math.min(tl1.y, tl2.y) <= pos1.y && pos1.y <= Math.max(tl1.y, tl2.y) ||
						tl1.x == pos1.x && Math.min(tl1.y, tl2.y) <= pos2.y && pos2.y <= Math.max(tl1.y, tl2.y)) {
						if (!concurrentLink.contains(tl)) concurrentLink.add(tl);
					}
				} else {
					if (tl1.y == pos1.y && Math.min(tl1.x, tl2.x) <= pos1.x && pos1.x <= Math.max(tl1.x, tl2.x) ||
						tl1.y == pos1.y && Math.min(tl1.x, tl2.x) <= pos2.x && pos2.x <= Math.max(tl1.x, tl2.x)) {
						if (!concurrentLink.contains(tl)) concurrentLink.add(tl);
					}
				}
			}
		}
		// TODO: process concurrences and subdivides
		links.add(link);
	}

	/**
	 * Remove a node
	 * @param node the node
	 */
	public void removeCircuitNode(CircuitNode node) {

	}

	/**
	 * Remove a link
	 * @param link the link
	 */
	public void removeCircuitLink(CircuitLink link) {

	}

	/**
	 * Gets all links in the circuit
	 * @return Iterable object that contains all links
	 */
	public Iterable<CircuitLink> getAllLinks() {
		return null;
	}

	/**
	 * Gets all nodes in the circuit
	 * @return Iterable object that contains all nodes
	 */
	public Iterable<CircuitNode> getAllNodes() {
		return null;
	}


	private void subdivideMatchingLink(Vector2 pos) {
		// TODO: subdivide the segment that pos is inside

	}
}
