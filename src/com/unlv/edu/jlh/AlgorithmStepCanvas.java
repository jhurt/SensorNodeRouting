package com.unlv.edu.jlh;

import com.unlv.edu.jlh.geom.Point;
import com.unlv.edu.jlh.geom.Arc;
import com.unlv.edu.jlh.util.SwingGuiUtils;
import com.unlv.edu.jlh.util.ListUtils;

import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.awt.*;

public class AlgorithmStepCanvas extends JFrame {
	private final DrawingPanel drawingPanel = new DrawingPanel();

	private final List<Arc> arcList;
	private final int iStepNumber;

	public AlgorithmStepCanvas(int iStepNumber, int jStepNumber, List<Arc> arcList) {
		this.iStepNumber = iStepNumber;
		this.arcList = new ArrayList<Arc>();
		ListUtils.copyList(arcList, this.arcList);
		this.setTitle("STEP( " + iStepNumber + ", "+jStepNumber+")");
	}

	public void showStep() {
		drawingPanel.setBackground(Color.WHITE);
		getContentPane().add(drawingPanel);
		SwingGuiUtils.setSizeBasedOnResolution(this);
		this.setLocation(this.getX() + (10 * iStepNumber), this.getY() + (10 * iStepNumber));
		this.setVisible(true);
	}

	class DrawingPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		DrawingPanel() {
			super();
		}

		private void drawPoint(final Point point, final Graphics2D graphics2D) {
			graphics2D.drawOval(point.getXAsInt() - 4, point.getYAsInt() - 4, 8, 8);
		}

		private void drawPoints(final List<Point> points, final Graphics2D graphics2D) {
			for (Point point : points) {
				drawPoint(point, graphics2D);
			}
		}

		private void renderArcs(final Graphics2D graphics2D) {
			graphics2D.setColor(Color.BLUE);
			for (Arc arc : arcList) {
				arc.setArcType(Arc.OPEN);
				graphics2D.draw(arc);
			}
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D graphics2D = (Graphics2D) g;
			renderArcs(graphics2D);
			graphics2D.dispose();
		}
	}
}
