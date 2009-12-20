package com.unlv.edu.jlh;

import com.unlv.edu.jlh.geom.Point;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class PointTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -2464578403535101213L;
	private final List<Point> pointList;

	public PointTableModel(final List<Point> pointList) {
		this.pointList = pointList;
	}

	public String getColumnName(int column) {
		switch (column) {
			case 0:
				return "Label";
			case 1:
				return "X";
			case 2:
				return "Y";
			default:
				return "";
		}
	}

	public int getRowCount() {
		return pointList.size();
	}

	public int getColumnCount() {
		return 3;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex < 0 || rowIndex >= pointList.size()) {
			return null;
		}
		switch (columnIndex) {
			case 0:
				StringBuilder name = new StringBuilder("N");
				name.append(rowIndex);
				return name.toString();
			case 1:
				return pointList.get(rowIndex).x;
			case 2:
				return pointList.get(rowIndex).y;
		}
		return null;
	}
}
