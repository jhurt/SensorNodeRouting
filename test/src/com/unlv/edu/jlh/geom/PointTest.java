package com.unlv.edu.jlh.geom;

import junit.framework.TestCase;
import org.junit.Test;

public class PointTest extends TestCase {

	@Test
	public void testIsCloser() {
		Point a = new Point(100, 100);
		Point b = new Point(350, 350);
		Point c = new Point(150, 150);
		assertTrue(a.isCloser(b, c));
		assertTrue(c.isCloser(b, a));

		a = new Point(0, 0);
		b = new Point(400, 0);
		c = new Point(500, 0);
		assertTrue(b.isCloser(c, a));
		assertTrue(c.isCloser(a, b));

		a = new Point(0, 100);
		b = new Point(0, 200);
		c = new Point(0, 250);
		assertTrue(b.isCloser(c, a));
		assertTrue(c.isCloser(a, b));
	}

	@Test
	public void testIsCloserHandlesNulls() {
		Point a = new Point(100, 100);
		Point c = new Point(150, 150);
		assertFalse(a.isCloser(null, c));
		assertFalse(a.isCloser(c, null));
		assertFalse(a.isCloser(null, null));
		assertFalse(c.isCloser(null, null));
	}

	@Test
	public void testSetX() {
		Point point = new Point(5.0, 6.0);
		assertEquals(5.0, point.getX());
		point.setX(7.0);
		assertEquals(7.0, point.getX());
	}

	@Test
	public void testSetY() {
		Point point = new Point(45.0, 76.0);
		assertEquals(76.0, point.getY());
		point.setY(564.0);
		assertEquals(564.0, point.getY());
	}

	@Test
	public void testGetAngleRadiansHandlesZeroAndNull() {
		Point pointA = new Point(0, 0);
		Point pointB = new Point(5, 5);
		assertEquals(0.0, pointA.getAngleRadians(pointA));
		assertEquals(0.0, pointB.getAngleRadians(pointB));

		assertEquals(0.0, pointA.getAngleRadians(null));
	}

	@Test
	public void testGetAngleRadiansOrthogonality() {
		Point pointA = new Point(0, 0);
		Point pointB = new Point(5, 5);
		Point pointC = new Point(10, 10);
		assertEquals(pointA.getAngleRadians(pointB), pointB.getAngleRadians(pointC));
		assertEquals(pointB.getAngleRadians(pointA), pointC.getAngleRadians(pointB));

		assertEquals(Math.PI * 2, pointA.getAngleRadians(pointB) + (Math.PI - pointB.getAngleRadians(pointA)));
		assertEquals(Math.PI * 2, pointA.getAngleRadians(pointC) + (Math.PI - pointC.getAngleRadians(pointA)));
		assertEquals(Math.PI * 2, pointB.getAngleRadians(pointC) + (Math.PI - pointC.getAngleRadians(pointB)));
	}

}
