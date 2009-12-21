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
