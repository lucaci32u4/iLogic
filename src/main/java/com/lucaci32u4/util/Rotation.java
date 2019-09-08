package com.lucaci32u4.util;

public enum Rotation { // CCW
	R0(0), R90(90), R180(180), R270(270);

	private static Rotation[] indexer = new Rotation[] { R0, R90, R180, R270 };
	private final int angle;

	Rotation(int angle) {
		this.angle = angle;
	}

	public static Rotation from(int angle) {
		if (angle < 0) angle = -angle;
		angle = angle % 360;
		if (angle % 90 != 0) {
			throw new IllegalArgumentException("");
		}
		return getRotation(angle);
	}

	public int getAngle() {
		return angle;
	}

	public Rotation stepCW() {
		return stepCCW(-1);
	}

	public Rotation stepCCW() {
		return stepCCW(1);
	}

	public Rotation stepCW(int steps) {
		return stepCCW(-steps);
	}

	public Rotation stepCCW(int steps) {
		return indexer[(angle + steps * 90) % 360];
	}

	private static Rotation getRotation(int normAngle) {
		return indexer[normAngle / 90];
	}
}
