package org.cobweb.javafxutil;

import javafx.scene.paint.Color;

/**
 * ColorLookup is an interface for mapping numbers in the finite range of colors
 */
public interface ColorLookup {

	/**
	 * Retrieves a color based on the total number of colors and the index
	 *
	 * @param index integer in the range
	 * @param num total number of colors in the range
	 * @return the color at the specified index
	 * */
	public Color getColor(int index, int num);
}
