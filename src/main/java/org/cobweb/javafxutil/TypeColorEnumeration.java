package org.cobweb.javafxutil;

import javafx.scene.paint.Color;

public class TypeColorEnumeration implements ColorLookup {

	// Color table is defined using JavaFX's definitions for each color
	private static Color[] colorTable = {
			Color.YELLOW, Color.CYAN,
			Color.GREEN, Color.RED,
			Color.ORANGE, Color.BLUE,
			Color.MAGENTA, Color.PINK };

	@Override
	public Color getColor(int index, int num) {
		// Generates any number of colors, num bound is ignored.
		Color color = colorTable[index % colorTable.length];
		while (index >= colorTable.length) {
			index -= colorTable.length;
			color = color.darker();
		}
		return color;
	}
}
