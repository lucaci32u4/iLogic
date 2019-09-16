package com.lucaci32u4.circuit;

public class WiringHelper {
	private boolean working = false;
	private boolean hasDirection = false;
	private boolean firstVertical = false;
	private int beginX = 0;
	private int beginY = 0;
	private int endX = 0;
	private int endY = 0;

	public void startLink(int x, int y) {
		if (working) throw new IllegalStateException("Cannot start a new link segment before another was finished");
		else {
			working = true;
			hasDirection = false;
			beginX = x;
			beginY = y;
			endX = x;
			endY = y;
		}
	}

	public void continueLink(int x, int y) {
		if (!working) throw new IllegalStateException("Cannot continue a link while none was started");
		endX = x;
		endY = y;
		if (!hasDirection && (beginX != endX || beginY != endY)) {
			firstVertical = (beginX == endX);
			hasDirection = true;
		}
	}

	public void toggleDirection() {
		firstVertical = !firstVertical;
	}

	/**
	 * Get a preview of the links's coordinates
	 * @param crdLocation an array of 6 integers that will be filled with data:
	 *                    [0] first point X
	 *                    [1] first point Y
	 *                    [2] second point X
	 *                    [3] second point Y
	 *                    [4] third point X
	 *                    [5] third point Y
	 */
	public void previewLinks(int[] crdLocation) {
		if (crdLocation.length != 6) throw new IllegalArgumentException("Invalid array size. Expected 6, recieved " + crdLocation.length);
		if (!working) throw new IllegalStateException("Cannot preview a link while none was started");
		crdLocation[0] = beginX;
		crdLocation[1] = beginY;
		crdLocation[4] = endX;
		crdLocation[5] = endY;
		if (hasDirection) {
			crdLocation[2] = (firstVertical ? beginX : endX);
			crdLocation[3] = (firstVertical ? endY : beginY);
		} else {
			crdLocation[2] = endX;
			crdLocation[3] = endY;
		}
	}

	public CircuitLink[] endLink() {
		if (!working) throw new IllegalStateException("Cannot end a link while none was started");
		CircuitLink[] links = null;
		if (beginX == endX && beginY == endY) {
			links = new CircuitLink[0];
		}
		if (beginX == endX && beginY != endY) {
			links = new CircuitLink[1];
			links[0] = new CircuitLink(beginY, endY, false, beginX);
		}
		if (beginX != endX && beginY == endY) {
			links = new CircuitLink[1];
			links[0] = new CircuitLink(beginX, endX, true, beginY);
		}
		if (beginX != endX && beginY != endY) {
			links = new CircuitLink[2];
			if (firstVertical) {
				links[0] = new CircuitLink(beginY, endY, true, beginX);
				links[1] = new CircuitLink(beginX, endX, false, endY);
			} else {
				links[0] = new CircuitLink(beginX, endX, false, beginY);
				links[1] = new CircuitLink(beginY, endY ,true, endX);
			}
		}
		working = false;
		return links;
	}
}
