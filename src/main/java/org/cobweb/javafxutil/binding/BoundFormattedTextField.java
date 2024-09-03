package org.cobweb.javafxutil.binding;

import java.text.NumberFormat;
import java.text.ParseException;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TextField;

import org.cobweb.cobweb2.ui.config.PropertyAccessor;

// TODO: Implement Serializable interface
public class BoundFormattedTextField extends TextField implements FieldBoundComponent {

	// UID for serialization compatability
	private static final long serialVersionUID = 6928975337583519338L;

	// Object holding the value being bound
	private final Object obj;

	private final NumberFormat numFormat;

	private final PropertyAccessor field;

	private final ObjectProperty<Number> boundVal;


	/** Constructor for the bound formatted text field object
	 * @param newObj object containing the value being bound
	 * @param newAccessor accessor used for the text field
	 * @param newNumFormat number format specified for the given text input
	 * */
	public BoundFormattedTextField(Object newObj, PropertyAccessor newAccessor, NumberFormat newNumFormat) {
		this.obj = newObj;
		this.field = newAccessor;
		this.numFormat = newNumFormat;

		// Set current bound value to the current value from the object
		this.boundVal = new SimpleObjectProperty<>((Number) field.get(this.obj));

		// Setup event listener to handle updates to the object & bound value after text changes
		this.textProperty().addListener((obs, oldText, newText) -> {
			// Parse text input while matching the format & update boundVal accordingly
			Number parsedVal = getValueType(newText);

			// Update the text field
			field.set(this.obj, boundVal.get());
		});

	}

	/* Deprecated
	@Override
	@SuppressWarnings("boxing")
	public void propertyChange(PropertyChangeEvent evt) {
		Object value = evt.getNewValue();
		Object out = null;
		if (field.getType().equals(value.getClass())) {
			out = value;
		} else if (value instanceof Double) {
			Double d = (Double) value;
			if (field.getType().equals(double.class)) {
				out = d;
			} else if (field.getType().equals(float.class)) {
				out = d.floatValue();
			}
		} else if (value instanceof Long) {
			Long l = (Long) value;
			if (field.getType().equals(long.class)) {
				out = l;
			} else if (field.getType().equals(int.class)) {
				out = l.intValue();
			} else if (field.getType().equals(float.class)) {
				out = l.floatValue();
			}
		} else {
			throw new IllegalArgumentException("bad input/output combination: " + value.getClass() + " -> " + field.getType());
		}
		field.set(obj, out);
	}
	*/

	@Override /** Overrides and implements the method defined in the FieldBoundComponent interface
	 			@return a string containing the name of the field being bound */
	public String getLabelText() { return field.getName(); }


	/** Getter for the bound value
	 * @return Number representing the bound value */
	public Number getBoundValue() { return boundVal.get(); }


	/** Setter for the bound value
	 * @param newValue number to be set as the new bound value
	 * */
	public void setBoundValueProperty(Number newValue) {
		// Update the bound value
		this.boundVal.set(newValue);

		// Update the text field
		this.setText(numFormat.format(newValue));

		// Update the field within the object
		field.set(this.obj, newValue);
	}


	/** Property accessor for the bound value
	 * @return the bound value number property for the object
	 * */
	public ObjectProperty<Number> boundValueProperty() { return boundVal; }


	/**
	 * @return a Number object containing the formatted parsed value based on the text input
	 * @param newText text input from the text field
	 */
	private Number getValueType(String newText) {
		try {
			// Parse the input text to the correct type
			return numFormat.parse(newText);
		}
		catch (IllegalArgumentException ex) {
			// Handle exception
			throw new IllegalArgumentException("Text input (" + newText + ") could not be parsed to number format: " + numFormat, ex);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

}
