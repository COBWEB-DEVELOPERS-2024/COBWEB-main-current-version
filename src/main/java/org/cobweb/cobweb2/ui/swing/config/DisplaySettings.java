package org.cobweb.cobweb2.ui.swing.config;

import org.cobweb.javafxutil.ColorLookup;
import org.cobweb.javafxutil.TypeColorEnumeration;

/**
 * Defines how the simulation is displayed, agent colors, etc.
 */
public class DisplaySettings {

	/**
	 * Maps agent type number to a color
	 */
	public ColorLookup agentColor = new TypeColorEnumeration();

}
