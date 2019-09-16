package com.lucaci32u4.circuit;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WiringHelperTest {

	@Test @Order(0)
	void wiring() {
		WiringHelper w = new WiringHelper();
		int[] prev = new int[6];
		CircuitLink[] links = null;
		w.startLink(1, 2);
		w.continueLink( 2, 2);
		w.previewLinks(prev);
		assertArrayEquals(new int[]{1, 2, 2, 2, 2, 2}, prev);
		w.continueLink(4, 5);
		w.previewLinks(prev);
		assertArrayEquals(new int[]{1, 2, 4, 2, 4, 5}, prev);
		links = w.endLink();
		assertEquals(2, links.length);
		assertEquals(new CircuitLink(1, 4, false, 2), links[0]);
		assertEquals(new CircuitLink(2, 5, true, 4), links[1]);
	}

	@Test @Order(1)
	void exceptions() {
		WiringHelper w = new WiringHelper();
		w.startLink(0, 0);
		assertThrows(IllegalStateException.class, () -> w.startLink(2, 2));
		w.endLink();
		assertThrows(IllegalStateException.class, () -> w.endLink());
		assertThrows(IllegalStateException.class, () -> w.continueLink(6, 6));
		assertThrows(IllegalStateException.class, () -> w.previewLinks(new int[6]));
		assertThrows(IllegalArgumentException.class, () -> w.previewLinks(new int[4]));
	}
}