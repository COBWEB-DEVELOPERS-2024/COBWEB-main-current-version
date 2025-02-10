package org.cobweb.cobweb2.impl;

import java.util.*;

import org.cobweb.cobweb2.core.*;
import org.cobweb.cobweb2.plugins.EnvironmentMutator;
import org.cobweb.cobweb2.ui.swing.discretizedgravity.DiscretizedGravitySplit;

public class ComplexEnvironment extends Environment {

	protected ComplexAgentParams agentData[];
	public ComplexEnvironmentParams data = new ComplexEnvironmentParams();
	private Map<Class<? extends EnvironmentMutator>, EnvironmentMutator> plugins = new LinkedHashMap<>();

	private double totalGridEnergy;
	private double avgAgentEnergy;
	private double temperature = 300; // Temporary placeholder (adjust later)
	private int prevSplits = 1;

	private Map<Location, ComplexAgent> agentCells = new HashMap<>(); // Tracks agents per discrete gravity cell
	private Set<Location> occupiedCells = new HashSet<>(); // Prevents multiple entities in one cell

	public ComplexEnvironment(SimulationInternals simulation) {
		super(simulation);
	}

	public Collection<Agent> getAllAgents() {
		return agentTable.values();
	}

	/**
	 * Aggregates energy across the entire grid and computes the average energy of all agents.
	 */
	public void computeAggregatedEnergy() {
		totalGridEnergy = 0;
		int agentCount = 0;

		for (Agent agent : this.getAllAgents()) {
			if (agent == null) continue;
			totalGridEnergy += agent.getEnergy();
			agentCount++;
		}

		avgAgentEnergy = agentCount > 0 ? totalGridEnergy / agentCount : 0;
	}

	/**
	 * Computes the number of splits based on total energy and temperature.
	 * Ensures merging (unsplitting) happens when necessary.
	 */
	public int getSplitCount() {
		computeAggregatedEnergy();

		// Partition function Z(x) approximation
		int computedSplits = (int) Math.floor(Math.sqrt(totalGridEnergy / (10 * temperature)));

		if (computedSplits < 1) {
			computedSplits = 1;
		}

		if (computedSplits < prevSplits) {
			handleMerging();
		}

		prevSplits = computedSplits;

		System.out.println("Total Energy: " + totalGridEnergy +
				" | Avg Energy: " + avgAgentEnergy +
				" | Computed Splits: " + computedSplits);
		System.out.flush();

		return computedSplits;
	}

	/**
	 * Adds an agent to the environment, ensuring that no other entity (agent or food) exists in the same discrete gravity cell.
	 */
	public synchronized void addAgent(Location l, int type) {
		if (occupiedCells.contains(l)) {
			return; // Block addition if cell is occupied
		}

		if (!hasAgent(l) && !hasStone(l) && !hasDrop(l)) {
			int agentType = type;
			ComplexAgent newAgent = (ComplexAgent) spawnAgent(new LocationDirection(l), agentType);
			agentCells.put(l, newAgent);
			occupiedCells.add(l);
		}
	}

	protected Agent spawnAgent(LocationDirection location, int agentType) {
		ComplexAgent child = (ComplexAgent) simulation.newAgent(agentType);
		ComplexAgentParams params = agentData[agentType];
		child.init(this, location, params, params.initEnergy.getValue());
		return child;
	}

	/**
	 * Ensures merging (unsplitting) happens when necessary.
	 * Agents absorb merged energy and redundant agents are removed.
	 */
	private void handleMerging() {
		Set<Location> toRemove = new HashSet<>();
		Map<Location, Double> mergedEnergies = new HashMap<>();

		for (Location l : agentCells.keySet()) {
			if (!isCellValidForAgent(l)) {
				toRemove.add(l);
			} else {
				double mergedEnergy = mergedEnergies.containsKey(l) ? mergedEnergies.get(l) : 0;
				mergedEnergy += agentCells.get(l).getEnergy();
				mergedEnergies.put(l, mergedEnergy);
			}
		}

		for (Location l : toRemove) {
			agentCells.remove(l);
			occupiedCells.remove(l);
		}

		System.out.println("MERGE MERGE EMRGE");


	}

	private boolean isCellValidForAgent(Location l) {
		return agentCells.containsKey(l) || !occupiedCells.contains(l);
	}

	@Override
	public synchronized void update() {
		super.update();
		int splits = getSplitCount();
		System.out.println("Splits in update loop: " + splits);
		System.out.flush();

		for (EnvironmentMutator v : plugins.values()) {
			v.update();
		}

		System.out.println("Updated Agent List:");
		for (Map.Entry<Location, ComplexAgent> entry : agentCells.entrySet()) {
			System.out.println("Location: " + entry.getKey() + " | Energy: " + entry.getValue().getEnergy());
		}
		System.out.flush();
	}

	public synchronized void setParams(ComplexEnvironmentParams envParams, AgentParams agentParams, boolean keepOldAgents, boolean keepOldArray, boolean keepOldDrops) throws IllegalArgumentException {
		data = envParams;
		agentData = agentParams.agentParams;
		super.load(data.width, data.height, data.wrapMap, keepOldArray);

		if (keepOldAgents) {
			killOffgridAgents();
			loadOldAgents();
		} else {
			agentTable.clear();
		}

		if (!keepOldDrops) {
			clearDrops();
		}
	}

	public void loadNew() {
		for (int i = 0; i < data.initialStones; ++i) {
			Location l;
			int tries = 0;
			do {
				l = topology.getRandomLocation();
			} while (tries++ < 100 && (hasStone(l) || hasDrop(l) || hasAgent(l)));
			if (tries < 100)
				addStone(l);
		}

		for (EnvironmentMutator p : plugins.values()) {
			p.loadNew();
		}
	}

	public void loadNewAgents() {
		for (int i = 0; i < agentData.length; ++i) {
			for (int j = 0; j < agentData[i].initialAgents; ++j) {
				Location location;
				int tries = 0;
				do {
					location = topology.getRandomLocation();
				} while (tries++ < 100 && (hasAgent(location) || hasStone(location) || hasDrop(location)));
				if (tries < 100) {
					int agentType = i;
					spawnAgent(new LocationDirection(location), agentType);
				}
			}
		}
	}

	private void loadOldAgents() {
		for (int x = 0; x < topology.width; ++x) {
			for (int y = 0; y < topology.height; ++y) {
				Location currentPos = new Location(x, y);
				ComplexAgent agent = (ComplexAgent) getAgent(currentPos);
				if (agent != null) {
					int theType = agent.getType();
					agent.setParams(agentData[theType]);
				}
			}
		}
	}

	public <T extends EnvironmentMutator> void addPlugin(T plugin) {
		plugins.put(plugin.getClass(), plugin);
	}

	public <T extends EnvironmentMutator> T getPlugin(Class<T> type) {
		@SuppressWarnings("unchecked")
		T plugin = (T) plugins.get(type);
		return plugin;
	}

	private void updateDrops() {
		for (int x = 0; x < topology.width; x++) {
			for (int y = 0; y < topology.height; y++) {
				Location l = new Location(x, y);
				if (!hasDrop(l))
					continue;
				Drop d = getDrop(l);
				d.update();
			}
		}
	}
}
