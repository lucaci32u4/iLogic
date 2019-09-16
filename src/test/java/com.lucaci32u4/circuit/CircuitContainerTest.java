package com.lucaci32u4.circuit;

import com.lucaci32u4.util.Rotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CircuitContainerTest {

	@Test @Order(1)
	void AddLinks() {
		CircuitContainer container = new CircuitContainer();
		/* Single */
		CircuitLink l1 = new CircuitLink(3, 7, true, 0);
		CircuitLink l2 = new CircuitLink(3, 7, false, 0);
		CircuitLink l3 = new CircuitLink(3, 7, true, 10);
		CircuitLink l4 = new CircuitLink(3, 7, false, 10);
		container.addCircuitLink(l1);
		assertEquals(1, container.countLinks());
		assertTrue(container.contains(l1));
		container.addCircuitLink(l2);
		assertEquals(2, container.countLinks());
		assertTrue(container.contains(l1));
		assertTrue(container.contains(l2));
		container.addCircuitLink(l3);
		assertEquals(3, container.countLinks());
		assertTrue(container.contains(l1));
		assertTrue(container.contains(l2));
		assertTrue(container.contains(l3));
		container.addCircuitLink(l4);
		assertEquals(4, container.countLinks());
		assertTrue(container.contains(l1));
		assertTrue(container.contains(l2));
		assertTrue(container.contains(l3));
		assertTrue(container.contains(l4));

		/* Adding coliniar getAllLinks() */
		CircuitLink l3_ext1 = new CircuitLink(11, 13, true, 10);
		CircuitLink l3_ext2 = new CircuitLink(15, 17, true, 10);
		CircuitLink l4_ext1 = new CircuitLink(11, 13, false, 10);
		CircuitLink l4_ext2 = new CircuitLink(15, 17, false, 10);
		container.addCircuitLink(new CircuitLink(11, 13, true, 10));
		container.addCircuitLink(new CircuitLink(15, 17, true, 10));
		container.addCircuitLink(new CircuitLink(11, 13, false, 10));
		container.addCircuitLink(new CircuitLink(15, 17, false, 10));
		assertEquals(8, container.countLinks());
		assertTrue(container.contains(l1));
		assertTrue(container.contains(l2));
		assertTrue(container.contains(l3));
		assertTrue(container.contains(l4));
		assertTrue(container.contains(l3_ext1));
		assertTrue(container.contains(l3_ext2));
		assertTrue(container.contains(l4_ext1));
		assertTrue(container.contains(l4_ext2));

		/* Extension on each end, cover one only */
		l1 = new CircuitLink(2, 8, true, 0);
		l2 = new CircuitLink(2, 8, false, 0);
		l3 = new CircuitLink(2, 8, true, 10);
		l4 = new CircuitLink(2, 8, false, 10);
		container.addCircuitLink(new CircuitLink(2, 4, true, 0));
		assertEquals(8, container.countLinks());
		container.addCircuitLink(new CircuitLink(6, 8, true, 0));
		assertEquals(8, container.countLinks());
		assertTrue(container.contains(l1));
		container.addCircuitLink(new CircuitLink(2, 4, false, 0));
		assertEquals(8, container.countLinks());
		container.addCircuitLink(new CircuitLink(6, 8, false, 0));
		assertEquals(8, container.countLinks());
		assertTrue(container.contains(l1));
		assertTrue(container.contains(l2));
		container.addCircuitLink(new CircuitLink(2, 4, true, 10));
		assertEquals(8, container.countLinks());
		container.addCircuitLink(new CircuitLink(6, 8, true, 10));
		assertEquals(8, container.countLinks());
		assertTrue(container.contains(l1));
		assertTrue(container.contains(l2));
		assertTrue(container.contains(l3));
		container.addCircuitLink(new CircuitLink(2, 4, false, 10));
		assertEquals(8, container.countLinks());
		container.addCircuitLink(new CircuitLink(6, 8, false, 10));
		assertEquals(8, container.countLinks());
		assertTrue(container.contains(l1));
		assertTrue(container.contains(l2));
		assertTrue(container.contains(l3));
		assertTrue(container.contains(l4));
		assertTrue(container.contains(l3_ext1));
		assertTrue(container.contains(l3_ext2));
		assertTrue(container.contains(l4_ext1));
		assertTrue(container.contains(l4_ext2));

		/* Extension on both ends, cover one only */
		l1 = new CircuitLink(1, 9, true, 0);
		l2 = new CircuitLink(1, 9, false, 0);
		l3 = new CircuitLink(1, 9, true, 10);
		l4 = new CircuitLink(1, 9, false, 10);
		container.addCircuitLink(l1);
		assertEquals(8, container.countLinks());
		assertTrue(container.contains(l1));
		container.addCircuitLink(l2);
		assertEquals(8, container.countLinks());
		assertTrue(container.contains(l1));
		assertTrue(container.contains(l2));
		container.addCircuitLink(l3);
		assertEquals(8, container.countLinks());
		assertTrue(container.contains(l1));
		assertTrue(container.contains(l2));
		assertTrue(container.contains(l3));
		container.addCircuitLink(l4);
		assertEquals(8, container.countLinks());
		assertTrue(container.contains(l1));
		assertTrue(container.contains(l2));
		assertTrue(container.contains(l3));
		assertTrue(container.contains(l4));

		/* Extension both ends, cover many */
		l3 = new CircuitLink(1, 18, true, 10);
		l4 = new CircuitLink(1, 18, false, 10);
		container.addCircuitLink(l3);
		container.addCircuitLink(l4);
		assertEquals(4, container.countLinks());
		assertTrue(container.contains(l1));
		assertTrue(container.contains(l2));
		assertTrue(container.contains(l3));
		assertTrue(container.contains(l4));
		assertFalse(container.contains(l3_ext1));
		assertFalse(container.contains(l3_ext2));
		assertFalse(container.contains(l4_ext1));
		assertFalse(container.contains(l4_ext2));
		container.clearLinks();

		/* Subdivision with many free fins */
		CircuitLink fin1 = new CircuitLink(3, 4, false, 4);
		CircuitLink fin2 = new CircuitLink(3, 4, false, 5);
		CircuitLink fin3 = new CircuitLink(3, 4, false, 6);
		CircuitLink fin4 = new CircuitLink(3, 4, false, 7);
		CircuitLink fin5 = new CircuitLink(3, 4, false, 8);
		CircuitLink fin6 = new CircuitLink(3, 4, false, 9);
		l1 = new CircuitLink(1, 6, true, 3);
		container.addCircuitLink(fin1);
		container.addCircuitLink(fin2);
		container.addCircuitLink(fin3);
		container.addCircuitLink(fin4);
		container.addCircuitLink(fin5);
		container.addCircuitLink(fin6);
		assertTrue(container.contains(fin1));
		assertTrue(container.contains(fin2));
		assertTrue(container.contains(fin3));
		assertTrue(container.contains(fin4));
		assertTrue(container.contains(fin5));
		assertTrue(container.contains(fin6));
		assertEquals(6, container.countLinks());
		container.addCircuitLink(l1);
		assertTrue(container.contains(fin1));
		assertTrue(container.contains(fin2));
		assertTrue(container.contains(fin3));
		assertTrue(container.contains(fin4));
		assertTrue(container.contains(fin5));
		assertTrue(container.contains(fin6));
		assertFalse(container.contains(l1));
		assertTrue(container.contains(new CircuitLink(1, 4, true, 3)));
		assertTrue(container.contains(new CircuitLink(4, 5, true, 3)));
		assertTrue(container.contains(new CircuitLink(5, 6, true, 3)));
		assertEquals(9, container.countLinks());
		l1 = new CircuitLink(4, 5, true, 4);
		l2 = new CircuitLink(8, 9, true, 4);
		container.addCircuitLink(l1);
		container.addCircuitLink(l2);
		assertTrue(container.contains(fin1));
		assertTrue(container.contains(fin2));
		assertTrue(container.contains(fin3));
		assertTrue(container.contains(fin4));
		assertTrue(container.contains(fin5));
		assertTrue(container.contains(fin6));
		assertTrue(container.contains(l1));
		assertTrue(container.contains(l2));
		assertEquals(11, container.countLinks());

		/* Mixed subdivision and extending */
		l1 = new CircuitLink(2, 9, true, 3);
		l2 = new CircuitLink(4, 9, true, 4);
		container.addCircuitLink(l1);
		container.addCircuitLink(l2);
		assertTrue(container.contains(fin1));
		assertTrue(container.contains(fin2));
		assertTrue(container.contains(fin3));
		assertTrue(container.contains(fin4));
		assertTrue(container.contains(fin5));
		assertTrue(container.contains(fin6));
		assertFalse(container.contains(l1));
		assertFalse(container.contains(l2));
		assertTrue(container.contains(new CircuitLink(1, 4, true, 3)));
		assertTrue(container.contains(new CircuitLink(4, 5, true, 3)));
		assertTrue(container.contains(new CircuitLink(5, 6, true, 3)));
		assertTrue(container.contains(new CircuitLink(6, 7, true, 3)));
		assertTrue(container.contains(new CircuitLink(7, 8, true, 3)));
		assertTrue(container.contains(new CircuitLink(8, 9, true, 3)));
		assertTrue(container.contains(new CircuitLink(4, 5, true, 4)));
		assertTrue(container.contains(new CircuitLink(5, 6, true, 4)));
		assertTrue(container.contains(new CircuitLink(6, 7, true, 4)));
		assertTrue(container.contains(new CircuitLink(7, 8, true, 4)));
		assertTrue(container.contains(new CircuitLink(8, 9, true, 4)));
		assertEquals(17, container.countLinks());

		/* Subdivision of existent getAllLinks() */
		container.clearLinks();
		l1 = new CircuitLink(0, 15, true, 10);
		l2 = new CircuitLink(8, 10, false, 2);
		l3 = new CircuitLink(10, 12, false, 4);
		l4 = new CircuitLink(8, 10, false, 4);
		container.addCircuitLink(l1);
		assertTrue(container.contains(l1));
		assertEquals(1, container.countLinks());
		container.addCircuitLink(l2);
		container.addCircuitLink(l3);
		container.addCircuitLink(l4);
		assertTrue(container.contains(l2));
		assertTrue(container.contains(l3));
		assertTrue(container.contains(l4));
		assertFalse(container.contains(l1));
		assertTrue(container.contains(new CircuitLink(0, 2, true, 10)));
		assertTrue(container.contains(new CircuitLink(2, 4, true, 10)));
		assertTrue(container.contains(new CircuitLink(4, 15, true, 10)));
		assertEquals(6, container.countLinks());
		l1 = new CircuitLink(8, 12, false, 6);
		container.addCircuitLink(l1);
		assertTrue(container.contains(l1));
		assertTrue(container.contains(l2));
		assertTrue(container.contains(l3));
		assertTrue(container.contains(l4));
		assertTrue(container.contains(new CircuitLink(0, 2, true, 10)));
		assertTrue(container.contains(new CircuitLink(2, 4, true, 10)));
		assertTrue(container.contains(new CircuitLink(4, 15, true, 10)));
		assertEquals(7, container.countLinks());
		container.clearLinks();
	}

	@Test @Order(2)
	void RemoveLinks() {
		CircuitContainer container = new CircuitContainer();
		CircuitLink vbar1 = new CircuitLink(0, 7, true, 0);
		CircuitLink vbar2 = new CircuitLink(0, 7, true, 4);
		CircuitLink vbar3 = new CircuitLink(0, 7, true, 8);
		CircuitLink vbar4 = new CircuitLink(0, 7, true, 12);
		container.addCircuitLink(vbar1);
		container.addCircuitLink(vbar2);
		container.addCircuitLink(vbar3);
		container.addCircuitLink(vbar4);
		CircuitLink h1 = new CircuitLink(0, 2, false, 2);
		CircuitLink h2 = new CircuitLink(2, 4, false, 6);
		CircuitLink h3 = new CircuitLink(8, 12, false, 4);
		container.addCircuitLink(h1);
		container.addCircuitLink(h2);
		container.addCircuitLink(h3);

		/* Removing branch to the right */
		container.removeCircuitLink(h1);
		assertTrue(container.contains(vbar1));
		assertTrue(container.contains(h2));
		assertTrue(container.contains(h3));
		assertEquals(9, container.countLinks());

		/* Removing branch to the left */
		container.removeCircuitLink(h2);
		assertTrue(container.contains(vbar1));
		assertTrue(container.contains(vbar2));
		assertTrue(container.contains(h3));
		assertEquals(7, container.countLinks());

		/* Removing branch to both directions */
		container.removeCircuitLink(h3);
		assertTrue(container.contains(vbar1));
		assertTrue(container.contains(vbar2));
		assertTrue(container.contains(vbar3));
		assertTrue(container.contains(vbar4));
		assertEquals(4, container.countLinks());

		CircuitLink cross1 = new CircuitLink(7, 8, false, 3);
		CircuitLink cross2 = new CircuitLink(12, 13, false, 3);

		/* Removing from cross */
		container.addCircuitLink(h3);
		container.addCircuitLink(cross1);
		container.addCircuitLink(cross2);
		container.removeCircuitLink(h3);
		assertTrue(container.contains(vbar1));
		assertTrue(container.contains(vbar2));
		assertTrue(container.contains(cross1));
		assertTrue(container.contains(cross2));
		assertTrue(container.contains(new CircuitLink(0, 3, true, 8)));
		assertTrue(container.contains(new CircuitLink(3, 7, true, 8)));
		assertTrue(container.contains(new CircuitLink(0, 3, true, 12)));
		assertTrue(container.contains(new CircuitLink(3, 7, true, 12)));
		assertEquals(8, container.countLinks());
		container.removeCircuitLink(cross1);
		container.removeCircuitLink(cross2);
		assertTrue(container.contains(vbar1));
		assertTrue(container.contains(vbar2));
		assertTrue(container.contains(vbar3));
		assertTrue(container.contains(vbar4));
		assertEquals(4, container.countLinks());
	}

	@Test @Order(3)
	void ComponentsLinks() {
		CircuitContainer container = new CircuitContainer();
		CircuitComponent[] n = new CircuitComponent[4];
		for (int i = 0; i < n.length; i++) { n[i] = new ComponentA(1); }
		for (int i = 0; i < n.length; i++) { container.addCircuitComponent(n[i], Rotation.from(90 * i) ,10 * i, 5); }
		for (int i = 0; i < n.length; i++) {
			assertEquals(10 * i, n[i].getPositionX());
			assertEquals(5, n[i].getPositionY());
		}
		assertEquals(0, n[0].getPins()[0].worldX);
		assertEquals(11,  n[1].getPins()[0].worldX);
		assertEquals(22, n[2].getPins()[0].worldX);
		assertEquals(31, n[3].getPins()[0].worldX);
		assertEquals(6, n[0].getPins()[0].worldY);
		assertEquals(7, n[1].getPins()[0].worldY);
		assertEquals(6, n[2].getPins()[0].worldY);
		assertEquals(5, n[3].getPins()[0].worldY);
		for (int i = 0; i < n.length; i++) { assertTrue(container.contains(n[i])); }
		for (int i = 0; i < n.length; i++) { container.removeCircuitComponent(n[i]); }
		for (int i = 0; i < n.length; i++) { assertFalse(container.contains(n[i])); }
		assertEquals(0, container.countComponents());
		for (int i = 0; i < n.length; i++) { container.addCircuitComponent(n[i], Rotation.from(90 * i) ,10 * i, 5); }
		CircuitLink cl1 = new CircuitLink(0, 4, false, 6);
		CircuitLink cl2 = new CircuitLink(7, 9, true, 22);
		container.addCircuitLink(cl1);
		container.addCircuitLink(cl2);
		assertTrue(container.contains(cl1));
		assertTrue(container.contains(cl2));
		assertEquals(2, container.countLinks());
		container.removeCircuitLink(cl1);
		container.removeCircuitLink(cl2);
		assertEquals(0, container.countLinks());
		cl1 = new CircuitLink(5, 9, true, 0);
		cl2 = new CircuitLink(8, 16, false, 7);
		container.addCircuitLink(cl1);
		container.addCircuitLink(cl2);
		assertFalse(container.contains(cl1));
		assertFalse(container.contains(cl2));
		assertTrue(container.contains(new CircuitLink(5, 6, true, 0)));
		assertTrue(container.contains(new CircuitLink(6, 9, true, 0)));
		assertTrue(container.contains(new CircuitLink(8, 11, false, 7)));
		assertTrue(container.contains(new CircuitLink(11, 16, false, 7)));
		assertEquals(4, container.countLinks());
		for (int i = 0; i < n.length; i++) { container.removeCircuitComponent(n[i]); }
		assertTrue(container.contains(cl1));
		assertTrue(container.contains(cl2));
		assertEquals(2, container.countLinks());
		for (int i = 0; i < n.length; i++) { container.addCircuitComponent(n[i], Rotation.from(90 * i) ,10 * i, 5); }
		assertFalse(container.contains(cl1));
		assertFalse(container.contains(cl2));
		assertTrue(container.contains(new CircuitLink(5, 6, true, 0)));
		assertTrue(container.contains(new CircuitLink(6, 9, true, 0)));
		assertTrue(container.contains(new CircuitLink(8, 11, false, 7)));
		assertTrue(container.contains(new CircuitLink(11, 16, false, 7)));
		assertEquals(4, container.countLinks());
		container.clearLinks();
		container.clearComponents();
	}
}

/**
 * Basic 2x2 component with one pin on the left
 */
class ComponentA extends CircuitComponent {
	ComponentA(int bitWidth) {
		CircuitPin[] pins = new CircuitPin[] { new CircuitPin(0, 1, bitWidth) };
		super.initComponent(pins, 2, 2);
	}
}