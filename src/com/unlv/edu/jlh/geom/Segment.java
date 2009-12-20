package com.unlv.edu.jlh.geom;

public class Segment {
	private final Point sourcePoint;
	private final Point targetPoint;

	public Segment(Point sourcePoint, Point targetPoint) {
		this.sourcePoint = sourcePoint;
		this.targetPoint = targetPoint;
	}

	public double getAngleRadians() {
		return sourcePoint.getAngleRadians(targetPoint);
	}

	public double getAngleDegrees() {
		return getAngleRadians() * (180.0 / Math.PI);
	}

	public double getAngleDegreesWithSegment(final Segment otherSegment) {
		return getAngleRadiansWithSegment(otherSegment) * (180.0 / Math.PI);
	}

	public Point getSourcePoint() {
		return sourcePoint;
	}

	public Point getTargetPoint() {
		return targetPoint;
	}

	public double getAngleRadiansWithSegment(final Segment otherSegment) {
		if (otherSegment == null) {
			return 0.0;
		}
		double a1 = getAngleRadians();
		double a2 = otherSegment.getAngleRadians();
		if (a2 >= a1) {
			return a2 - a1;
		}
		else {
			return 2.0 * Math.PI - (a1 - a2);
		}
	}

	@Override
	public boolean equals(Object otherSegment) {
		if (!(otherSegment instanceof Segment) || otherSegment == null) {
			return false;
		}
		Segment segment = (Segment) otherSegment;
		return (this.getSourcePoint().equals(segment.getSourcePoint())
				&& this.getTargetPoint().equals(segment.getTargetPoint()));
	}

}
