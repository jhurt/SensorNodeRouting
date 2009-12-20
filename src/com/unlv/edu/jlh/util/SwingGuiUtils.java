package com.unlv.edu.jlh.util;

import javax.swing.*;
import java.awt.*;

/**
 * Utility methods for Swing
 */
public class SwingGuiUtils {

	/**
	 * Set the size of the JComponent based on the resolution of the screen
	 *
	 * @param component
	 */
	public static void setSizeBasedOnResolution(final JComponent component) {
		Dimension dimension = getDimensionBasedOnResolution();
		component.setSize(dimension.width, dimension.height);
	}

	/**
	 * Set the size of the JFrame based on the resolution of the screen
	 *
	 * @param frame
	 */
	public static void setSizeBasedOnResolution(final JFrame frame) {
		Dimension dimension = getDimensionBasedOnResolution();
		frame.setSize(dimension.width, dimension.height);
	}

	/**
	 * @return a Dimension object containing the resolution of the first display iff it exists
	 *         800 x 600 will be returned as a default
	 */
	private static Dimension getDimensionBasedOnResolution() {
		int width = 800;
		int height = 600;
		GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] graphicsDevices = graphicsEnvironment.getScreenDevices();
		if (graphicsDevices.length >= 1) {
			DisplayMode displayMode = graphicsDevices[0].getDisplayMode();
			width = displayMode.getWidth()-60;
			height = displayMode.getHeight()-60;
		}
		return new Dimension(width, height);
	}

}
