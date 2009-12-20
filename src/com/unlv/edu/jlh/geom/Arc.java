package com.unlv.edu.jlh.geom;

import java.awt.geom.Arc2D;
import java.util.List;
import java.util.ArrayList;

public class Arc extends Arc2D.Double {
	public enum INTERSECTION_TYPE {
		DO_NOT_INTERSECT, INTERSECTS_AT_ONE_POINT, INTERSECTS_AT_TWO_POINTS
	}

	public enum QUADRANT {
		I, II, III, IV
	}

	private final Circle circle;

	/**
	 * @param circle
	 * @param angSt
	 * @param angExt
	 */
	public Arc(final Circle circle, double angSt, double angExt) {
		this.circle = circle;
		this.setArcByCenter(circle.getCenterX(), circle.getCenterY(),
				circle.getRadius(), angSt, angExt, Arc2D.PIE);
	}

	/**
	 * @param x
	 * @param y
	 * @param radius
	 * @param angSt
	 * @param angExt
	 */
	public Arc(double x, double y, double radius, double angSt, double angExt) {
		this(new Circle(x, y, radius), angSt, angExt);
	}

	/**
	 * @param supportingCircle
	 * @param endPointA
	 * @param endPointB
	 */
	public Arc(final Circle supportingCircle, final Point endPointA, final Point endPointB) {
		this.circle = supportingCircle;
		Arc2D.Double arcA = new Arc2D.Double();
		arcA.setFrame(supportingCircle.getBounds2D());
		arcA.setAngles(endPointA, endPointB);
		setArc(arcA);
		setArcType(Arc2D.PIE);
	}

	/**
	 * @param supportingCircle
	 * @param startSegment
	 * @param endSegment
	 */
	public Arc(final Circle supportingCircle, final Segment startSegment, final Segment endSegment) {
		this(supportingCircle, startSegment.getTargetPoint(), endSegment.getTargetPoint());
		assert (startSegment.getSourcePoint().equals(supportingCircle.getCenter()));
		assert (endSegment.getSourcePoint().equals(supportingCircle.getCenter()));
	}

	/**
	 * @param circle
	 * @param pointA
	 * @param pointB
	 * @return the smaller of the two possible arcs formed by the two points and the circle
	 */
	public static Arc getSmallerOfTwoArcs(final Circle circle, final Point pointA, final Point pointB) {
		Arc tempArc = new Arc(circle, pointA, pointB);
		Arc tempArcTwo = new Arc(circle, pointB, pointA);
		if (tempArc.getAngleExtent() < tempArcTwo.getAngleExtent()) {
			return tempArc;
		}
		return tempArcTwo;
	}

	/**
	 * @return the end angle of the arc
	 */
	public double getAngleEnd() {
		return (getAngleStart() + getAngleExtent());
	}

	/**
	 * @param otherArc
	 * @return true iff this arc is connected at an endpoint to the other arc
	 */
	public boolean isConnectedAtEndPoint(final Arc otherArc) {
		if (otherArc == null) {
			return false;
		}

		return (this.getStartPoint().equals(otherArc.getStartPoint())
				|| this.getStartPoint().equals(otherArc.getEndPoint())
				|| this.getEndPoint().equals(otherArc.getStartPoint())
				|| this.getEndPoint().equals(otherArc.getEndPoint()));
	}

	/**
	 * @return the start point of the arc
	 */
	public Point getStartPoint() {
		return new Point(super.getStartPoint());
	}

	/**
	 * @return the end point of the arc
	 */
	public Point getEndPoint() {
		return new Point(super.getEndPoint());
	}

	/**
	 * @return the mid point of the arc
	 */
	public Point getMidPoint() {
		double halfOfAngleExtent = getAngleExtent() / 2.0;
		Arc arc = new Arc(getCircle(), getAngleStart360() + halfOfAngleExtent, halfOfAngleExtent);
		return arc.getStartPoint();
	}

	public List<Point> getSomePoints() {
		List<Point> points = new ArrayList<Point>();
		double incrementAngle = getAngleExtent() / 50.0;
		double currentAngle = getAngleStart360() + incrementAngle;
		for(int i =0; i < 50;i++) {
			Arc arc = new Arc(getCircle(), currentAngle, getAngleExtent() - currentAngle);
			points.add(new Point(arc.getStartPoint()));
			currentAngle += incrementAngle;
		}
		return points;
	}

	/**
	 * @return the supporting circle for this arc
	 */
	public Circle getCircle() {
		return circle;
	}

	/**
	 * Determine the intersection points and the INTERSECTION_TYPE of this arc's intersection with otherArc
	 *
	 * @param otherArc
	 * @param intersectionPointOne this point's coordinates will be set if the intersection type is one or two points
	 * @param intersectionPointTwo this point's coordintaes will be set if the intersection type is two points
	 * @return one of: DO_NOT_INTERSECT, INTERSECTS_AT_ONE_POINT, INTERSECTS_AT_TWO_POINTS
	 */
	public INTERSECTION_TYPE determineIntersectionPoints(final Arc otherArc, final Point intersectionPointOne,
																											 final Point intersectionPointTwo) {
		if (otherArc != null && intersectionPointOne != null && intersectionPointTwo != null) {
			Point circleIntersectionPointA = new Point();
			Point circleIntersectionPointB = new Point();
			if (circle.intersects(otherArc.getCircle())) {
				circle.determineIntersectionPointsCoordinates(otherArc.getCircle(), circleIntersectionPointA,
						circleIntersectionPointB);
				int numberOfIntersectingPoints = 0;
				if (this.pointIsOnArc(circleIntersectionPointA) && otherArc.pointIsOnArc(circleIntersectionPointA)) {
					intersectionPointOne.setX(circleIntersectionPointA.getX());
					intersectionPointOne.setY(circleIntersectionPointA.getY());
					numberOfIntersectingPoints++;
				}
				if (this.pointIsOnArc(circleIntersectionPointB) && otherArc.pointIsOnArc(circleIntersectionPointB)) {
					if (numberOfIntersectingPoints == 0) {
						intersectionPointOne.setX(circleIntersectionPointB.getX());
						intersectionPointOne.setY(circleIntersectionPointB.getY());
					}
					else {
						intersectionPointTwo.setX(circleIntersectionPointB.getX());
						intersectionPointTwo.setY(circleIntersectionPointB.getY());
					}
					numberOfIntersectingPoints++;
				}
				if (numberOfIntersectingPoints == 1) {
					return INTERSECTION_TYPE.INTERSECTS_AT_ONE_POINT;
				}
				else if (numberOfIntersectingPoints == 2) {
					return INTERSECTION_TYPE.INTERSECTS_AT_TWO_POINTS;
				}
			}
		}
		return INTERSECTION_TYPE.DO_NOT_INTERSECT;
	}

	/**
	 * The start angle of this arc from 0 to 360 degrees
	 *
	 * @return
	 */
	public double getAngleStart360() {
		return getAngle360(getAngleStart());
	}

	/**
	 * The end angle of this arc from 0 to 360 degrees
	 *
	 * @return
	 */
	public double getAngleEnd360() {
		double angle = getAngleStart360() + getAngle360(getAngleExtent());
		while (angle > 360.0) {
			angle -= 360.0;
		}
		return angle;
	}

	/**
	 * @param angle
	 * @return true iff angle falls between the start and end angle of this arc
	 */
	private boolean isBetweenStartAndEndAngle(final double angle) {
		double startAngle = getAngleStart360();
		double endAngle = getAngleEnd360();
		return ((startAngle == angle || endAngle == angle) ||
				(startAngle < endAngle && angle > startAngle && angle < endAngle) ||
				(endAngle < startAngle && (angle > startAngle || angle < endAngle)));
	}

	/**
	 * @param angle
	 * @return a conversion of the given angle from a possibly negative degree representation
	 */
	double getAngle360(final double angle) {
		if (angle >= 0.0) {
			return angle;
		}
		else {
			return 180.0 + (180.0 - Math.abs(angle));
		}
	}

	/**
	 * @param point
	 * @return true iff the point is on the arc
	 */
	public boolean pointIsOnArc(final Point point) {
		if (getStartPoint().equals(point) || getEndPoint().equals(point)) {
			return true;
		}
		double difference = Math.abs(getCircle().getCenter().distanceSq(point) - Math.pow(getCircle().getRadius(), 2.0));
		if (difference <= 0.5) {
			double ah = Math.abs(point.getX() - getCircle().getCenterX()) / getCircle().getRadius();
			double angle = Math.toDegrees(Math.acos(ah));
			if (point.getX() < getCircle().getCenterX() &&
					point.getY() < getCircle().getCenterY()) {
				angle = 180.0 - angle;
			}
			else if (point.getX() < getCircle().getCenterX() &&
					point.getY() >= getCircle().getCenterY()) {
				angle += 180.0;
			}
			else if (point.getX() > getCircle().getCenterX() &&
					point.getY() >= getCircle().getCenterY()) {
				angle = 360.0 - angle;
			}
			return isBetweenStartAndEndAngle(angle);
		}
		return false;
	}

	/**
	 * @param point
	 * @return true iff the point falls within the arc's range
	 */
	public boolean containsPoint(final Point point) {
		return this.contains(point);
	}

	/**
	 * @return true if this arc is a complete circle
	 */
	public boolean isCircle() {
		return getAngleExtent() >= 360.0;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("circleA = new Circle(");
		sb.append(getCircle().getX());
		sb.append(", ");
		sb.append(getCircle().getY());
		sb.append(", ");
		sb.append(getCircle().getRadius());
		sb.append(");\n");
		sb.append("arc = new Arc(circleA, ");
		sb.append(getAngleStart());
		sb.append(", ");
		sb.append(getAngleExtent());
		sb.append(");\n");
		sb.append("arcList.add(arc);\n");

//		StringBuilder sb = new StringBuilder("Circle: (");
//		sb.append(this.);
//		sb.append(",");
//		sb.append(getCircle().getY());
//		sb.append(") Start Angle: ");
//		sb.append(this.getAngleStart());
//		sb.append(", Extent Angle: ");
//		sb.append(this.getAngleExtent());
//		sb.append(", Start Point: ");
//		sb.append(this.getStartPoint());
//		sb.append(", End Point: ");
//		sb.append(this.getEndPoint());
		return sb.toString();
	}

	@Override
	public boolean equals(Object otherArc) {
		if (otherArc != null && otherArc instanceof Arc) {
			Arc arc = (Arc) otherArc;
			return (this.getX() == arc.getX() && this.getY() == arc.getY()
					&& this.getAngleExtent() == arc.getAngleExtent()
					&& this.getAngleStart() == arc.getAngleStart());
		}
		return false;
	}

	// **** methods requested by Gewali ****

	public int getNumberOfOrthogonalRays() {
		double angleStart = this.getAngleStart();
		double angleEnd = this.getAngleStart() + this.getAngleExtent();
		int numberOfOrthogonalRays = 0;
		if (angleStart <= 90.0 && angleEnd >= 90.0) {
			//include east ray
			numberOfOrthogonalRays++;
		}
		if (angleStart <= 180.0 && angleEnd >= 180.0) {
			//include north ray
			numberOfOrthogonalRays++;
		}
		if (angleStart <= 270.0 && angleEnd >= 270.0) {
			//include west ray
			numberOfOrthogonalRays++;
		}
		if (angleStart <= 360.0 && angleEnd >= 360.0) {
			//include south ray
			numberOfOrthogonalRays++;
		}
		return numberOfOrthogonalRays;
	}

	/**
	 * The quadrant of the point where this arc starts
	 *
	 * @return one of I, II, III, IV
	 */
	public QUADRANT getStartQuadrant() {
		double angleStart = this.getAngleStart();
		return getQuadrant(angleStart);
	}

	/**
	 * The quadrant of the point where this arc ends
	 *
	 * @return one of I, II, III, IV
	 */
	public QUADRANT getEndQuadrant() {
		double angleEnd = (this.getAngleStart() + this.getAngleExtent()) % 360.0;
		return getQuadrant(angleEnd);
	}

	/**
	 * @param angle
	 * @return the quadrant that the specified angle falls in
	 */
	QUADRANT getQuadrant(final double angle) {
		if (angle >= 0.0 && angle < 90.0) {
			return QUADRANT.I;
		}
		else if (angle >= 90.0 && angle < 180.0) {
			return QUADRANT.II;
		}
		else if (angle >= 180.0 && angle < 270.0) {
			return QUADRANT.III;
		}
		else if (angle >= 270.0 && angle <= 360.0) {
			return QUADRANT.IV;
		}
		//default to first quadradnt
		return QUADRANT.I;
	}
}
