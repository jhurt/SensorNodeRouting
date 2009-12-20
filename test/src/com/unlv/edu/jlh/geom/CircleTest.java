package com.unlv.edu.jlh.geom;

import junit.framework.TestCase;
import org.junit.Test;

public class CircleTest extends TestCase {

	@Test
	public void testGetIntersectionTypeWhenDoNotIntersect() {
		Circle circleA = new Circle(40, 40, 40);
		Circle circleB = new Circle(500, 500, 40);
		assertEquals(Circle.INTERSECTION_TYPE.DO_NOT_INTERSECT, circleA.getIntersectionType(circleB));

		/** circleA is inside of circleB **/
		circleA = new Circle(40, 40, 40);
		circleB = new Circle(50, 50, 4000);
		assertEquals(Circle.INTERSECTION_TYPE.DO_NOT_INTERSECT, circleA.getIntersectionType(circleB));
	}

	@Test
	public void testGetIntersectionTypeWhenIntersectAtOnePoint() {
		Circle circleA = new Circle(0, 0, 20);
		Circle circleB = new Circle(40, 0, 20);
		assertEquals(Circle.INTERSECTION_TYPE.INTERSECTS_AT_ONE_POINT, circleA.getIntersectionType(circleB));

		circleA = new Circle(0, 20, 20);
		circleB = new Circle(0, 60, 20);
		assertEquals(Circle.INTERSECTION_TYPE.INTERSECTS_AT_ONE_POINT, circleA.getIntersectionType(circleB));

		circleA = new Circle(0, 10, 20);
		circleB = new Circle(0, 50, 20);
		assertEquals(Circle.INTERSECTION_TYPE.INTERSECTS_AT_ONE_POINT, circleA.getIntersectionType(circleB));

		circleA = new Circle(0, 0, 2.5);
		circleB = new Circle(4, 3, 2.5);
		assertEquals(Circle.INTERSECTION_TYPE.INTERSECTS_AT_ONE_POINT, circleA.getIntersectionType(circleB));
	}

	@Test
	public void testGetIntersectionTypeWhenIntersectAtTwoPoints() {
		Circle circleA = new Circle(0, 0, 30);
		Circle circleB = new Circle(40, 0, 30);
		assertEquals(Circle.INTERSECTION_TYPE.INTERSECTS_AT_TWO_POINTS, circleA.getIntersectionType(circleB));

		circleA = new Circle(0, 20, 30);
		circleB = new Circle(0, 60, 30);
		assertEquals(Circle.INTERSECTION_TYPE.INTERSECTS_AT_TWO_POINTS, circleA.getIntersectionType(circleB));

		circleA = new Circle(20, 20, 40);
		circleB = new Circle(70, 70, 40);
		assertEquals(Circle.INTERSECTION_TYPE.INTERSECTS_AT_TWO_POINTS, circleA.getIntersectionType(circleB));
	}

	@Test
	public void testDetermineIntersectionPointsCoordinates() {
		Circle circleA = new Circle(200, 200, 100);
		Circle circleB = new Circle(230, 230, 100);
		assertEquals(Circle.INTERSECTION_TYPE.INTERSECTS_AT_TWO_POINTS, circleA.getIntersectionType(circleB));
		Point expectedPointA = new Point(245.89862519457375, 384.10137480542625);
		Point expectedPointB = new Point(384.10137480542625,245.89862519457375);
		Point pointA = new Point();
		Point pointB = new Point();
		circleA.determineIntersectionPointsCoordinates(circleB, pointA, pointB);
		assertEquals(expectedPointA, pointA);
		assertEquals(expectedPointB, pointB);
	}

}
