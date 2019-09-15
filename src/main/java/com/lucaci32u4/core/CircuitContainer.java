package com.lucaci32u4.core;


import com.lucaci32u4.util.Rotation;

import java.util.ArrayList;
import java.util.Comparator;

public class CircuitContainer {
	private CircuitData data = new CircuitData();

	/**
	 * Add a new node to the circuit
	 * @param node the node
	 */
	public void addCircuitNode(CircuitNode node, Rotation rot, int x, int y) {
		data.addNode(node);
		node.setPosition(x, y);
		node.setRotation(rot);
		for (CircuitPin pin : node.getPins()) {
			subdivideAtPoint(pin.worldX, pin.worldY, false);
			subdivideAtPoint(pin.worldY, pin.worldX, true);
		}
	}

	/**
	 * Remove a node
	 * @param node the node
	 */
	public void removeCircuitNode(CircuitNode node) {
		boolean found = data.removeNode(node);
		if (found) {
			for (CircuitPin pin : node.getPins()) {
				mergeAtPoint(pin.worldX, pin.worldY, false);
				mergeAtPoint(pin.worldY, pin.worldX, true);
			}
		}
	}

	/**
	 * Add a new link according to link's coordinates
	 * @param link the link
	 */
	public void addCircuitLink(CircuitLink link) {
		link = mergeConcurrentSegments(link);
		subdivideAtPoint(link.getP1(), link.getLineColumn(), link.isVertical());
		subdivideAtPoint(link.getP2(), link.getLineColumn(), link.isVertical());
		ArrayList<CircuitLink> pieces = subdivideSegmentAtIntersections(link);
		data.addLinks(pieces);
	}

	/**
	 * Remove a link
	 * @param link the link
	 */
	public void removeCircuitLink(CircuitLink link) {
		boolean found = data.removeLink(link);
		if (found) {
			mergeAtPoint(link.getP1(), link.getLineColumn(), link.isVertical());
			mergeAtPoint(link.getP2(), link.getLineColumn(), link.isVertical());
		}
	}

	/**
	 * Check if the container contains a link
	 * @param link the link to be checked
	 * @return whether the container has the link
	 */
	public boolean contains(CircuitLink link) {
		return data.contains(link);
	}

	/**
	 * Check if the container contains a node
	 * @param node the node to be checked
	 * @return whether the container has the node
	 */
	public boolean contains(CircuitNode node) {
		return data.contains(node);
	}

	/**
	 * Removes all links
	 */
	public void clearLinks() {
		data.clearLinks();
	}

	/**
	 * Removes all nodes
	 */
	public void clearNodes() {
		data.clearNodes();
	}

	/**
	 * Get the number of links
	 * @return number of links
	 */
	public int countLinks() {
		return data.countLinks();
	}

	/**
	 * Get the number of nodes
	 * @return nubmer of nodes
	 */
	public int countNodes() {
		return data.countNodes();
	}

	/**
	 * Merge all concurrent segments in the circuit with the given one
	 * @param link the segment to merge with
	 * @return the new merged segment
	 */
	private CircuitLink mergeConcurrentSegments(CircuitLink link) {
		int p1 = link.getP1();
		int p2 = link.getP2();
		boolean vertical = link.isVertical();
		ArrayList<CircuitLink> toMerge = new ArrayList<>();

		for (CircuitLink old : data.iterLinks()) {
			boolean oldVertical = old.isVertical();
			if (vertical == oldVertical && link.getLineColumn() == old.getLineColumn()) {
				int o1 = old.getP1();
				int o2 = old.getP2();
				if ((o1 <= p1 && p1 <= o2) || (o1 <= p2 && p2 <= o2) || (p1 <= o1 && o2 <= p2)) {
					p1 = Math.min(p1, o1);
					p2 = Math.max(p2, o2);
					toMerge.add(old);
				}
			}
		}

		data.removeLinks(toMerge);

		return new CircuitLink(p1, p2, vertical, link.getLineColumn());
	}

	private ArrayList<CircuitLink> subdivideSegmentAtIntersections(CircuitLink link) {
		int p1 = link.getP1();
		int p2 = link.getP2();
		int lincol = link.getLineColumn();
		boolean vertical = link.isVertical();
		ArrayList<Integer> ptsSubdivide = new ArrayList<>();

		for (CircuitLink old : data.iterLinks()) {
			int o1 = old.getP1();
			int o2 = old.getP2();
			if (old.isVertical() != vertical) {
				int olc = old.getLineColumn();
				if ((p1 != olc && p2 != olc) || o1 > lincol || lincol > o2) {
					/* subdiv this */
					boolean contact = (o1 == lincol || o2 == lincol);
					if (contact && p1 < olc && olc < p2) {
						if (!ptsSubdivide.contains(olc)) ptsSubdivide.add(olc);
					}
				}
			}
		}
		for (CircuitNode node : data.iterNodes()) {
			for (CircuitPin pin : node.getPins()) {
				if (p1 < pin.worldX && pin.worldX < p2 && lincol == pin.worldY) {
					if (!ptsSubdivide.contains(pin.worldY)) ptsSubdivide.add(pin.worldX);
				}
				if (p1 < pin.worldY && pin.worldY < p2 && lincol == pin.worldX) {
					if (!ptsSubdivide.contains(pin.worldX)) ptsSubdivide.add(pin.worldY);
				}
			}
		}
		ptsSubdivide.add(p1);
		ptsSubdivide.add(p2);
		ptsSubdivide.sort(Comparator.comparingInt(a -> a));
		ArrayList<CircuitLink> mergeLinks = new ArrayList<>();
		for (int i = 0, subdivMaxIndex = ptsSubdivide.size() - 2; i <= subdivMaxIndex; i++) {
			mergeLinks.add(new CircuitLink(ptsSubdivide.get(i), ptsSubdivide.get(i + 1), vertical, lincol));
		}
		return mergeLinks;
	}

	private void subdivideAtPoint(int p, int lincol, boolean vertical) {
		CircuitLink subdivLink = null;
		int subdivWhere = 0;
		for (CircuitLink old : data.iterLinks()) {
			int o1 = old.getP1();
			int o2 = old.getP2();
			if (old.isVertical() != vertical) {
				int olc = old.getLineColumn();
				if (p == olc && o1 < lincol && lincol < o2) {
					subdivLink = old;
					subdivWhere = lincol;
					break;
				}
			}
		}
		if (subdivLink != null) {
			data.removeLink(subdivLink);
			data.addLink(new CircuitLink(subdivLink.getP1(), subdivWhere, !vertical, p));
			data.addLink(new CircuitLink(subdivWhere, subdivLink.getP2(), !vertical, p));
		}
	}

	private void mergeAtPoint(int p, int lincol, boolean vertical) {
		CircuitLink[] mergeLinks = new CircuitLink[2];
		int mergeLinksCounter = 0;
		boolean pointContinues = false;

		for (CircuitLink old : data.iterLinks()) {
			if (vertical != old.isVertical()) {
				if (old.getLineColumn() == p && (old.getP1() == lincol || old.getP2() == lincol)) {
					mergeLinks[mergeLinksCounter++] = old;
				}
			} else {
				if (old.getLineColumn() == lincol && (old.getP1() == p || old.getP2() == p)) {
					pointContinues = true;
				}
			}
		}
		if (!pointContinues && mergeLinksCounter == 2) {
			data.removeLink(mergeLinks[0]);
			data.removeLink(mergeLinks[1]);
			data.addLink(new CircuitLink(Math.min(mergeLinks[0].getP1(), mergeLinks[1].getP1()), Math.max(mergeLinks[0].getP2(), mergeLinks[1].getP2()), !vertical, p));
		}
	}

}