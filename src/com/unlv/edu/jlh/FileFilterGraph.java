package com.unlv.edu.jlh;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class FileFilterGraph extends FileFilter {

	public static final String FILE_EXTENSION = ".graph";

	public boolean accept(File f) {
		return (f.isDirectory() || f.getName().endsWith(FILE_EXTENSION));
	}

	public String getDescription() {
		return "Sensor Node Graph Files";
	}
}