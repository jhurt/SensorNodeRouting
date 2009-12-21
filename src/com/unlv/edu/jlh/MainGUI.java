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

import com.unlv.edu.jlh.geom.Circle;
import com.unlv.edu.jlh.geom.Point;
import com.unlv.edu.jlh.util.ListUtils;
import com.unlv.edu.jlh.util.SwingGuiUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class MainGUI extends JFrame {
	private static final long serialVersionUID = 1L;
	private final static int MAX_WIRELESS_RANGE = 600;

	public enum Action {
		ADD_NODES, MOVE_ALL_NODES, DELETE_NODE, MOVE_SINGLE_NODE, COMPUTE_FREE_REGION
	}

	private final JTable pointTable = new JTable();
	private final PointTableModel tableModel;
	private final JPopupMenu rightClickPopupMenu = new JPopupMenu();
	private final DrawingPanel drawingPanel = new DrawingPanel();
	private final JCheckBox showConnectionsCheckBox = new JCheckBox("Show Connectivity");
	private final JCheckBox showRangeCheckBox = new JCheckBox("Show Range");
	private final JSlider rangeSlider = new JSlider(0, MAX_WIRELESS_RANGE);

	private final List<Point> pointList;
	private final List<Line2D> connectivityList;
	private final MainGUI mainGUI;

	/** mutable state **/
	private Action selectedAction = Action.ADD_NODES; //stores the currently selected action
	private int currentRange = 300;
	private Point nearestPoint;
	private Color color = Color.BLACK;

	public static void main(String[] args) {
		//Schedule a job for the event-dispatching thread:
		//creating and showing this application's GUI.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	/**
	 * Create the GUI and show it.  For thread safety,
	 * this method should be invoked from the
	 * event-dispatching thread.
	 */
	public static void createAndShowGUI() {
		MainGUI gui = new MainGUI();
		gui.initSwingComponents();
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SwingGuiUtils.setSizeBasedOnResolution(gui);
		gui.setLocation(1450, 10);
		gui.setTitle("Sensor Node Routing");
		gui.setVisible(true);
	}

	public MainGUI() {
		super();
		pointList = new Vector<Point>();
		connectivityList = new Vector<Line2D>();
		tableModel = new PointTableModel(pointList);
		mainGUI = this;
	}

	private void buildConnectivityList() {
		connectivityList.clear();
		int currentRangeSq = currentRange * currentRange;
		for (int i = 0; i < pointList.size(); i++) {
			Point iPoint = pointList.get(i);
			for (int j = i + 1; j < pointList.size(); j++) {
				Point jPoint = pointList.get(j);
				if (iPoint.distanceSq(jPoint) <= currentRangeSq) {
					connectivityList.add(new Line2D.Double(iPoint.getX(), iPoint.getY(), jPoint.getX(), jPoint.getY()));
				}
			}
		}
	}

	private void initSwingComponents() {
		JPanel lowerLeftPanel = new JPanel();
		lowerLeftPanel.setBackground(Color.WHITE);
		lowerLeftPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));

		/** range check box **/
		showRangeCheckBox.setBackground(Color.WHITE);
		showRangeCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				repaint();
			}
		});
		showRangeCheckBox.setSelected(true);
		lowerLeftPanel.add(showRangeCheckBox);

		/** connectivity check box **/
		showConnectionsCheckBox.setBackground(Color.WHITE);
		showConnectionsCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				repaint();
			}
		});
		showConnectionsCheckBox.setSelected(false);
		lowerLeftPanel.add(showConnectionsCheckBox);

		/** range slider **/
		rangeSlider.setPaintTicks(true);
		rangeSlider.setPaintLabels(true);
		rangeSlider.setMajorTickSpacing(100);
		//rangeSlider.setMinorTickSpacing(50);
		rangeSlider.setValue(currentRange);
		rangeSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				currentRange = source.getValue();
				buildConnectivityList();
				repaint();
			}
		});
		rangeSlider.setBackground(Color.WHITE);
		lowerLeftPanel.add(new JLabel("Wireless Range: "));
		lowerLeftPanel.add(rangeSlider);

		/** file menu **/
		JMenuBar menubar = new JMenuBar();
		setJMenuBar(menubar);
		JMenu filemenu = new JMenu("File");
		JMenuItem newGraphMenuItem = new JMenuItem("New");
		newGraphMenuItem.addActionListener(new DeleteNodeListener());
		JMenuItem openGraphMenuItem = new JMenuItem("Open Graph");
		openGraphMenuItem.addActionListener(new OpenGraphListener());
		JMenuItem saveGraphMenuItem = new JMenuItem("Save Graph");
		saveGraphMenuItem.addActionListener(new SaveGraphListener());
		JMenuItem exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		filemenu.add(newGraphMenuItem);
		filemenu.addSeparator();
		filemenu.add(openGraphMenuItem);
		filemenu.add(saveGraphMenuItem);
		filemenu.addSeparator();
		filemenu.add(exitMenuItem);
		menubar.add(filemenu);

		/** options menu **/
		JMenu optionsMenu = new JMenu("Options");
		JMenuItem pickColorMenuItem = new JMenuItem("Pick Color");
		pickColorMenuItem.addActionListener(new PickColorListener());
		optionsMenu.add(pickColorMenuItem);
		menubar.add(optionsMenu);

		/** right-click menu **/
		final JMenuItem addNodeMenuItem = new JCheckBoxMenuItem("Add Nodes");
		addNodeMenuItem.setSelected(true);
		final JMenuItem moveGraphMenuItem = new JCheckBoxMenuItem("Move All Nodes");
		final JMenuItem deleteNodeMenuItem = new JCheckBoxMenuItem("Delete Node");
		final JMenuItem moveNodeItem = new JCheckBoxMenuItem("Move Single Node");
		final JMenuItem computeFreeRegionItem = new JCheckBoxMenuItem("Compute Free Region");

		addNodeMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectedAction = Action.ADD_NODES;
				addNodeMenuItem.setSelected(true);
				moveGraphMenuItem.setSelected(false);
				deleteNodeMenuItem.setSelected(false);
				moveNodeItem.setSelected(false);
				computeFreeRegionItem.setSelected(false);
			}
		});
		moveGraphMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectedAction = Action.MOVE_ALL_NODES;
				moveGraphMenuItem.setSelected(true);
				addNodeMenuItem.setSelected(false);
				deleteNodeMenuItem.setSelected(false);
				moveNodeItem.setSelected(false);
				computeFreeRegionItem.setSelected(false);
			}
		});
		deleteNodeMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectedAction = Action.DELETE_NODE;
				deleteNodeMenuItem.setSelected(true);
				addNodeMenuItem.setSelected(false);
				moveGraphMenuItem.setSelected(false);
				moveNodeItem.setSelected(false);
				computeFreeRegionItem.setSelected(false);
			}
		});
		moveNodeItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectedAction = Action.MOVE_SINGLE_NODE;
				deleteNodeMenuItem.setSelected(false);
				addNodeMenuItem.setSelected(false);
				moveGraphMenuItem.setSelected(false);
				moveNodeItem.setSelected(true);
				computeFreeRegionItem.setSelected(false);
			}
		});
		computeFreeRegionItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectedAction = Action.COMPUTE_FREE_REGION;
				deleteNodeMenuItem.setSelected(false);
				addNodeMenuItem.setSelected(false);
				moveGraphMenuItem.setSelected(false);
				moveNodeItem.setSelected(false);
				computeFreeRegionItem.setSelected(true);
			}
		});

		rightClickPopupMenu.add(addNodeMenuItem);
		rightClickPopupMenu.add(moveGraphMenuItem);
		rightClickPopupMenu.add(deleteNodeMenuItem);
		rightClickPopupMenu.add(moveNodeItem);
		rightClickPopupMenu.add(computeFreeRegionItem);

		/** left panel (drawing area & move buttons) **/
		JPanel leftPanel = new JPanel();
		leftPanel.setBackground(Color.WHITE);
		leftPanel.setLayout(new BorderLayout());
		drawingPanel.setBackground(Color.WHITE);
		leftPanel.add(drawingPanel, BorderLayout.CENTER);
		leftPanel.add(lowerLeftPanel, BorderLayout.SOUTH);

		/** point table **/
		pointTable.setModel(tableModel);
		/** put the table in a scroll pane in case there are many rows in the table **/
		JScrollPane tableScrollPane = new JScrollPane(pointTable);

		/** split pane **/
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, tableScrollPane);
		splitPane.setContinuousLayout(true);
		splitPane.setOneTouchExpandable(true);
		SwingGuiUtils.setSizeBasedOnResolution(splitPane);
		splitPane.setDividerLocation(0.85);

		getContentPane().add(splitPane);
	}

	public void updatePointTable() {
		/** fire a table data changed event for the underlying table model **/
		tableModel.fireTableDataChanged();
	}

	private class PickColorListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			color = JColorChooser.showDialog(mainGUI, "Choose Nodes Color", color);
		}
	}

	private class DeleteNodeListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			new Thread("DeleteNodeThread") {
				public void run() {
					if (pointList.size() > 0) {
						pointList.clear();
						updatePointTable();
						buildConnectivityList();
						repaint();
					}
				}
			}.start();
		}
	}

	private class OpenGraphListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			new Thread("OpenGraphThread") {
				public void run() {
					JFileChooser jfileChooser = new JFileChooser();
					jfileChooser.setFileFilter(new FileFilterGraph());
					if (JFileChooser.APPROVE_OPTION == jfileChooser.showOpenDialog(mainGUI)) {
						File openFile = jfileChooser.getSelectedFile();
						if (openFile != null) {
							ObjectInputStream objectInputStream = null;
							try {
								objectInputStream = new ObjectInputStream(new FileInputStream(openFile));
								HashMap saveMap = (HashMap) objectInputStream.readObject();
								List<Point> tempPointList = (List<Point>) saveMap.get("points");
								ListUtils.copyList(tempPointList, pointList);
								currentRange = (Integer) saveMap.get("range");
								rangeSlider.setValue(currentRange);
								updatePointTable();
								buildConnectivityList();
								repaint();
							}
							catch (IOException ioe) {
								System.out.println(ioe.getMessage());
							}
							catch (ClassNotFoundException cnfe) {
								System.out.println(cnfe.getMessage());
							}
							finally {
								if (objectInputStream != null) {
									try {
										objectInputStream.close();
									}
									catch (IOException ioe) {
										//no op
									}
								}
							}
						}
					}
				}
			}.start();
		}
	}

	private class SaveGraphListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			new Thread("SaveGraphThread") {
				public void run() {
					if(pointList.size() < 1) {
						JOptionPane.showMessageDialog(mainGUI, "Nothing To Save Genius!", "User Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					JFileChooser jfileChooser = new JFileChooser();
					jfileChooser.setFileFilter(new FileFilterGraph());
					if (JFileChooser.APPROVE_OPTION == jfileChooser.showSaveDialog(mainGUI)) {
						File saveFile = jfileChooser.getSelectedFile();
						if (saveFile != null) {
							if (!saveFile.getName().endsWith(FileFilterGraph.FILE_EXTENSION)) {
								saveFile = new File(saveFile.getAbsolutePath() + FileFilterGraph.FILE_EXTENSION);
							}
							HashMap saveMap = new HashMap();
							saveMap.put("range", currentRange);
							saveMap.put("points", pointList);
							ObjectOutputStream objectOutputStream = null;
							try {
								objectOutputStream = new ObjectOutputStream(new FileOutputStream(saveFile));
								objectOutputStream.writeObject(saveMap);
							}
							catch (IOException ioe) {
								System.out.println(ioe.getMessage());
							}
							finally {
								if (objectOutputStream != null) {
									try {
										objectOutputStream.close();
									}
									catch (IOException ioe) {
										//no op
									}
								}
							}
						}
					}
				}
			}.start();
		}
	}

	class DrawingPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private List<Point> mouseDragPointList;
		protected int cursorX, cursorY;

		DrawingPanel() {
			super();
			mouseDragPointList = new Vector<Point>();
			addMouseListener(new MouseHandler());
			addMouseMotionListener(new MouseMotionHandler());
		}

		private void renderPoints(final Graphics2D graphics2D) {
			int i = 0;
			synchronized (pointList) {
				for (Point point : pointList) {
					StringBuilder sb = new StringBuilder("N");
					sb.append(i++);
					graphics2D.drawString(sb.toString(), point.getXAsInt() - 5, point.getYAsInt() - 5);
					drawPoint(point, graphics2D);
				}
			}
		}

		private void drawPoint(final Point point, final Graphics2D graphics2D) {
			graphics2D.drawOval(point.getXAsInt() - 4, point.getYAsInt() - 4, 8, 8);
		}

		private void renderConnectivityLines(final Graphics2D graphics2D) {
			if (showConnectionsCheckBox.isSelected()) {
				for (Line2D line : connectivityList) {
					graphics2D.draw(line);
				}
			}
		}

		private void renderRangeCircles(final Graphics2D graphics2D) {
			if (showRangeCheckBox.isSelected()) {
				/** TODO: We are currently only implementing this for 2 Nodes, and once that works
				 * we need to abstract the algorithm and change it to work for N Nodes, where N > 1
				 */
				if (pointList.size() > 0) {
					graphics2D.setColor(Color.LIGHT_GRAY);
					/** draw the range circles **/
					synchronized (pointList) {
						for (Point point : pointList) {
							Circle circle = new Circle(point.getX() - currentRange, point.getY() - currentRange, currentRange);
							graphics2D.draw(circle);
						}
					}
				}
			}
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D graphics2D = (Graphics2D) g;
			graphics2D.setColor(color);
			graphics2D.drawString("(" + cursorX + "," + cursorY + ")", 10, 20);

			renderPoints(graphics2D);
			renderConnectivityLines(graphics2D);
			renderRangeCircles(graphics2D);

			graphics2D.dispose();
		}

		private void drawCircle(final Point point, final int radius, final Graphics g) {
			g.drawOval(point.getXAsInt() - radius, point.getYAsInt() - radius, radius * 2, radius * 2);
		}

		/**
		 * @param point
		 * @return The index in the point list of the point closest to the passed in point
		 */
		public int getIndexOfClosestPoint(final Point point) {
			int minIndex = 0;
			for (int i = 1; i < pointList.size(); i++) {
				if (pointList.get(i).isCloser(pointList.get(minIndex), point)) {
					minIndex = i;
				}
			}
			return minIndex;
		}


		/**
		 * replace the ith point of the point list with the point specified by p1
		 *
		 * @param p1
		 * @param index
		 */
		public void replaceIthPoint(final Point p1, final int index) {
			if (index < pointList.size()) {
				pointList.set(index, p1);
			}
		}

		/**
		 * offset the entire graph about the X axis
		 *
		 * @param offset specifies the amount of pixels to offset the graph by,
		 *               a negative value will offset the graph in the negative x direction
		 */
		public void offsetGraphXDirection(final int offset) {
			for (Point point : pointList) {
				point.setLocation(point.x + offset, point.y);
			}
		}

		/**
		 * offset the entire graph about the Y axis
		 *
		 * @param offset specifies the amount of pixels to offset the graph by,
		 *               a negative value will offset the graph in the negative y direction
		 */
		public void offsetGraphYDirection(final int offset) {
			for (Point point : pointList) {
				point.setLocation(point.x, point.y + offset);
			}
		}


		private class MouseHandler extends MouseAdapter {
			/**
			 * Fired when a mouse button is clicked
			 *
			 * @param e
			 */
			public void mousePressed(MouseEvent e) {
				/** handle a left click **/
			
				if (e.getButton() == MouseEvent.BUTTON1) {
					switch (selectedAction) {
						case ADD_NODES:
							handleDrawNodesEvent(e);
							break;
						case DELETE_NODE:
							handleDeleteNodeEvent(e);
							break;
						case COMPUTE_FREE_REGION:
							handleComputeFreeRegionEvent(e);
							break;
						default:
							//no op
							break;
					}
				}
				/** handle a right click **/
				else if (e.getButton() == MouseEvent.BUTTON3) {
					rightClickPopupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		}

		private class MouseMotionHandler extends MouseMotionAdapter {

			/**
			 * Fired once before a sequence of 0 or more mouse drag events
			 *
			 * @param e
			 */
			public void mouseMoved(MouseEvent e) {
				cursorX = e.getX();
				cursorY = e.getY();
				/** copy current point list so that we can use them
				 *  as reference points in the mouse dragged event.
				 */
				Point clickPoint = new Point(cursorX, cursorY);
				int index = getIndexOfClosestPoint(clickPoint);
				if (index >= 0 && index < pointList.size()) {
					nearestPoint = pointList.get(getIndexOfClosestPoint(clickPoint));
				}
				if (clickPoint != null && nearestPoint != null && clickPoint.distanceSq(nearestPoint) > 16.0) {
					nearestPoint = null;
				}
				ListUtils.copyList(pointList, mouseDragPointList);
				repaint();
			}

			/**
			 * Fired many times during a mouse drag
			 *
			 * @param e
			 */
			public void mouseDragged(MouseEvent e) {
				switch (selectedAction) {
					case MOVE_ALL_NODES:
						handleMoveGraphEvent(e);
						break;
					case MOVE_SINGLE_NODE:
						handleMoveNodeEvent(e);
						break;
					default:
						//no op
						break;
				}
			}
		}

		//NOTE: the event handlers all spawn new threads so that the
		//AWT Event Dispatch thread can continue processing events
		private void handleDrawNodesEvent(final MouseEvent e) {
			new Thread("DrawNodeThread") {
				public void run() {
					pointList.add(new Point(e.getX(), e.getY()));
					updatePointTable();
					buildConnectivityList();
					repaint();
				}
			}.start();
		}

		private void handleComputeFreeRegionEvent(final MouseEvent e) {
			new Thread("ComputeFreeRegionThread") {
				public void run() {
					if (nearestPoint != null) {
						new ShowArcIntersectionFrame(pointList, currentRange, nearestPoint).buildArcListAndShowGUI();
					}
				}
			}.start();
		}

		private void handleMoveGraphEvent(final MouseEvent e) {
			new Thread("MoveGraphThread") {
				public void run() {
					int xOffset = e.getX() - cursorX;
					int yOffset = e.getY() - cursorY;
					for (int i = 0; i < mouseDragPointList.size(); i++) {
						Point referencePoint = mouseDragPointList.get(i);
						Point correspondingPoint = pointList.get(i);
						correspondingPoint.setLocation(referencePoint.x + xOffset, referencePoint.y + yOffset);
					}
					updatePointTable();
					buildConnectivityList();
					repaint();
				}
			}.start();
		}

		private void handleDeleteNodeEvent(final MouseEvent e) {
			new Thread("DeleteNodeThread") {
				public void run() {
					Point clickPoint = new Point(e.getX(), e.getY());
					int index = getIndexOfClosestPoint(clickPoint);
					if (index >= 0 && index < pointList.size()) {
						Point nearestPoint = pointList.get(getIndexOfClosestPoint(clickPoint));
						if (clickPoint.distanceSq(nearestPoint) <= 16.0) {
							pointList.remove(nearestPoint);
							updatePointTable();
							buildConnectivityList();
							repaint();
						}
					}
				}
			}.start();
		}

		private void handleMoveNodeEvent(final MouseEvent e) {
			new Thread("MoveSingleNodeThread") {
				public void run() {
					if (nearestPoint != null) {
						nearestPoint.setX(e.getX());
						nearestPoint.setY(e.getY());
						updatePointTable();
						buildConnectivityList();
						repaint();
					}
				}
			}.start();
		}
	}
}
