package com.lucaci32u4.core;

public class CircuitLink {
	private final int p1, p2, lincol;
	private final boolean vertical;


	public CircuitLink(int p1, int p2, boolean vertical, int lincol) {
		if (p1 > p2) { int aux = p1; p1 = p2; p2 = aux; }
		this.p1 = p1;
		this.p2 = p2;
		this.vertical = vertical;
		this.lincol = lincol;
	}

	public boolean isVertical() {
		return vertical;
	}

	public int getP1() {
		return p1;
	}

	public int getP2() {
		return p2;
	}

	public int getLineColumn() {
		return lincol;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof CircuitLink) {
			CircuitLink l = (CircuitLink)o;
			return l.vertical == vertical && l.lincol == lincol && l.p1 == p1 && l.p2 == p2;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
		// TODO: do actual hashCode calculation
	}

	@Override
	public String toString() {
		return new StringBuilder("[").append(vertical ? "col " : "lin ").append(lincol).append(", ").append(p1).append("->").append(p2).append("]").toString();
	}
}
