package com.unlv.edu.jlh.geom;

import junit.framework.TestCase;
import org.junit.Test;

public class ArcTest extends TestCase {

  @Test
  public void testArcContainsPoint() {
    Circle circleA = new Circle(200, 200, 300);
    Arc arc = new Arc(circleA, 0.0, 90.0);
		Point point = new Point(505.0, 495.0);
		assertEquals(true, arc.containsPoint(point));

		arc = new Arc(circleA, 90.0, 90.0);
		point = new Point(495.0, 495.0);
		assertEquals(true, arc.containsPoint(point));

		arc = new Arc(circleA, 180.0, 90.0);
		point = new Point(495.0, 505.0);
		assertEquals(true, arc.containsPoint(point));

		arc = new Arc(circleA, 270.0, 90.0);
		point = new Point(505.0, 505.0);
		assertEquals(true, arc.containsPoint(point));
  }

	@Test
	public void testPointIsOnArc3() {
		Circle circleA = new Circle(200, 200, 300);
    Arc arc = new Arc(circleA, 0.0, 90.0);
		Point point = new Point(800.0, 500.0);
		assertTrue(arc.pointIsOnArc(point));
		point = new Point(800.0, 550.0);
		assertFalse(arc.pointIsOnArc(point));
	}
	
	@Test
	public void testPointIsOnArc() {
		Circle circleA = new Circle(200, 300, 100);
		Circle circleB = new Circle(220, 301, 100);
		Arc arcA = new Arc(circleA, 270.0, 180.0);
		Arc arcB = new Arc(circleB, 90.0, 180.0);
		Point pointA = new Point();
		Point pointB = new Point();
		assertEquals(Arc.INTERSECTION_TYPE.INTERSECTS_AT_TWO_POINTS,
				arcA.determineIntersectionPoints(arcB, pointA, pointB));
		assertTrue(arcA.pointIsOnArc(pointA));
		assertTrue(arcA.pointIsOnArc(pointB));
		assertTrue(arcB.pointIsOnArc(pointA));
		assertTrue(arcB.pointIsOnArc(pointB));
	}

	@Test
	public void testPointIsOnArc2() {
		Circle circleA = new Circle(220, 301, 100);
		Point pointA = new Point(305.0313325870663, 499.8733482586737);
		Point pointB = new Point(314.9686674129337, 301.1266517413263);
		Arc arcA = new Arc(circleA, pointA, pointB);
		assertTrue(arcA.pointIsOnArc(pointA));
		assertTrue(arcA.pointIsOnArc(pointB));
	}

	@Test
	public void testConstructArc() {
		Circle circleA = new Circle(353.0, 75.0, 300.0);
		Point pointA = new Point(596.9033060979825, 669.7086034259322);
		Point pointB = new Point(575.0966939020175, 85.29139657406779);
		Arc arc = new Arc(circleA, pointB, pointA);
		assertNotNull(arc);
		assertEquals(arc.getCircle().getCenterX(), arc.getCenterX());
		assertEquals(arc.getCircle().getCenterY(), arc.getCenterY());

		assertEquals(105.05093809692141, arc.getAngleStart());
		assertEquals(154.17194551367166, arc.getAngleExtent());
	}

	@Test
	public void testDetermineIntersectionPointsWhenArcsIntersectAtTwoPoints() {
		Circle circleA = new Circle(200, 300, 100);
		Circle circleB = new Circle(220, 301, 100);
		Arc arcA = new Arc(circleA, 270.0, 180.0);
		Arc arcB = new Arc(circleB, 90.0, 180.0);
		assertEquals(Arc.INTERSECTION_TYPE.INTERSECTS_AT_TWO_POINTS,
				arcA.determineIntersectionPoints(arcB, new Point(), new Point()));
	}

	@Test
	public void testDetermineIntersectionPointsWhenArcsDoNotIntersect() {
		Circle circleA = new Circle(200, 300, 100);
		Circle circleB = new Circle(220, 301, 100);
		Arc arcA = new Arc(circleA, 90.0, 180.0);
		Arc arcB = new Arc(circleB, 270.0, 180.0);
		assertEquals(Arc.INTERSECTION_TYPE.DO_NOT_INTERSECT,
				arcA.determineIntersectionPoints(arcB, new Point(), new Point()));

		circleA = new Circle(200, 300, 100);
		circleB = new Circle(700, 300, 100);
		arcA = new Arc(circleA, 270.0, 180.0);
		arcB = new Arc(circleB, 90.0, 180.0);
		assertEquals(Arc.INTERSECTION_TYPE.DO_NOT_INTERSECT,
				arcA.determineIntersectionPoints(arcB, new Point(), new Point()));
	}

	@Test
	public void testDetermineIntersectionPointsWhenArcsIntersectAtOnePoint() {
		Circle circleA = new Circle(200, 300, 100);
		Circle circleB = new Circle(220, 301, 100);
		Arc arcA = new Arc(circleA, 270.0, 180.0);
		Arc arcB = new Arc(circleB, 90.0, 90.0);
		assertEquals(Arc.INTERSECTION_TYPE.INTERSECTS_AT_ONE_POINT,
				arcA.determineIntersectionPoints(arcB, new Point(), new Point()));

		circleA = new Circle(200, 300, 100);
		circleB = new Circle(220, 301, 100);
		arcA = new Arc(circleA, 270.0, 90.0);
		arcB = new Arc(circleB, 90.0, 180.0);
		assertEquals(Arc.INTERSECTION_TYPE.INTERSECTS_AT_ONE_POINT,
				arcA.determineIntersectionPoints(arcB, new Point(), new Point()));
	}

	@Test
	public void testGetNumberOfOrthogonalRays() {
		Circle circleA = new Circle(5, 5, 100);
		Arc arcA = new Arc(circleA, 0.0, 95.0);
		assertEquals(1, arcA.getNumberOfOrthogonalRays());
		arcA = new Arc(circleA, 0.0, 190.0);
		assertEquals(2, arcA.getNumberOfOrthogonalRays());
		arcA = new Arc(circleA, 0.0, 285.0);
		assertEquals(3, arcA.getNumberOfOrthogonalRays());
		arcA = new Arc(circleA, 0.0, 360.0);
		assertEquals(4, arcA.getNumberOfOrthogonalRays());

		arcA = new Arc(circleA, 80.0, 95.0);
		assertEquals(1, arcA.getNumberOfOrthogonalRays());
		arcA = new Arc(circleA, 80.0, 110.0);
		assertEquals(2, arcA.getNumberOfOrthogonalRays());
		arcA = new Arc(circleA, 80.0, 180.0);
		assertEquals(2, arcA.getNumberOfOrthogonalRays());
		arcA = new Arc(circleA, 80.0, 360.0);
		assertEquals(4, arcA.getNumberOfOrthogonalRays());
	}

	@Test
	public void testGetStartQuadrant() {
		Circle circleA = new Circle(45, 343, 3452);
		Arc arcA = new Arc(circleA, 50.0, 95.0);
		assertEquals(Arc.QUADRANT.I, arcA.getStartQuadrant());
		arcA = new Arc(circleA, 115.0, 95.0);
		assertEquals(Arc.QUADRANT.II, arcA.getStartQuadrant());
		arcA = new Arc(circleA, 200.0, 95.0);
		assertEquals(Arc.QUADRANT.III, arcA.getStartQuadrant());
		arcA = new Arc(circleA, 290.0, 95.0);
		assertEquals(Arc.QUADRANT.IV, arcA.getStartQuadrant());
	}

	@Test
	public void testGetEndQuadrant() {
		Circle circleA = new Circle(6712, 2345, 341234);
		Arc arcA = new Arc(circleA, 0.0, 50.0);
		assertEquals(Arc.QUADRANT.I, arcA.getEndQuadrant());
		arcA = new Arc(circleA, 30.0, 90.0);
		assertEquals(Arc.QUADRANT.II, arcA.getEndQuadrant());
		arcA = new Arc(circleA, 50.0, 190.0);
		assertEquals(Arc.QUADRANT.III, arcA.getEndQuadrant());
		arcA = new Arc(circleA, 100.0, 250.0);
		assertEquals(Arc.QUADRANT.IV, arcA.getEndQuadrant());
		arcA = new Arc(circleA, 130.0, 330.0);
		assertEquals(Arc.QUADRANT.II, arcA.getEndQuadrant());
	}

	@Test
	public void testGetAngle360() {
		Circle circleA = new Circle(6712, 2345, 341234);
		Arc arcA = new Arc(circleA, 0.0, 50.0);
		assertEquals(25.0, arcA.getAngle360(25.0));
		assertEquals(195.0, arcA.getAngle360(-165.0));
	}

	@Test
	public void testGetAngleStart360() {
		Circle circleA = new Circle(6712, 2345, 341234);
		Arc arcA = new Arc(circleA, -175.0, 50.0);
		assertEquals(185.0, arcA.getAngleStart360());
	}

	@Test
	public void testGetAngleEnd360() {
		Circle circleA = new Circle(6712, 2345, 341234);
		Arc arcA = new Arc(circleA, 30.0, -155);
		assertEquals(235.0, arcA.getAngleEnd360());
		arcA = new Arc(circleA, 45.0, 105.0);
		assertEquals(150.0, arcA.getAngleEnd360());
		arcA = new Arc(circleA, 355.0, 370.0);
		assertEquals(5.0, arcA.getAngleEnd360());
	}
}
