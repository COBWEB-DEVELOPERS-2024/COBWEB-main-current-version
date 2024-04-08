package org.cobweb.cobweb2.ui.swing.genetics;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.cobweb.cobweb2.plugins.genetics.GATracker;
import org.cobweb.cobweb2.plugins.genetics.GeneticParams;
import org.cobweb.cobweb2.ui.SimulationRunner;
import org.cobweb.cobweb2.ui.UpdatableUI;
import org.cobweb.cobweb2.ui.ViewerClosedCallback;
import org.cobweb.cobweb2.ui.ViewerPlugin;
import org.cobweb.cobweb2.ui.swing.config.DisplaySettings;
import org.cobweb.swingutil.JComponentWaiter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.DefaultXYDataset;

public class GAChartOutput implements ViewerPlugin, ActionListener, UpdatableUI {

	/** Charts that represent GA outputs */
	private JFreeChart[] gene_status_distribution_chart;
	private JFreeChart[] gene_value_distribution_chart;

	/** x-vectors of the GA outputs */
	private double[] gene_status_distribution_range = new double[GATracker.GENE_STATUS_DISTRIBUTION_SIZE];
	private double[] gene_value_distribution_range = new double[GATracker.GENE_VALUE_DISTRIBUTION_SIZE];

	/** Data of GA output charts .*/
	private DefaultXYDataset[] gene_status_distribution_data;
	private DefaultXYDataset[] gene_value_distribution_data;

	/** Chart panels of GA output that hold the charts. */
	private JPanel gene_status_distribution_panel;
	private JPanel gene_value_distribution_panel;

	/** Chart frame of GA that holds the charts (and their ChartPanels). */
	private JFrame chart_display_frame;

	/** Buttons that display GA outputs on the chart frame. */
	private JButton gene_status_distribution_button =
			new JButton("Genotype-Phenotype Correlation Value");
	private JButton gene_value_distribution_button =
			new JButton("Genotype Value");

	/** Current component on display. */
	private JPanel current_display;

	@Override
	public void actionPerformed(java.awt.event.ActionEvent e) {

		/* Give focus to the gene_status_distribution plot and remove
		 * chart currently displayed from the display frame.*/
		if (e.getSource().equals(gene_status_distribution_button)) {
			chart_display_frame.remove(current_display);
			chart_display_frame.getContentPane()
			.add(gene_status_distribution_panel, BorderLayout.CENTER);
			chart_display_frame.setName("Genotype-Phenotype Correlation Value Distribution");
			chart_display_frame.setVisible(true);
			current_display = gene_status_distribution_panel;

			/* Give focus to the gene_value_distribution plot and remove
			 * chart currently displayed from the display frame.*/
		} else if (e.getSource().equals(gene_value_distribution_button)) {
			chart_display_frame.remove(current_display);
			chart_display_frame.getContentPane()
			.add(gene_value_distribution_panel, BorderLayout.CENTER);
			chart_display_frame.setName("Genotype Value Distribution");
			chart_display_frame.setVisible(true);
			current_display = gene_value_distribution_panel;
		}
		chart_display_frame.repaint();
	}

	/** Initialize a chart representation of gene status distribution among agents. */
	private void initGeneStatusDistributionPlot(String[] names) {
		chart_display_frame.setName("Genotype-Phenotype Correlation Value Distribution");
		gene_status_distribution_panel = new JPanel(new GridLayout(1,geneCount));
		for (int i = 0; i < geneCount; i++) {
			gene_status_distribution_data[i] = new DefaultXYDataset();
			gene_status_distribution_chart[i] = ChartFactory.createXYAreaChart(
					"Gene " + (i+1) + " : " + names[i],
					"Phenotype Multiplier", "Number of Agents",
					gene_status_distribution_data[i], PlotOrientation.VERTICAL,
					true,    // legend?
					true,    // tooltips?
					false    // URLs?
					);

			XYItemRenderer renderer = gene_status_distribution_chart[i].getXYPlot().getRenderer();

			for (int agent = 0; agent < numAgentTypes; agent++) {
				renderer.setSeriesPaint(agent, displaySettings.agentColor.getColor(agent, numAgentTypes));
			}

			ChartPanel cp = new ChartPanel(gene_status_distribution_chart[i]);
			gene_status_distribution_panel.add(cp);
			gene_status_distribution_chart[i].addChangeListener(cp);
		}
		chart_display_frame.getContentPane().add(gene_status_distribution_panel, BorderLayout.CENTER);
		current_display = gene_status_distribution_panel;
		initGeneStatusDistributionRangeVector(); // Initialize x vector
	}

	private int numAgentTypes;

	private int geneCount;
	private SimulationRunner scheduler;
	private GATracker gaTracker;
	private DisplaySettings displaySettings;


	public GAChartOutput(GATracker tracker, GeneticParams params, SimulationRunner scheduler, DisplaySettings dispSettings) {
		gaTracker = tracker;
		this.displaySettings = dispSettings;
		numAgentTypes = gaTracker.getAgentTypeCount();
		geneCount = gaTracker.getGeneCount();

		String[] names = new String[geneCount];
		for(int i = 0; i < geneCount; i++) {
			names[i] = params.phenotype[i].toString();
		}

		gene_value_distribution_chart = new JFreeChart[geneCount];
		gene_status_distribution_chart = new JFreeChart[geneCount];

		gene_status_distribution_data  = new DefaultXYDataset[geneCount];
		gene_value_distribution_data = new DefaultXYDataset[geneCount];

		gene_value_distribution_panel = new JPanel(new GridLayout(1,geneCount));
		for (int i = 0; i < geneCount; i++) {
			gene_value_distribution_data[i] = new DefaultXYDataset();
			gene_value_distribution_chart[i] = ChartFactory.createXYAreaChart(
					"Gene " + (i+1) + " : " + names[i],
					"Gene Value", "Number of Agents",
					gene_value_distribution_data[i], PlotOrientation.VERTICAL,
					true,    // legend?
					true,    // tooltips?
					false    // URLs?
					);

			XYItemRenderer renderer = gene_value_distribution_chart[i].getXYPlot().getRenderer();
			for (int agent = 0; agent < numAgentTypes; agent++) {
				renderer.setSeriesPaint(agent, displaySettings.agentColor.getColor(agent, numAgentTypes));
			}


			ChartPanel cp = new ChartPanel(gene_value_distribution_chart[i]);
			gene_value_distribution_panel.add(cp);
			gene_value_distribution_chart[i].addChangeListener(cp);
		}
		current_display = gene_value_distribution_panel;
		initGeneValueDistributionRangeVector(); // Initialize x vector

		if (chart_display_frame != null) {
			chart_display_frame.dispose();
			chart_display_frame = null;
		}
		chart_display_frame = new JFrame(getName());
		refreshWaiter = new JComponentWaiter(chart_display_frame);
		JPanel button_panel = new JPanel();

		// If we are tracking the gene status distribution
		initGeneStatusDistributionPlot(names);

		// Initialize button
		gene_status_distribution_button.addActionListener(this);
		button_panel.add(gene_status_distribution_button);
		gene_value_distribution_button.addActionListener(this);
		button_panel.add(gene_value_distribution_button);

		chart_display_frame.getContentPane().add(button_panel, BorderLayout.SOUTH);
		chart_display_frame.setSize(new Dimension(900, 460));

		chart_display_frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				onClosed.viewerClosed();
			}
		});

		this.scheduler = scheduler;
	}


	private void setSchedulerSubscribed(boolean subscribe) {
		if (subscribe)
			scheduler.addUIComponent(this);
		else
			scheduler.removeUIComponent(this);
	}

	/** Initialize the gene_status_distribution_range x-vector. */
	private void initGeneStatusDistributionRangeVector() {
		for (int i = 0; i < GATracker.GENE_STATUS_DISTRIBUTION_SIZE; i++) {
			// Rounding the number off to the 4 significand digits
			gene_status_distribution_range[i] = Math.round(2*Math.abs(Math.sin(i*Math.PI/180)*10000))/10000.0;
		}
	}

	/** Initialize the gene_value_distribution_range x-vector. */
	private void initGeneValueDistributionRangeVector() {
		for (int i = 0; i < GATracker.GENE_VALUE_DISTRIBUTION_SIZE; i++) {
			// Rounding the number off to the 4 significand digits
			gene_value_distribution_range[i] = i;

		}
	}

	private JComponentWaiter refreshWaiter;
	private ViewerClosedCallback onClosed;

	/** Update the gene_status_distribution data */
	private void updateGeneStatusDistributionData() {
		double[][][] value = gaTracker.getGeneValueDistribution();
		double[][][] status = gaTracker.getGeneStatusDistribution();

		/*
		 * Collecting all data into one array, then replacing everything at once
		 * to hopefully fix race condition in JFreeChart
		 */
		double[][][][][] new_datasets = new double[geneCount][][][][];

		if (refreshWaiter.isReadyToRefresh()) {
			for (int j = 0; j < geneCount; j++) {
				new_datasets[j] = new double[numAgentTypes][2][][];

				for (int i = 0; i < numAgentTypes; i++) {
					double[][] newx = {gene_status_distribution_range, status[i][j]};
					new_datasets[j][i][0] = newx;
					double[][] newy = {gene_value_distribution_range, value[i][j]};
					new_datasets[j][i][1] = newy;
				}
			}

			for (int j = 0; j < geneCount; j++) {
				for (int i = 0; i < numAgentTypes; i++) {
					String key = "Agent " + (i+1);
					gene_status_distribution_data[j].addSeries(key, new_datasets[j][i][0]);
					gene_value_distribution_data[j].addSeries(key, new_datasets[j][i][1]);
				}
			}

			// If wait is true, there might be a deadlock pressing the stop button
			refreshWaiter.refresh(false);
		}

	}

	@Override
	public String getName() {
		return "Gene Statistics";
	}

	@Override
	public void on() {
		chart_display_frame.setVisible(true);
		setSchedulerSubscribed(true);
	}

	@Override
	public void off() {
		chart_display_frame.setVisible(false);
		setSchedulerSubscribed(false);
	}

	@Override
	public void dispose() {
		off();
		chart_display_frame.dispose();
		chart_display_frame = null;
	}

	@Override
	public void setClosedCallback(ViewerClosedCallback onClosed) {
		this.onClosed = onClosed;
	}

	@Override
	public void update(boolean synchronous) {
		updateGeneStatusDistributionData();
	}

	@Override
	public boolean isReadyToUpdate() {
		return true;
	}

	@Override
	public void onStopped() {
		update(true);
	}

	@Override
	public void onStarted() {
		// Nothing
	}
}
