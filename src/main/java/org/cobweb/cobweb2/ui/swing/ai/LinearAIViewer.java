package org.cobweb.cobweb2.ui.swing.ai;

import javafx.stage.WindowEvent;

import org.cobweb.cobweb2.impl.ai.LinearWeightsControllerParams;
import org.cobweb.cobweb2.ui.ViewerClosedCallback;
import org.cobweb.cobweb2.ui.ViewerPlugin;

public class LinearAIViewer implements ViewerPlugin {

	private LinearAIGraph aiGraph;
	private ViewerClosedCallback onClosed;
	private LinearWeightsControllerParams controllerParams;

	/** Constructor for the LinearAIViewer object
	 * @param controllerParams parameters for the AI controller */
	public LinearAIViewer(LinearWeightsControllerParams controllerParams) {
		this.controllerParams = controllerParams;
	}

	/** Method to handle window close requests and display the window containing the graph */
	@Override
	public void on() {
		// Create the window
		aiGraph = new LinearAIGraph(controllerParams);

		// Event handler for the window close request
		aiGraph.setOnCloseRequest(event -> {
			onClosed.viewerClosed(); // Viewer has been closed
		});

		// setVisible(true) // Old Swing method

		// Display the window containing the graph
		aiGraph.show();
	}

	/** Method to handle the window being closed and viewer being turned off */
	@Override
	public void off() {
		// Ensure that the window is created
		if (aiGraph == null) { return; } // If it's not created, don't do anything

		// Deprecated Swing stuff
		// aiGraph.setVisible(false);
		// aiGraph.setEnabled(false);

		aiGraph.close(); // Close the graph

		// Set the reference to the window to null
		aiGraph = null;
	}

	/** Method to return the name of the viewer plugin
	 * @return String containing the name of the viewer plugin */
	@Override
	public String getName() {
		return "AI Weight Stats";
	}

	/** Method to set the callback to be called when AI graph viewer gets closed */
	@Override
	public void setClosedCallback(ViewerClosedCallback onClosed) {
		this.onClosed = onClosed;
	}

	/** Method for ensuring the AI graph viewer is closed --> garbage collection */
	@Override
	public void dispose() {
		off();
	}
}