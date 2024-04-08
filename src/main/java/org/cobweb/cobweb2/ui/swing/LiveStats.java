package org.cobweb.cobweb2.ui.swing;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;

import org.cobweb.cobweb2.Simulation;
import org.cobweb.cobweb2.ui.SimulationRunner;
import org.cobweb.cobweb2.ui.StatsTracker;
import org.cobweb.cobweb2.ui.UpdatableUI;
import org.cobweb.cobweb2.ui.ViewerClosedCallback;
import org.cobweb.cobweb2.ui.ViewerPlugin;
import org.cobweb.swingutil.JComponentWaiter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class LiveStats implements UpdatableUI, ViewerPlugin {

	private JFrame graph = new JFrame("Population");

	private JComponentWaiter graphSynchronizer = new JComponentWaiter(graph);

	private int frame = 0;


	private static final int frameskip = 50;


	private XYSeries agentData = new XYSeries("Agents");
	private XYSeries foodData = new XYSeries("Food");

	private XYSeriesCollection data = new XYSeriesCollection();
	private JFreeChart plot = ChartFactory.createXYLineChart(
			"Agent and Food count"
			, "Time"
			, "Count"
			, data
			, PlotOrientation.VERTICAL
			, true
			, false
			, false);

	private SimulationRunner scheduler;

	private StatsTracker statsTracker;

	public LiveStats(SimulationRunner scheduler) {
		this.scheduler = scheduler;
		graph.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentHidden(ComponentEvent e) {
				if (onClosed != null)
					onClosed.viewerClosed();
			}
		});

		graph.setSize(500, 500);
		ChartPanel cp = new ChartPanel(plot, true);
		graph.add(cp);
		plot.setAntiAlias(true);
		plot.setNotify(false);

		data.addSeries(agentData);
		data.addSeries(foodData);

		statsTracker = new StatsTracker((Simulation) this.scheduler.getSimulation());

		scheduler.addUIComponent(this);
	}

	@Override
	public void dispose() {
		off();
		scheduler.removeUIComponent(this);
		graph.dispose();
		graph = null;
	}

	@Override
	public void update(boolean sync) {
		long time = statsTracker.getTime();
		long agentCount = statsTracker.getAgentCount();
		long foodCount = statsTracker.countFoodTiles();

		agentData.add(time, agentCount);
		foodData.add(time, foodCount);

		if (frame++ >= frameskip) {
			frame = 0;
			plot.setNotify(true);
			plot.setNotify(false);
		}

		graphSynchronizer.refresh(sync);
	}

	@Override
	public boolean isReadyToUpdate() {
		return true;
	}

	public void toggleGraphVisible() {
		graph.setVisible(!graph.isVisible());
	}

	@Override
	public String getName() {
		return "Population Graph";
	}

	@Override
	public void on() {
		graph.setVisible(true);
	}

	@Override
	public void off() {
		graph.setVisible(false);
	}

	private ViewerClosedCallback onClosed;
	@Override
	public void setClosedCallback(ViewerClosedCallback onClosed) {
		this.onClosed = onClosed;
	}

	@Override
	public void onStopped() {
		plot.setNotify(true);
		plot.setNotify(false);
	}

	@Override
	public void onStarted() {
		// Nothing
	}

}
