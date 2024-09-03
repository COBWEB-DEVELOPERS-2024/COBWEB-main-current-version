package org.cobweb.javafxutil.binding;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.CheckBox;

import org.cobweb.cobweb2.ui.config.PropertyAccessor;

// TODO: Implement Serializable interface
public class BoundCheckBox extends CheckBox implements FieldBoundComponent {
	// Version control identifier for the serialization compatibility
	private static final long serialVersionUID = -4621056922233460755L;
	private final Object obj;
	private final PropertyAccessor field;
	private final String label;
	private final BooleanProperty boolProperty;

	/** Constructor for the BoundCheckBox object
	 * // TODO: write this later lol
	 * @param newObj
	 * @param newAccessor
	 * */
	public BoundCheckBox(Object newObj, PropertyAccessor newAccessor) {
		this.obj = newObj;
		this.field = newAccessor;

		// Check to see if the accessor type is the appropriate type (boolean)
		if (!field.getType().equals(boolean.class) && !field.getType().equals(Boolean.class)) {
			throw new IllegalArgumentException("Accessor type must be boolean");
		}

		// Set the BooleanProperty to the value from the data field
		this.boolProperty = new SimpleBooleanProperty((boolean) field.get(obj));
		this.selectedProperty().bindBidirectional(boolProperty); // Bind selected property to the boolean property

		// Set up the event listener for updating the field when the checkbox/boolean values change
		boolProperty.addListener((obs, oldVal, newVal) -> field.set(obj, newVal));

		this.label = newAccessor.getName();
	}

	@Override /** Overrides and implements the method defined in the FieldBoundComponent interface
	 @return a string containing the name of the field being bound */
	public String getLabelText() { return field.getName(); }
}
