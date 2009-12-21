/*
Copyright (c) 2009, University of Nevada, Las Vegas
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
    * Neither the name of the University of Nevada, Las Vegas, nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
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
