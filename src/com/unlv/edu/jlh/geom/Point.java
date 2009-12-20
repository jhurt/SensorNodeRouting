package com.unlv.edu.jlh.geom;

import java.awt.geom.Point2D;

public class Point extends Point2D.Double {
	public Point() {
		super(0, 0);
	}

  public Point(Point2D point) {
    super(point.getX(), point.getY());
  }

	public Point(int x, int y) {
		super(x, y);
	}

	public Point(double x, double y) {
		super(x, y);
	}

	public int getXAsInt() {
		return (int) x;
	}

	public int getYAsInt() {
		return (int) y;
	}

	public void setX(final double x) {
		super.setLocation(x, this.getY());
	}

	public void setY(final double y) {
		super.setLocation(this.getX(), y);
	}

	/**
	 *
	 * @param otherPoint
	 * @return the euclidean distance of this point to otherPoint
	 */
	public double distance(final Point otherPoint) {
		return this.distance(otherPoint.getX(), otherPoint.getY());
	}

	/**
	 * Angle with the axis made by the segment formed with this as the source point and endPoint as the target point
	 *
	 * @param endPoint
	 * @return
	 */
	public double getAngleRadians(final Point endPoint) {
		if (endPoint == null ||
				(this.getX() == endPoint.getX() && this.getY() == endPoint.getY())) {
			return 0.0;
		}

		double length = this.distance(endPoint);
		double k = (endPoint.getY() - this.getY()) / length;
		//1,4 and 2,3  quadrants are interchanged
		if (k >= 0) {
			if (endPoint.getX() >= this.getX()) {
				// First Quadrant.
				return (2.0 * Math.PI - Math.asin(k));
			}
			// Second Quadrant.
			return (Math.PI + Math.asin(k));
		}
		else {
			if (endPoint.getX() >= this.getX()) {
				// Fourth Quadrant.
				return Math.asin(-k);
			}
			// Third Quadrant.
			return (Math.PI - Math.asin(-k));
		}
	}

	/**
	 * @param a
	 * @param b
	 * @return true iff the distance from this Point to Point b is shorter than the distance from Point a to Point b
	 */
	public boolean isCloser(final Point a, final Point b) {
		if (a == null || b == null) {
			return false;
		}
		return (this.distanceSq(b) < a.distanceSq(b));
	}

	@Override
	public String toString() {
		return ("(" + x + "," + y + ")");
	}

	@Override
	public boolean equals(Object otherPoint) {
		if (otherPoint != null && otherPoint instanceof Point) {
			Point point = (Point) otherPoint;
			return (Math.abs(this.getX() - point.getX()) < 1.5 &&
          Math.abs(this.getY() - point.getY()) < 1.5);
		}
		return false;
	}
}
