/**
 *
 */
package org.cobweb.javafxutil.binding;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

import org.cobweb.cobweb2.ui.config.PropertyAccessor;

// TODO: Implement serializable interface
public class EnumComboBox<T extends Enum<T>> extends ComboBox<T> implements FieldBoundComponent {
	private static final long serialVersionUID = -9190442597939410887L;

	private T[] values;
	private PropertyAccessor field;
	private Object obj;

	@SuppressWarnings("unchecked")
	public EnumComboBox(Object newObj, PropertyAccessor newAccessor) {
		this.obj = newObj;
		this.field = newAccessor;

		Class<?> fieldType = field.getType();

		// Check to see if the field type is an enum, throw an error if it's not
		if (!fieldType.isEnum()) {
			throw new IllegalArgumentException("Field type must be an enum");
		}

		// Retrieve enum constants and assign them to the array
		this.values = (T[]) fieldType.getEnumConstants();

		// Set the enum constants from the array into the ComboBox
		ObservableList<T> items = FXCollections.observableArrayList(values);
		this.setItems(items);

		// Set up an event handler to update the current field value whenever selection changes are made
		this.valueProperty().addListener((obs, oldVal, newVal) -> {
			if(newVal != null) { // Only update the field if it's a non-null value
				field.set(this.obj, newVal);
			}
		});
	}

	@Override /** Overrides and implements the method defined in the FieldBoundComponent interface
	 @return a string containing the name of the field being bound */
	public String getLabelText() { return field.getName(); }

	/* Deprecated - old overridden Swing methods from when this method implemented the DefaultComboBoxModel & JComboBox interfaces
	@Override
	public Object getSelectedItem() {
		return field.get(obj);
	}

	@Override
	public void setSelectedItem(Object arg0) {
		field.set(obj, arg0);
	}

	@Override
	public T getElementAt(int arg0) {
		return values[arg0];
	}

	@Override
	public int getSize() {
		return values.length;
	}

	*/

}