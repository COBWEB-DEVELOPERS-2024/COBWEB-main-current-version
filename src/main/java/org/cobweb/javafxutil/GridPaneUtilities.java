/*
 * Copyright (c) 1995 - 2008 Sun Microsystems, Inc.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Sun Microsystems nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.cobweb.javafxutil;

import javafx.scene.Node;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.geometry.Insets;

/** New utility methods for creating form- or grid-style layouts with JavaFX's GridPane. */
public class GridPaneUtilities {
	/**
	 * Aligns and arranges nodes within a compact grid format via a GridPane.
	 * Cells are adjusted to have a minimum height and width.
	 *
	 * @param gridPane the pane to align and arrange nodes with
	 * @param rows number of rows
	 * @param cols number of columns
	 * @param initialX x location to start the grid at -> insets
	 * @param initialY y location to start the grid at -> insets
	 * @param xPad x padding between cells
	 * @param yPad y padding between cells
	 * @param minCellWidth minimum cell width
	 * @param minCellHeight minimum cell height
	 */
	public static void makeCompactGrid(GridPane gridPane,
									   int rows, int cols,
									   int initialX, int initialY,
									   int xPad, int yPad, int minCellWidth,
									   int minCellHeight) {
		// Set the padding
		gridPane.setPadding(new Insets(initialY, initialX, initialY, initialX));

		// Set the gaps
		gridPane.setHgap(xPad); // x gap
		gridPane.setVgap(yPad); // y gap

		// TODO: Test manual size adjustment --> Might not be needed since window size adjustment is handled automatically by the GridPane
		// Declare variables for the manual size adjustment
		// double widthTotal = initialX;  // Initial padding for X
		// double heightTotal = initialY; // Initial padding for Y

		// Adjust each column and make them the same width.
		for (int c = 0; c < cols; c++) {									// Loop through each column
			ColumnConstraints cConstraints = new ColumnConstraints();		// Create a new ColumnConstraints object
			cConstraints.setMinWidth(minCellWidth); 						// Set the column's minimum width
			cConstraints.setFillWidth(true); 								// Allow the column's width to be filled
			cConstraints.setHgrow(Priority.ALWAYS); 						// Allow the column to resize if space is available
			gridPane.getColumnConstraints().add(cConstraints); 				// Set the column constraints within the GridPane

			// TODO: Test manual size readjustment after migrating everything else to JavaFX
			// totalWidth += minCellWidth + xPad;
		}

		// Adjust each row and make them the same height.
		for (int r = 0; r < rows; r++) {									// Loop through each row in the grid
			RowConstraints rConstraints = new RowConstraints();				// Create a new RowConstraints object
			rConstraints.setMinHeight(minCellHeight); 						// Set the row's minimum height
			rConstraints.setFillHeight(true); 								// Allow the row's height to be filled
			rConstraints.setVgrow(Priority.ALWAYS); 						// Allow the row to resize if space is available
			gridPane.getRowConstraints().add(rConstraints); 				// Set the row constraints within the GridPane

			// TODO: Test manual size readjustment after migrating everything else to JavaFX
			// totalHeight += minCellHeight + yPad;
		}

		// TODO: Test manual size readjustment after migrating everything else to JavaFX
		// Set the GridPane's size based on the total height and width
		// gridPane.setPrefSize(totalWidth, totalHeight);
	}

	/**
	 * Aligns and arranges nodes within a compact grid format via a GridPane.
	 * Cells are adjusted to have a maximum height and width.
	 *
	 * @param gridPane the pane to align and arrange nodes with
	 * @param rows number of rows
	 * @param cols number of columns
	 * @param initialX x location to start the grid at -> insets
	 * @param initialY y location to start the grid at -> insets
	 * @param xPad x padding between cells
	 * @param yPad y padding between cells
	 */
	public static void makeGrid(GridPane gridPane,
								int rows, int cols,
								int initialX, int initialY,
								int xPad, int yPad) {
		// Set the padding
		gridPane.setPadding(new Insets(initialY, initialX, initialY, initialX));

		// Set the gaps
		gridPane.setHgap(xPad); // x gap
		gridPane.setVgap(yPad); // y gap

		// Declare variables to track the max height and width
		double maxCellWidth = 0;	// For columns
		double maxCellHeight = 0;	// For rows

		// TODO: Test manual size adjustment --> Might not be needed since window size adjustment is handled automatically by the GridPane
		// Declare variables for the manual size adjustment
		// double widthTotal = initialX;  // Initial padding for X
		// double heightTotal = initialY; // Initial padding for Y

		for(Node node : gridPane.getChildren()) { 							// Loop through the nodes within the GridPane
			// Update max width
			maxCellWidth = Math.max(maxCellWidth, node.prefWidth(-1)); 	// Compare the current max width with the node's preferred width

			// Update max height
			maxCellHeight = Math.max(maxCellHeight, node.prefHeight(-1)); // Compare the current max height with the node's preferred height
		}

		// Adjust each column and make them the same width.
		for (int c = 0; c < cols; c++) {									// Loop through each column
			ColumnConstraints cConstraints = new ColumnConstraints();		// Create a new ColumnConstraints object
			cConstraints.setMinWidth(maxCellWidth); 						// Set the column's maximum width
			cConstraints.setFillWidth(true); 								// Allow the column's width to be filled
			cConstraints.setHgrow(Priority.ALWAYS); 						// Allow the column to resize if space is available
			gridPane.getColumnConstraints().add(cConstraints); 				// Set the column constraints within the GridPane

			// TODO: Test manual size readjustment after migrating everything else to JavaFX
			// totalWidth += maxCellWidth + xPad;
		}

		// Adjust each row and make them the same height.
		for (int r = 0; r < rows; r++) {									// Loop through each row in the grid
			RowConstraints rConstraints = new RowConstraints();				// Create a new RowConstraints object
			rConstraints.setMinHeight(maxCellHeight); 						// Set the row's maximum height
			rConstraints.setFillHeight(true); 								// Allow the row's height to be filled
			rConstraints.setVgrow(Priority.ALWAYS); 						// Allow the row to resize if space is available
			gridPane.getRowConstraints().add(rConstraints); 				// Set the row constraints within the GridPane

			// TODO: Test manual size readjustment after migrating everything else to JavaFX
			// totalHeight += minCellHeight + yPad;
		}

		// TODO: Test manual size readjustment after migrating everything else to JavaFX
		// Set the GridPane's size based on the total height and width
		// gridPane.setPrefSize(totalWidth, totalHeight);
	}

	/**
	 * A debugging utility that prints to stdout the node's
	 * minimum, preferred, and maximum width sizes.
	 *
	 * @param node to print size info for
	 */
	public static void printWidthSizes(Node node) {
		System.out.println("minimumSize = " + node.minWidth(-1));
		System.out.println("preferredSize = " + node.prefWidth(-1));
		System.out.println("maximumSize = " + node.maxWidth(-1));
	}

	/**
	 * A debugging utility that prints to stdout the node's
	 * minimum, preferred, and maximum height sizes.
	 *
	 * @param node to print size info for
	 */
	public static void printHeightSizes(Node node) {
		System.out.println("minimumSize = " + node.minHeight(-1));
		System.out.println("preferredSize = " + node.prefHeight(-1));
		System.out.println("maximumSize = " + node.maxHeight(-1));
	}
}


// Deprecated below - utilizes AWT and Swing libraries
/**
 * A 1.4 file that provides utility methods for
 * creating form- or grid-style layouts with SpringLayout.
 * These utilities are used by several programs, such as
 * SpringBox and SpringCompactGrid.
 */
/*
public class SpringUtilities {
	// Used by makeCompactGrid.
	private static SpringLayout.Constraints getConstraintsForCell(
			int row, int col,
			Container parent,
			int cols) {
		SpringLayout layout = (SpringLayout) parent.getLayout();
		Component c = parent.getComponent(row * cols + col);
		return layout.getConstraints(c);
	}
	*/
	/**
	 * Aligns the first <code>rows</code> * <code>cols</code>
	 * components of <code>parent</code> in
	 * a grid. Each component in a column is as wide as the maximum
	 * preferred width of the components in that column;
	 * height is similarly determined for each row.
	 * The parent is made just big enough to fit them all.
	 *
	 * @param parent parent container
	 * @param rows number of rows
	 * @param cols number of columns
	 * @param initialX x location to start the grid at
	 * @param initialY y location to start the grid at
	 * @param xPad x padding between cells
	 * @param yPad y padding between cells
	 * @param minCellWidth minimum cell width
	 * @param minCellHeight minimum cell height
	 */
	/*
	public static void makeCompactGrid(Container parent,
			int rows, int cols,
			int initialX, int initialY,
			int xPad, int yPad, int minCellWidth, int minCellHeight) {
		SpringLayout layout = (SpringLayout)parent.getLayout();

		//Align all cells in each column and make them the same width.
		Spring x = Spring.constant(initialX);
		for (int c = 0; c < cols; c++) {
			Spring width = Spring.constant(minCellWidth);
			for (int r = 0; r < rows; r++) {
				width = Spring.max(width,
						getConstraintsForCell(r, c, parent, cols).
						getWidth());
			}
			for (int r = 0; r < rows; r++) {
				SpringLayout.Constraints constraints =
						getConstraintsForCell(r, c, parent, cols);
				constraints.setX(x);
				constraints.setWidth(width);
			}
			x = Spring.sum(x, Spring.sum(width, Spring.constant(xPad)));
		}

		//Align all cells in each row and make them the same height.
		Spring y = Spring.constant(initialY);
		for (int r = 0; r < rows; r++) {
			Spring height = Spring.constant(minCellHeight);
			for (int c = 0; c < cols; c++) {
				height = Spring.max(height,
						getConstraintsForCell(r, c, parent, cols).
						getHeight());
			}
			for (int c = 0; c < cols; c++) {
				SpringLayout.Constraints constraints =
						getConstraintsForCell(r, c, parent, cols);
				constraints.setY(y);
				constraints.setHeight(height);
			}
			y = Spring.sum(y, Spring.sum(height, Spring.constant(yPad)));
		}

		//Set the parent's size.
		SpringLayout.Constraints pCons = layout.getConstraints(parent);
		pCons.setConstraint(SpringLayout.SOUTH, y);
		pCons.setConstraint(SpringLayout.EAST, x);
	}
	*/
	/**
	 * Aligns the first <code>rows</code> * <code>cols</code>
	 * components of <code>parent</code> in
	 * a grid. Each component is as big as the maximum
	 * preferred width and height of the components.
	 * The parent is made just big enough to fit them all.
	 *
	 * @param parent parent container
	 * @param rows number of rows
	 * @param cols number of columns
	 * @param initialX x location to start the grid at
	 * @param initialY y location to start the grid at
	 * @param xPad x padding between cells
	 * @param yPad y padding between cells
	 */
	/*
	public static void makeGrid(Container parent,
			int rows, int cols,
			int initialX, int initialY,
			int xPad, int yPad) {
		SpringLayout layout;

		layout = (SpringLayout)parent.getLayout();

		Spring xPadSpring = Spring.constant(xPad);
		Spring yPadSpring = Spring.constant(yPad);
		Spring initialXSpring = Spring.constant(initialX);
		Spring initialYSpring = Spring.constant(initialY);
		int max = rows * cols;

		//Calculate Springs that are the max of the width/height so that all
		//cells have the same size.
		Spring maxWidthSpring = layout.getConstraints(parent.getComponent(0)).
				getWidth();
		Spring maxHeightSpring = layout.getConstraints(parent.getComponent(0)).
				getWidth();
		for (int i = 1; i < max; i++) {
			SpringLayout.Constraints cons = layout.getConstraints(
					parent.getComponent(i));

			maxWidthSpring = Spring.max(maxWidthSpring, cons.getWidth());
			maxHeightSpring = Spring.max(maxHeightSpring, cons.getHeight());
		}

		//Apply the new width/height Spring. This forces all the
		//components to have the same size.
		for (int i = 0; i < max; i++) {
			SpringLayout.Constraints cons = layout.getConstraints(
					parent.getComponent(i));

			cons.setWidth(maxWidthSpring);
			cons.setHeight(maxHeightSpring);
		}

		//Then adjust the x/y constraints of all the cells so that they
		//are aligned in a grid.
		SpringLayout.Constraints lastCons = new Constraints();
		SpringLayout.Constraints lastRowCons = new Constraints();
		for (int i = 0; i < max; i++) {
			SpringLayout.Constraints cons = layout.getConstraints(
					parent.getComponent(i));
			if (i % cols == 0) { //start of new row
				lastRowCons = lastCons;
				cons.setX(initialXSpring);
			} else { //x position depends on previous component
				cons.setX(Spring.sum(lastCons.getConstraint(SpringLayout.EAST),
						xPadSpring));
			}

			if (i / cols == 0) { //first row
				cons.setY(initialYSpring);
			} else { //y position depends on previous row
				cons.setY(Spring.sum(lastRowCons.getConstraint(SpringLayout.SOUTH),
						yPadSpring));
			}
			lastCons = cons;
		}

		//Set the parent's size.
		SpringLayout.Constraints pCons = layout.getConstraints(parent);
		pCons.setConstraint(SpringLayout.SOUTH,
				Spring.sum(
						Spring.constant(yPad),
						lastCons.getConstraint(SpringLayout.SOUTH)));
		pCons.setConstraint(SpringLayout.EAST,
				Spring.sum(
						Spring.constant(xPad),
						lastCons.getConstraint(SpringLayout.EAST)));
	}
	*/
	/**
	 * A debugging utility that prints to stdout the component's
	 * minimum, preferred, and maximum sizes.
	 */
	/*
	public static void printSizes(Component c) {
		System.out.println("minimumSize = " + c.getMinimumSize());
		System.out.println("preferredSize = " + c.getPreferredSize());
		System.out.println("maximumSize = " + c.getMaximumSize());
	}
}
*/