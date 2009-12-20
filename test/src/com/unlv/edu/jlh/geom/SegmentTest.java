package com.unlv.edu.jlh.geom;

import junit.framework.TestCase;
import org.junit.Test;

public class SegmentTest extends TestCase {

	@Test
	public void testGetAngleRadiansWithSegment() {
		Point pointA = new Point(0, 0);
		Point pointB = new Point(5, 0);
		Point pointC = new Point(0, 5);

		Segment segmentOne = new Segment(pointA, pointB);
		Segment segmentTwo = new Segment(pointA, pointC);

		assertEquals(Math.PI * 2 * 0.75, segmentOne.getAngleRadiansWithSegment(segmentTwo));
		assertEquals(Math.PI / 2, segmentTwo.getAngleRadiansWithSegment(segmentOne));
	}

	@Test
	public void testEquals() {
		Point pointA = new Point(1, 2);
		Point pointB = new Point(1, 2);
		Point pointC = new Point(4, 5);
		Point pointD = new Point(4, 5);
		Point pointE = new Point(6, 7);
		Point pointF = new Point(9, 10);

		Segment segmentOne = new Segment(pointA, pointC);
		Segment segmentTwo = new Segment(pointB, pointD);
		Segment segmentThree = new Segment(pointE, pointF);

		assertTrue(segmentOne.equals(segmentTwo));
		assertTrue(segmentTwo.equals(segmentOne));
		assertFalse(segmentTwo.equals(segmentThree));
		assertFalse(segmentThree.equals(segmentOne));
	}
}
