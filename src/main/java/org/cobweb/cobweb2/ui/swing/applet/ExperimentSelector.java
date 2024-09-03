/**
 *
 */
package org.cobweb.cobweb2.ui.swing.applet;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;

import java.util.Map;

import javax.swing.JComboBox;

/**
 *
 */
public class ExperimentSelector extends ComboBox<String> {
	/**
	 *
	 */
	private static final long serialVersionUID = 8328713697383538804L;

	public ExperimentSelector(Map<String, String> experiments) {
		// super(experiments.keySet().toArray(new String[0])); // OLD - for JComboBox (Swing)

		// Initialize ComboBox with a list of experiment names
		super(FXCollections.observableArrayList(experiments.keySet()));

		// Disable editing for the ComboBox
		this.setEditable(false);

		// Set the amount of visible rows (max = 5)
		this.setVisibleRowCount(5); // 5 rows visible when the dropdown is shown

		// this.setMaximumRowCount(5); // OLD - for JComboBox (Swing)

	}

}
