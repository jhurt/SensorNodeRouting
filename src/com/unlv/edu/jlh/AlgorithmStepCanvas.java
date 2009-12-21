/*
Copyright (c) 2009, University of Nevada, Las Vegas
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
    * Neither the name of the University of Nevada, Las Vegas, nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
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
