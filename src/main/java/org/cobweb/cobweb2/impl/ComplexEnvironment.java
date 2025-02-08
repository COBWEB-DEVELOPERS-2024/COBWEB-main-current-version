package org.cobweb.cobweb2.impl;

import java.util.*;

import org.cobweb.cobweb2.core.Agent;
import org.cobweb.cobweb2.core.Drop;
import org.cobweb.cobweb2.core.Environment;
import org.cobweb.cobweb2.core.Location;
import org.cobweb.cobweb2.core.LocationDirection;
import org.cobweb.cobweb2.core.SimulationInternals;
import org.cobweb.cobweb2.plugins.EnvironmentMutator;

/**
 * 2D grid where agents and food live.
 */
public class ComplexEnvironment extends Environment {

	protected ComplexAgentParams agentData[];

	public ComplexEnvironmentParams data = new ComplexEnvironmentParams();

	private Map<Class<? extends EnvironmentMutator>, EnvironmentMutator> plugins = new LinkedHashMap<>();

	// Store aggregated energy per grid cell
	private Map<Location, Integer> cellEnergyMap = new HashMap<>();
	private Map<Location, Integer> cellSizeMap = new HashMap<>();

	public ComplexEnvironment(SimulationInternals simulation) {
		super(simulation);
	}

	/**
	 * Retrieves all grid locations.
	 */
	public Collection<Location> getAllGridLocations() {
		List<Location> locations = new ArrayList<>();
		for (int x = 0; x < topology.width; x++) {
			for (int y = 0; y < topology.height; y++) {
				locations.add(new Location(x, y));
			}
		}
		return locations;
	}

	/**
	 * Aggregates energy per grid cell before resizing.
	 */
	public void computeAggregatedEnergy() {
		cellEnergyMap.clear();

		for (Agent agent : this.getAllAgents()) {
			if (agent == null) continue;
			Location loc = agent.getPosition();

			if (!isValidLocation(loc)) continue;

			int energy = agent.getEnergy();
			cellEnergyMap.put(loc, cellEnergyMap.getOrDefault(loc, 0) + energy);
		}
	}


	public int getAggregatedEnergy(Location loc) {
		return cellEnergyMap.getOrDefault(loc, 0);
	}

	public Collection<Agent> getAllAgents() {
		return agentTable.values();
	}

	/**
	 * Resizes grid dynamically based on energy levels.
	 */
	public void resizeGridBasedOnEnergy() {
		computeAggregatedEnergy(); // Ensure energy is updated before resizing

		for (Location loc : cellEnergyMap.keySet()) {
			if (!isValidLocation(loc)) continue;
			int energy = cellEnergyMap.get(loc);
			adjustCellSize(loc, energy);
		}
	}

	// ✅ Ensure location is inside the valid grid area
	private boolean isValidLocation(Location loc) {
		if (!data.wrapMap) {
			return loc.x >= 0 && loc.y >= 0 && loc.x < data.width && loc.y < data.height;
		}
		return true;
	}



	/**
	 * Adjusts the size of a cell based on energy.
	 */

	private void adjustCellSize(Location loc, int energy) {
		int baseSize = 1;
		int newSize = Math.max(baseSize, energy / 10);

		if (loc.x < 0 || loc.y < 0 || loc.x >= data.width || loc.y >= data.height) {
			System.out.println("Skipping out-of-bounds cell: " + loc);
			return;
		}
		cellSizeMap.put(loc, newSize);

		System.out.println("Cell at " + loc + " resized to: " + newSize);
	}


	/**
	 * Retrieves the current size of a grid cell.
	 */
	public int getCellSize(Location loc) {
		return cellSizeMap.getOrDefault(loc, 1);
	}


	public synchronized void addAgent(Location l, int type) {
		if (!hasAgent(l) && !hasStone(l) && !hasDrop(l)) {
			int agentType = type;
			spawnAgent(new LocationDirection(l), agentType);
		}
	}

	protected Agent spawnAgent(LocationDirection location, int agentType) {
		ComplexAgent child = (ComplexAgent) simulation.newAgent(agentType);
		ComplexAgentParams params = agentData[agentType];
		child.init(this, location, params, params.initEnergy.getValue());
		return child;
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

	@Override
	public synchronized void update() {
		super.update();
		resizeGridBasedOnEnergy();
		for (EnvironmentMutator v : plugins.values()) {
			v.update();
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
