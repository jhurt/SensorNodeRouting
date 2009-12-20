package com.unlv.edu.jlh.geom;

import java.awt.geom.Ellipse2D;
import java.util.List;

public class Circle extends Ellipse2D.Double {
	public enum INTERSECTION_TYPE {
		DO_NOT_INTERSECT, INTERSECTS_AT_ONE_POINT, INTERSECTS_AT_TWO_POINTS
	}

	protected final double radius;

	/**
	 * Create a new Circle
	 *
	 * @param x			the x value of the upper left corner of the Circle's bounding box
	 * @param y			the y value of the upper left corner of the Circle's bounding box
	 * @param radius the radius of the new Circle
	 */
	public Circle(double x, double y, double radius) {
		super(x, y, radius * 2, radius * 2);
		this.radius = radius;
	}

	/**
	 * @return The radius of this Circle
	 */
	public double getRadius() {
		return radius;
	}

	/**
	 * @return a Point with the center coordinates of this Circle
	 */
	public Point getCenter() {
		return new Point(this.getCenterX(), this.getCenterY());
	}

	/**
	 * @param otherCircle
	 * @return true iff this Circle intersects the Circle specified by otherCircle
	 */
	public boolean intersects(final Circle otherCircle) {
		return getIntersectionType(otherCircle) != INTERSECTION_TYPE.DO_NOT_INTERSECT;
	}

	/**
	 * Set the intersection points of the intersection of this Circle with the Circle specified by otherCircle
	 *
	 * @param otherCircle
	 * @param intersectionPointOne
	 * @param intersectionPointTwo
	 */
	public void determineIntersectionPointsCoordinates(final Circle otherCircle, final Point intersectionPointOne,
																										 final Point intersectionPointTwo) {
		if (otherCircle == null || intersectionPointOne == null || intersectionPointTwo == null) {
			return;
		}
		double centerToCenterDistance = getCenter().distance(otherCircle.getCenter());
		/** Determine the distance from point 0 to point 2. point 2 is the point where the line through the circle
		 * intersection points crosses the line between the circle centers
		 **/
		double a = ((getRadius() * getRadius()) -
				(otherCircle.getRadius() * otherCircle.getRadius()) +
				(centerToCenterDistance * centerToCenterDistance)) / (2.0 * centerToCenterDistance);

		double dx = otherCircle.getCenterX() - getCenterX();
		double dy = otherCircle.getCenterY() - getCenterY();
		/** Determine the coordinates of point 2. **/
		double x2 = getCenterX() + (dx * a / centerToCenterDistance);
		double y2 = getCenterY() + (dy * a / centerToCenterDistance);

		/** Determine the distance from point 2 to either of the intersection points **/
		double h = Math.sqrt((getRadius() * getRadius()) - (a * a));

		/* Now determine the offsets of the intersection points from point 2 **/
		double rx = -dy * (h / centerToCenterDistance);
		double ry = dx * (h / centerToCenterDistance);

		/* Determine the absolute intersection points. */
		intersectionPointOne.setX(x2 + rx);
		intersectionPointOne.setY(y2 + ry);

		intersectionPointTwo.setX(x2 - rx);
		intersectionPointTwo.setY(y2 - ry);
	}

	/**
	 * Determine the intersection type of this Circle with the Circle specified by otherCircle
	 *
	 * @param otherCircle
	 * @return INTERSECTION_TYPE.DO_NOT_INTERSECT if the 2 circles do not intersect
	 *         INTERSECTION_TYPE_INTERSECTS_AT_ONE_POINT if the 2 circles intersect at exactly one point
	 *         INTERSECTION_TYPE_INTERSECTS_AT_TWO_POINTS if the 2 circles intersect at exactly two points
	 */
	public INTERSECTION_TYPE getIntersectionType(final Circle otherCircle) {
		if (otherCircle == null) {
			return INTERSECTION_TYPE.DO_NOT_INTERSECT;
		}
		double centerToCenterDistance = getCenter().distance(otherCircle.getCenter());
		if (centerToCenterDistance > getRadius() + otherCircle.getRadius()) {
			return INTERSECTION_TYPE.DO_NOT_INTERSECT;
		}
		if (centerToCenterDistance < Math.abs(getRadius() - otherCircle.getRadius())) {
			/** one circle is contained within the other **/
			return INTERSECTION_TYPE.DO_NOT_INTERSECT;
		}

		if (centerToCenterDistance == getRadius() + otherCircle.getRadius()) {
			return INTERSECTION_TYPE.INTERSECTS_AT_ONE_POINT;
		}
		return INTERSECTION_TYPE.INTERSECTS_AT_TWO_POINTS;
	}

	public boolean containsPoint(final Point point) {
		assert point != null;

		return this.getCenter().distance(point) <= radius + 0.5;
		//return this.contains(point);
	}

	public Arc getArcThatIsOutsideMore(final Arc arcA, final Arc arcB) {
		List<Point> pointsA = arcA.getSomePoints();
		int a = 0;
		for (Point point : pointsA) {
			if (containsPoint(point)) {
				a++;
			}
		}
		List<Point> pointsB = arcB.getSomePoints();
		int b = 0;
		for (Point point : pointsB) {
			if (containsPoint(point)) {
				b++;
			}
		}
		if (a < b) {
			return arcA;
		}
		return arcB;
	}

	public boolean containsSomePoints(List<Point> points) {
		assert points != null;
		assert points.size() > 0;
		int count = 0;
		for (Point point : points) {
			if (containsPoint(point)) {
				count++;
			}
		}
		return count >= 10;
	}

	public boolean containsManyPoints(final Arc arc) {
		List<Point> points = arc.getSomePoints();
		int count = 0;
		for (Point point : points) {
			if (containsPoint(point)) {
				count++;
			}
		}
		return count > (0.65 * points.size());
	}

}
