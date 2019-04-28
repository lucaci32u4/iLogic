package com.lucaci32u4.core;

import com.lucaci32u4.util.Vector2;

public class CircuitLink {
	private Vector2 pos1 = Vector2.ZERO;
	private Vector2 pos2 = Vector2.ZERO;


	public CircuitLink(Vector2 pos1, Vector2 pos2) {
		this.pos1 = pos1;
		this.pos2 = pos2;
	}

	/**
	 * Gets the first connect position of the link
	 * @return the position
	 */
	public Vector2 getPos1() {
		return pos1;
	}

	/**
	 * Gets the second connext position of this link
	 * @return the position
	 */
	public Vector2 getPos2() {
		return pos2;
	}

	/**
	 * Sets the first connect position of this link
	 * @param pos1 the position
	 */
	void setPos1(Vector2 pos1) {
		this.pos1 = pos1;
	}

	/**
	 * Sets the second connect position of this link
	 * @param pos2 the position
	 */
	void setPos2(Vector2 pos2) {
		this.pos2 = pos2;
	}






	public static boolean isValid(CircuitLink link) {
		return (link.pos1.x == link.pos2.x) ^ (link.pos1.y == link.pos2.y);
	}
}
