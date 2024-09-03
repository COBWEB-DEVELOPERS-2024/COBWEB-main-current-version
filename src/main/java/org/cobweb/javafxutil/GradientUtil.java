package org.cobweb.javafxutil;

import javafx.scene.paint.Color;

public class GradientUtil {

	/* Deprecated from Swing build - Uses java.awt.Color library
	public static Color colorFromFloat(float y) {
		return new Color(Color.HSBtoRGB((1 - y) * 2 / 3, 1f, 1f));
	}

	public static Color colorFromFloat(float y, float alpha) {
		int a = (int)( alpha * 255);
		int intCol =
				Color.HSBtoRGB((1 - y) * 2 / 3, 1f, 1f) & 0xffffff |
				(a << 24);
		return new Color(intCol, true);
	}
	*/

	public static Color colorFromFloat(float y) {
		// Return HSB -> RGB
		return Color.hsb((1 - y) * 240, 1.0, 1.0); // via JavaFX's built-in HSB method
	}

	public static Color colorFromFloat(float y, float alpha) {
		// Return HSB -> RGBA
		return Color.hsb((1 - y) * 240, 1.0, 1.0, alpha); // via JavaFX's built-in HSB method
	}

}
