package com.unlv.edu.jlh;

import com.unlv.edu.jlh.util.SwingGuiUtils;
import com.unlv.edu.jlh.util.ListUtils;
import com.unlv.edu.jlh.geom.Point;
import com.unlv.edu.jlh.geom.Circle;
import com.unlv.edu.jlh.geom.Arc;

import java.util.*;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;

public class ShowArcIntersectionFrame extends JFrame {
	private static final boolean DEBUG = false;

	private final List<Point> pointList;
	private final List<Point> originalPointList;
	private final List<Arc> arcListToDraw;
	private final List<Arc> arcListTwo;
	private final Point nodePoint;
	private final double currentRange;
	private final DrawingPanel drawingPanel = new DrawingPanel();
	private final Circle nodeRangeCircle;

	public ShowArcIntersectionFrame(List<Point> pointList, int currentRange) {
		this(pointList, currentRange, null);
	}

	public ShowArcIntersectionFrame(List<Point> pointList, int currentRange, Point selectedPoint) {
		assert currentRange > 0;
		assert selectedPoint != null;
		this.pointList = new Vector<Point>();
		ListUtils.copyList(pointList, this.pointList);
		originalPointList = new ArrayList<Point>();
		ListUtils.copyList(pointList, originalPointList);
		nodePoint = selectedPoint;
		assert pointList.indexOf(nodePoint) > -1;
		setTitle("Outer Free Region For N" + pointList.indexOf(nodePoint));
		this.currentRange = currentRange;
		this.arcListToDraw = new Vector<Arc>();
		this.arcListTwo = new Vector<Arc>();
		this.nodeRangeCircle = new Circle(nodePoint.getX() - currentRange, nodePoint.getY() - currentRange, currentRange);
	}

	public void buildArcListAndShowGUI() {
		Collections.sort(pointList, new ClosestPointComparator(nodePoint));
		buildArcListForOuterFreeRegion();
		System.out.println("Size before filter: " + arcListToDraw.size());
		filterArcList();
		System.out.println("Size after filter: " + arcListToDraw.size());
		drawingPanel.setBackground(Color.WHITE);
		getContentPane().add(drawingPanel);
		SwingGuiUtils.setSizeBasedOnResolution(this);
		this.setLocation(this.getX() + 20, this.getY() + 20);
		this.setVisible(true);
	}

	private void buildArcListForOuterFreeRegion() {
		arcListToDraw.add(new Arc(nodeRangeCircle, 0.0, 360.0));
		for (int i = 0; i < pointList.size(); i++) {
			Point point = pointList.get(i);
			if (!nodePoint.equals(point)) {
				double distanceToNode = nodePoint.distance(point);
				if (distanceToNode < nodeRangeCircle.getRadius() || distanceToNode > (2 * nodeRangeCircle.getRadius())) {
					continue;
				}
				Circle circle = new Circle(point.getX() - currentRange, point.getY() - currentRange, currentRange);
				ListUtils.copyList(arcListToDraw, arcListTwo);
				int j = 0;
				while (!arcListToDraw.isEmpty()) {
					j++;
					Arc arcToConsider = arcListToDraw.remove(0);
					if (arcToConsider.isCircle() && arcListToDraw.size() == 0 &&
							Circle.INTERSECTION_TYPE.INTERSECTS_AT_TWO_POINTS == circle.getIntersectionType(arcToConsider.getCircle())) {
						/** handle the case where the only arc in the list is a
						 * complete circle, not yet intersected with any nodes **/
						handleArcIsCircle(arcToConsider, circle);
						if (DEBUG) {
							new AlgorithmStepCanvas(i + 1, j, arcListTwo).showStep();
						}
					}
					else {
						Point intersectionPointA = new Point();
						Point intersectionPointB = new Point();
						circle.determineIntersectionPointsCoordinates(arcToConsider.getCircle(), intersectionPointA,
								intersectionPointB);
						Arc possiblePartioningArc = new Arc(circle, intersectionPointA, intersectionPointB);
						Arc.INTERSECTION_TYPE intersectionType =
								arcToConsider.determineIntersectionPoints(possiblePartioningArc, intersectionPointA, intersectionPointB);
						if (Arc.INTERSECTION_TYPE.INTERSECTS_AT_TWO_POINTS == intersectionType) {
							handleIntersectsArcAtTwoPoints(arcToConsider, possiblePartioningArc, intersectionPointA, intersectionPointB);
							if (DEBUG) {
								new AlgorithmStepCanvas(i + 1, j, arcListTwo).showStep();
							}
						}
						else if (Arc.INTERSECTION_TYPE.INTERSECTS_AT_ONE_POINT == intersectionType) {
							possiblePartioningArc = new Arc(circle, 0.0, 360.0);
							handleIntersectsArcAtOnePoint2(arcToConsider, possiblePartioningArc, intersectionPointA);
							if (DEBUG) {
								new AlgorithmStepCanvas(i + 1, j, arcListTwo).showStep();
							}
						}
					}
				}
				ListUtils.copyList(arcListTwo, arcListToDraw);
			}
		}
	}


	private void handleArcIsCircle(final Arc arcToConsider, final Circle currentCircle) {
		Point intersectionPointA = new Point();
		Point intersectionPointB = new Point();
		currentCircle.determineIntersectionPointsCoordinates(arcToConsider.getCircle(), intersectionPointA, intersectionPointB);

		Arc firstArc = new Arc(arcToConsider.getCircle(), intersectionPointA, intersectionPointB);
		Arc secondArc = new Arc(currentCircle, intersectionPointA, intersectionPointB);
		if (Arc.INTERSECTION_TYPE.INTERSECTS_AT_TWO_POINTS ==
				secondArc.determineIntersectionPoints(firstArc, intersectionPointA, intersectionPointB)) {
			assert arcListTwo.size() == 1;
			arcListTwo.add(secondArc);
			arcListTwo.add(firstArc);
			boolean removed = arcListTwo.remove(arcToConsider);
			assert removed;
			assert arcListTwo.size() == 2;
		}
	}

	private void handleIntersectsArcAtTwoPoints(final Arc arcToConsider, final Arc partitioningArc,
																							final Point intersectionPointA, final Point intersectionPointB) {
		Arc secondArc = new Arc(arcToConsider.getCircle(), arcToConsider.getStartPoint(), intersectionPointA);
		Arc thirdArc = new Arc(arcToConsider.getCircle(), intersectionPointB, arcToConsider.getEndPoint());
		arcListTwo.remove(arcToConsider);
		arcListToDraw.remove(arcToConsider);
		arcListTwo.add(thirdArc);
		arcListTwo.add(partitioningArc);
		arcListTwo.add(secondArc);
	}


	private void handleIntersectsArcAtOnePoint2(final Arc arcToConsider, final Arc partitioningArc,
																							final Point intersectionPointA) {
		if (arcToConsider.getEndPoint().equals(intersectionPointA) ||
				arcToConsider.getStartPoint().equals(intersectionPointA)) {
			if (partitioningArc.getCircle().containsManyPoints(arcToConsider)) {
				arcListTwo.remove(arcToConsider);
			}
			return;
		}

		Arc arcA = Arc.getSmallerOfTwoArcs(arcToConsider.getCircle(), intersectionPointA, arcToConsider.getStartPoint());
		Arc arcB = Arc.getSmallerOfTwoArcs(arcToConsider.getCircle(), intersectionPointA, arcToConsider.getEndPoint());
		if (partitioningArc.getCircle().containsManyPoints(arcA)) {
			arcListTwo.remove(arcToConsider);
			arcListTwo.add(arcB);
		}
		else if (partitioningArc.getCircle().containsManyPoints(arcB)) {
			arcListTwo.remove(arcToConsider);
			arcListTwo.add(arcA);
		}

		Point intersectionPointC = new Point();
		Point intersectionPointD = new Point();
		List<Arc> arcs = new ArrayList<Arc>();
		ListUtils.copyList(arcListToDraw, arcs);
		while (!arcs.isEmpty()) {
			Arc arc = arcs.remove(0);
			int index = arcListTwo.indexOf(arc);
			if (index == -1) {
				continue;
			}
			Arc.INTERSECTION_TYPE intersectionType =
					arc.determineIntersectionPoints(partitioningArc, intersectionPointC, intersectionPointD);
			if (!arc.equals(arcToConsider) && Arc.INTERSECTION_TYPE.DO_NOT_INTERSECT == intersectionType &&
					/** the arc does not intersect, see if we should throw it out or keep it in the list **/
					partitioningArc.getCircle().containsManyPoints(arc)) {
				arcListTwo.remove(arc);
				arcListToDraw.remove(arc);
			}
			else if (Arc.INTERSECTION_TYPE.INTERSECTS_AT_ONE_POINT == intersectionType) {
				/** the arcs intersect **/
				Arc newPartitioningArc = Arc.getSmallerOfTwoArcs(partitioningArc.getCircle(), intersectionPointA, intersectionPointC);
				Arc arc1 = buildPartialArc(arc, newPartitioningArc, intersectionPointC);
				Arc arc2 = buildPartialArc(arcToConsider, newPartitioningArc, intersectionPointA);
				if (nodeRangeCircle.containsManyPoints(arc1)) {
					arcListTwo.add(arc1);
					arcListTwo.remove(arc);
					arcListToDraw.remove(arc);
				}
				if (nodeRangeCircle.containsManyPoints(newPartitioningArc)) {
					arcListTwo.add(newPartitioningArc);
				}
				if (nodeRangeCircle.containsManyPoints(arc2)) {
					arcListTwo.add(arc2);
					arcListTwo.remove(arcToConsider);
					arcListToDraw.remove(arcToConsider);
				}
			}
		}
	}

	private void filterArcList() {
		if (arcListToDraw.size() <= 1) {
			return;
		}
		//filterUnconnectedArcs();
		//filterArcsNotInACycle();
		filterArcsNotInsideOuterFreeRegion();
		//filterArcsOutsideRange();
	}

	/**
	 * Filter out arcs that are not part of a complete cycle
	 */
	private void filterArcsNotInACycle() {
		/** fill a N x N matrix map with false values **/
		HashMap<Pair, Boolean> arcToAreConnectedMap = new HashMap<Pair, Boolean>();
		for (int i = 0; i < arcListToDraw.size(); i++) {
			Arc arcA = arcListToDraw.get(i);
			for (int j = 0; j < arcListToDraw.size(); j++) {
				Arc arcB = arcListToDraw.get(j);
				Pair pair = new Pair(arcA, arcB);
				arcToAreConnectedMap.put(pair, false);
			}
		}

		/** build the N x N matrix to boolean connectivity value map **/
		for (int i = 0; i < arcListToDraw.size(); i++) {
			Arc arcA = arcListToDraw.get(i);
			for (int j = 0; j < arcListToDraw.size(); j++) {
				Arc arcB = arcListToDraw.get(j);
				if (i != j && arcA.isConnectedAtEndPoint(arcB)) {
					Pair pair = new Pair(arcA, arcB);
					arcToAreConnectedMap.put(pair, true);
					for (int k = 0; k < arcListToDraw.size(); k++) {
						Arc arcC = arcListToDraw.get(k);
						pair = new Pair(arcB, arcC);
						if (arcToAreConnectedMap.get(pair)) {
							pair = new Pair(arcA, arcC);
							arcToAreConnectedMap.put(pair, true);
						}
					}
				}
			}
		}

		/** filter out arcs not in a complete cycle **/
		List<Arc> cyclicArcList = new ArrayList<Arc>();
		for (int i = 0; i < arcListToDraw.size(); i++) {
			Arc arc = arcListToDraw.get(i);
			Pair pair = new Pair(arc, arc);
			if (arcToAreConnectedMap.get(pair)) {
				cyclicArcList.add(arc);
				for (int j = 0; j < arcListToDraw.size(); j++) {
					Arc arcA = arcListToDraw.get(j);
					if (i != j) {
						pair = new Pair(arc, arcA);
						if (arcToAreConnectedMap.get(pair)) {
							cyclicArcList.add(arcA);
						}
					}
				}
			}
		}
		ListUtils.copyList(cyclicArcList, arcListToDraw);
	}

	/**
	 * Filter out arcs that are outside of the node range
	 */
	private void filterArcsOutsideRange() {
		List<Arc> tempArcs = new ArrayList<Arc>();
		ListUtils.copyList(arcListToDraw, tempArcs);
		while (!tempArcs.isEmpty()) {
			Arc arc = tempArcs.remove(0);
			if (!nodeRangeCircle.containsManyPoints(arc)) {
				arcListToDraw.remove(arc);
			}
		}
	}

	/**
	 * Filter out arcs that are not connected to any other arcs
	 */
	private void filterUnconnectedArcs() {
		List<Arc> connectedArcList = new ArrayList<Arc>();
		for (int i = 0; i < arcListToDraw.size(); i++) {
			Arc arcToConsider = arcListToDraw.get(i);
			for (int j = 0; j < arcListToDraw.size(); j++) {
				if (i != j) {
					Arc arc = arcListToDraw.get(j);
					if (arcToConsider.isConnectedAtEndPoint(arc)) {
						connectedArcList.add(arcToConsider);
						break;
					}
				}
			}
		}
		ListUtils.copyList(connectedArcList, arcListToDraw);
	}

	private void filterArcsNotInsideOuterFreeRegion() {
		List<Arc> arcList = new ArrayList<Arc>();
		ListUtils.copyList(arcListToDraw, arcList);
		for (int i = 0; i < arcListToDraw.size(); i++) {
			Arc arc = arcListToDraw.get(i);
			Line2D.Double centerToStart = new Line2D.Double(nodeRangeCircle.getCenter(), arc.getStartPoint());
			Line2D.Double centerToMiddle = new Line2D.Double(nodeRangeCircle.getCenter(), arc.getMidPoint());
			Line2D.Double centerToEnd = new Line2D.Double(nodeRangeCircle.getCenter(), arc.getEndPoint());
			for (int j = 0; j < arcListToDraw.size() && j != i; j++) {
				Arc testArc = arcListToDraw.get(j);
				Line2D.Double arcLine = new Line2D.Double(testArc.getStartPoint(), testArc.getEndPoint());
				if (arcLine.intersectsLine(centerToMiddle) ||
						(!testArc.getStartPoint().equals(arc.getStartPoint()) &&
								!testArc.getEndPoint().equals(arc.getStartPoint()) &&
								arcLine.intersectsLine(centerToStart)) ||
						(!testArc.getStartPoint().equals(arc.getEndPoint()) &&
								!testArc.getEndPoint().equals(arc.getEndPoint()) &&
								arcLine.intersectsLine(centerToEnd))) {
//				if (arcLine.intersectsLine(centerToMiddle) ||
//						(!testArc.isConnectedAtEndPoint(arc) &&
//								arcLine.intersectsLine(centerToStart)) ||
//						(!testArc.isConnectedAtEndPoint(arc) &&
//								arcLine.intersectsLine(centerToEnd))) {
					arcList.remove(arc);
					break;
				}
			}
		}
		ListUtils.copyList(arcList, arcListToDraw);
	}

	private Arc buildPartialArc(final Arc arcToConsider, final Arc partitioningArc, final Point intersectionPointA) {
		double start = intersectionPointA.distanceSq(arcToConsider.getStartPoint());
		double end = intersectionPointA.distanceSq(arcToConsider.getEndPoint());
		if (start < end) {
			Arc arcA = new Arc(arcToConsider.getCircle(), intersectionPointA, arcToConsider.getEndPoint());
			Arc arcB = new Arc(arcToConsider.getCircle(), arcToConsider.getEndPoint(), intersectionPointA);
			return partitioningArc.getCircle().getArcThatIsOutsideMore(arcA, arcB);
		}
		else {
			Arc arcA = new Arc(arcToConsider.getCircle(), arcToConsider.getStartPoint(), intersectionPointA);
			Arc arcB = new Arc(arcToConsider.getCircle(), intersectionPointA, arcToConsider.getStartPoint());
			return partitioningArc.getCircle().getArcThatIsOutsideMore(arcA, arcB);
		}
	}

//	private void handleIntersectsArcAtOnePoint(final Arc arcToConsider, final Arc partitioningArc,
//																						 final Point intersectionPointA) {
//		Point intersectionPointC = new Point();
//		Point intersectionPointD = new Point();
//		List<Arc> arcs = new ArrayList<Arc>();
//		ListUtils.copyList(arcListToDraw, arcs);
//		while (!arcs.isEmpty()) {
//			Arc arc = arcs.remove(0);
//			int index = arcListTwo.indexOf(arc);
//			if (index == -1) {
//				continue;
//			}
//			Arc.INTERSECTION_TYPE intersectionType =
//					arc.determineIntersectionPoints(partitioningArc, intersectionPointC, intersectionPointD);
//			if (Arc.INTERSECTION_TYPE.INTERSECTS_AT_ONE_POINT == intersectionType) {
//				if (!intersectionPointA.equals(intersectionPointC)) {
//					/** the arcs intersect **/
//					Arc newPartitioningArc = Arc.getSmallerOfTwoArcs(partitioningArc.getCircle(), intersectionPointA, intersectionPointC);
//					arcListTwo.add(buildPartialArc(arc, newPartitioningArc, intersectionPointC));
//					arcListTwo.add(newPartitioningArc);
//					arcListTwo.add(buildPartialArc(arcToConsider, newPartitioningArc, intersectionPointA));
//					arcListToDraw.add(buildPartialArc(arc, newPartitioningArc, intersectionPointC));
//					arcListToDraw.add(newPartitioningArc);
//					arcListToDraw.add(buildPartialArc(arcToConsider, newPartitioningArc, intersectionPointA));
//
//					arcListTwo.remove(arcToConsider);
//					arcListTwo.remove(arc);
//					arcListToDraw.remove(arcToConsider);
//					arcListToDraw.remove(arc);
//				}
//				else {
//					if (partitioningArc.getCircle().containsManyPoints(arc)) {
//
//
//					}
//				}
//			}
//			else {
//				if (!arc.equals(arcToConsider) && Arc.INTERSECTION_TYPE.DO_NOT_INTERSECT == intersectionType &&
//						/** the arc does not intersect, see if we should throw it out or keep it in the list **/
//						partitioningArc.getCircle().containsManyPoints(arc)) {
//					arcListTwo.remove(arc);
//					arcListToDraw.remove(arc);
//				}
//			}
//		}
//	}


//private List<Arc> sortByConnectivity(List<Arc> arcs) {
//		if (arcs.size() <= 2) {
//			return arcs;
//		}
//		List<Arc> sortedArcs = new ArrayList<Arc>();
//		sortedArcs.add(arcs.remove(0));
//		while (arcs.size() > 0) {
//			if (sortedArcs.size() < 1) {
//				sortedArcs.add(arcs.remove(0));
//			}
//			Arc tempArc = sortedArcs.get(sortedArcs.size() - 1);
//			for (int i = 0; i < arcs.size(); i++) {
//				Arc arc = arcs.get(i);
//				if (tempArc.isConnectedAtEndPoint(arc)) {
//					arcs.remove(i);
//					sortedArcs.add(arc);
//					break;
//				}
//				else if (i == arcs.size() - 1) {
//					sortedArcs.remove(tempArc);
//					if (arcs.size() > 0) {
//						sortedArcs.add(arcs.remove(0));
//					}
//				}
//			}
//		}
//		return sortedArcs;
//	}


	class ClosestPointComparator implements Comparator<Point> {
		private final Point point;

		public ClosestPointComparator(Point point) {
			this.point = point;
		}

		public int compare(Point o1, Point o2) {
			if (o1 == null || o2 == null) {
				return -1;
			}
			double distanceA = point.distanceSq(o1);
			double distanceB = point.distanceSq(o2);

			if (distanceA < distanceB) {
				return -1;
			}
			if (distanceA > distanceB) {
				return 1;
			}
			return 0;
		}
	}

	class DrawingPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		DrawingPanel() {
			super();
		}

		private void renderPoints(final Graphics2D graphics2D) {
			int i = 0;
			for (Point point : originalPointList) {
				StringBuilder sb = new StringBuilder("N");
				sb.append(i++);
				graphics2D.drawString(sb.toString(), point.getXAsInt() - 5, point.getYAsInt() - 5);
				drawPoint(point, graphics2D);
			}
		}

		private void drawPoint(final Point point, final Graphics2D graphics2D) {
			graphics2D.drawOval(point.getXAsInt() - 4, point.getYAsInt() - 4, 8, 8);
		}

		private void renderRangeCircles(final Graphics2D graphics2D) {
			if (pointList.size() > 0) {
				graphics2D.setColor(Color.LIGHT_GRAY);
				/** draw the range circles **/
				for (Point point : pointList) {
					Circle circle = new Circle(point.getX() - currentRange, point.getY() - currentRange, currentRange);
					graphics2D.draw(circle);
				}
			}
		}

		private void renderArcs(final Graphics2D graphics2D) {
			graphics2D.setColor(Color.RED);
			for (Arc arc : arcListToDraw) {
				arc.setArcType(Arc.OPEN);
				graphics2D.draw(arc);
			}
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D graphics2D = (Graphics2D) g;
			renderPoints(graphics2D);
			renderRangeCircles(graphics2D);
			renderArcs(graphics2D);
			graphics2D.dispose();
		}
	}

	class Pair {
		private final Arc arcA;
		private final Arc arcB;

		public Pair(Arc arcA, Arc arcB) {
			this.arcA = arcA;
			this.arcB = arcB;
		}

		@Override
		public boolean equals(Object o) {
			if (o == null || !(o instanceof Pair)) {
				return false;
			}
			Pair otherPair = (Pair) o;
			return this.getArcA().equals(otherPair.getArcA())
					&& this.getArcB().equals(otherPair.getArcB());
		}

		@Override
		public int hashCode() {
			if (arcA == null) {
				return 0;
			}
			int hash = 1;
			hash = (hash * 31) + arcA.hashCode();
			return hash;
		}

		public Arc getArcA() {
			return arcA;
		}

		public Arc getArcB() {
			return arcB;
		}
	}
}
