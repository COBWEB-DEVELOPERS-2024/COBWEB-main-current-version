package org.cobweb.cobweb2.ui.swing.ai;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.util.Duration;

import org.cobweb.cobweb2.impl.ai.LinearWeightsControllerParams;

/**
 * Creates a bar chart containing AI data
 * */
public class LinearAIGraph extends Stage {
	// TODO: Implement serializable interface
	private static final long serialVersionUID = 674068154319803208L;

	private final Timeline refreshTimer; 				   // Needed for refreshing the data on the graphs

	private final LinearWeightsControllerParams LWCParams; // LWC parameters for graph data

	// Global objects for the graphs
	private final BarChart barChart;
	private final XYChart.Series<String, Number> xySeriesChart = new XYChart.Series<>();


	/** Constructor for the LinearAIGraph object
	 * @param newControllerParams specified controller parameters
	 * */
	public LinearAIGraph(LinearWeightsControllerParams newControllerParams) {
		// Initialize/set the LWC params
		this.LWCParams = newControllerParams;

		// Set the title of the stage (or window)
		setTitle("AI Output Distribution");

		// Create the y-axis
		CategoryAxis y = new CategoryAxis();
		y.setLabel("Value");

		// Create the x-axis
		CategoryAxis x = new CategoryAxis();
		x.setLabel("Output");

		// Initialize the bar chart object
		this.barChart = new BarChart<>(x, y);
		barChart.setTitle("AI Output Distribution"); // Set the title of the bar chart
		barChart.getData().add(xySeriesChart); 		 // Add the x & y series chart to the bar chart

		// Loop through each output name within the LWC
		for(String names : LinearWeightsControllerParams.outputNames) {
			// Add each data point with initial values of 0 to start
			xySeriesChart.getData().add(new XYChart.Data<>(names, 0));
		}

		// Create the window for the graph
		Scene scene = new Scene (barChart, 800, 800); // Scene contains the bar chart, displaying it at 800x800 resolution
		setScene(scene); // Place the scene onto the stage

		// Initialize the refresh timer using KeyFrame to trigger the refreshData() method every 100 ms
		refreshTimer = new Timeline(new KeyFrame(Duration.millis(100), e -> refreshData()));

		// Set the cycle count to ensure data will be refreshed every 100 ms
		refreshTimer.setCycleCount(Timeline.INDEFINITE); // Cycle count is indefinite until the window is closed

		// Refresh data when the window is shown
		setOnShown(e -> { // event = e
			refreshTimer.play();
		});

		// TODO: Test this and compare hardware resource consumption
		// setOnShowing(e -> refreshTimer.play());

		// Stop the refresh timer when the window is closed or minimized
		setOnHidden(e -> { // event = e
			refreshTimer.stop();
		});

		// TODO: Test these and compare hardware resource consumption
		// setOnHiding(e -> refreshTimer.pause());
		// setOnHiding(e -> refreshTimer.stop());
		// setOnHidden(e -> refreshTimer.pause());

		// Display the window
		show(); // 'Stage' method to display the window/stage
	}

	/** Refreshes data being displayed on the chart */
	private void refreshData() {
		// Declare an array to retrieve and store the latest data from LWC parameters
		double[] data = LWCParams.getRunningOutputMean();

		// Handle UI updates
		Platform.runLater(() -> { // Update the UI on the JavaFX application thread
			// Loop through the data
			for(int i = 0; i < LinearWeightsControllerParams.OUTPUT_COUNT; i++) {
				// Update each Y value on the XY series chart
				xySeriesChart.getData().get(i).setYValue(data[i]);
			}
		});
	}
}

/* Deprecated code used for reference. Uses old Swing/AWT libraries
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.Timer;

import org.cobweb.cobweb2.impl.ai.LinearWeightsControllerParams;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class LinearAIGraph extends JFrame implements WindowListener, ActionListener {

	private static final long serialVersionUID = 674068154319803208L;

	private ChartPanel chartPan;

	private DefaultCategoryDataset catd = new DefaultCategoryDataset();
	private JFreeChart chart;

	private Timer refreshTimer;

	private LinearWeightsControllerParams params;

	public LinearAIGraph(LinearWeightsControllerParams controllerParams) {
		super("AI output distribution");
		this.params = controllerParams;

		for (String x : LinearWeightsControllerParams.outputNames) {
			catd.addValue(0, x, "");
		}
		chart = ChartFactory.createBarChart("AI output distribution", "Output", "Value", catd, PlotOrientation.VERTICAL, true, false, false);
		chartPan = new ChartPanel(chart, true);


		this.add(chartPan);
		this.addWindowListener(this);
		refreshTimer = new Timer(100, this);
		this.pack();
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// Nothing
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// Nothing
	}

	@Override
	public void windowClosing(WindowEvent e) {
		refreshTimer.stop();
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// Nothing
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// Nothing
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// Nothing
	}

	@Override
	public void windowOpened(WindowEvent e) {
		refreshTimer.start();
	}
*/
	/**
	 * Refresh timer function
	 */
	/*
	@Override
	public void actionPerformed(ActionEvent arg0) {
		double data[] = params.getRunningOutputMean();
		for (int i = 0; i < LinearWeightsControllerParams.OUTPUT_COUNT; i++) {
			catd.setValue(data[i], LinearWeightsControllerParams.outputNames[i], "");
		}
	}

}
*/