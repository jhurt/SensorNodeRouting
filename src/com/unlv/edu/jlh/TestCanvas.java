package com.unlv.edu.jlh;

import com.unlv.edu.jlh.geom.Point;
import com.unlv.edu.jlh.geom.Arc;
import com.unlv.edu.jlh.geom.Circle;
import com.unlv.edu.jlh.util.SwingGuiUtils;

import javax.swing.*;
import java.util.Vector;
import java.util.List;
import java.awt.*;

public class TestCanvas extends JFrame {
	private final DrawingPanel drawingPanel = new DrawingPanel();

	private final List<Point> pointList;
	private final List<Arc> arcList;

	public static void main(String[] args) {
		new TestCanvas().showGUI();
	}
	
	public TestCanvas() {
		pointList=new Vector<Point>();
		arcList = new Vector<Arc>();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void showGUI() {
		Circle circleA;
		Arc arc;
		circleA = new Circle(262.0, 186.0, 113.0);
		arc = new Arc(circleA, -19.57039335098838, 12.820814629012279);
		arcList.add(arc);
		circleA = new Circle(288.0, 286.0, 113.0);
		arc = new Arc(circleA, 0.0, 360.0);
		arcList.add(arc);
		circleA = new Circle(454.0, 150.0, 113.0);
		arc = new Arc(circleA, -139.19029609670136, 93.58531224084142);
		arcList.add(arc);
	  

		drawingPanel.setBackground(Color.WHITE);
		getContentPane().add(drawingPanel);
		SwingGuiUtils.setSizeBasedOnResolution(this);
		this.setLocation(this.getX() + 20, this.getY() + 20);
		this.setVisible(true);
	}

	class DrawingPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		DrawingPanel() {
			super();
		}

		private void renderPoints(final Graphics2D graphics2D) {
			int i = 0;
			for (Point point : pointList) {
				StringBuilder sb = new StringBuilder("P");
				sb.append(i++);
				graphics2D.drawString(sb.toString(), point.getXAsInt() - 5, point.getYAsInt() - 5);
				drawPoint(point, graphics2D);
			}
		}

		private void drawPoint(final Point point, final Graphics2D graphics2D) {
			graphics2D.drawOval(point.getXAsInt() - 4, point.getYAsInt() - 4, 8, 8);
		}

		private void drawPoints(final List<Point> points, final Graphics2D graphics2D) {
		for(Point point : points) {
			drawPoint(point, graphics2D);
		}

		}


//		private void renderRangeCircles(final Graphics2D graphics2D) {
//			if (pointList.size() > 0) {
//				graphics2D.setColor(Color.LIGHT_GRAY);
//				/** draw the range circles **/
//				for (Point point : pointList) {
//					Circle circle = new Circle(point.getX() - currentRange, point.getY() - currentRange, currentRange);
//					graphics2D.draw(circle);
//				}
//			}
//		}

		private void renderArcs(final Graphics2D graphics2D) {
			graphics2D.setColor(Color.RED);
			for (Arc arc : arcList) {
				arc.setArcType(Arc.OPEN);
				graphics2D.draw(arc);
				graphics2D.setColor(Color.BLUE);
				drawPoints(arc.getSomePoints(), graphics2D);
				//graphics2D.draw(arc.getBounds2D());
			}
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D graphics2D = (Graphics2D) g;
			renderPoints(graphics2D);
			//renderRangeCircles(graphics2D);
			renderArcs(graphics2D);
			graphics2D.dispose();
		}
	}

}
