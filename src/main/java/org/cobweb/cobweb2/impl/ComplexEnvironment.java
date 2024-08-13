package org.cobweb.cobweb2.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.cobweb.cobweb2.core.Agent;
import org.cobweb.cobweb2.core.Drop;
import org.cobweb.cobweb2.core.Environment;
import org.cobweb.cobweb2.core.Location;
import org.cobweb.cobweb2.core.LocationDirection;
import org.cobweb.cobweb2.core.SimulationInternals;
import org.cobweb.cobweb2.plugins.EnvironmentMutator;

/**
 * 2D grid where agents and food live
 */
public class ComplexEnvironment extends Environment {

	protected ComplexAgentParams agentData[];

	public ComplexEnvironmentParams data = new ComplexEnvironmentParams();

	private Map<Class<? extends EnvironmentMutator>, EnvironmentMutator> plugins = new LinkedHashMap<>();

	public ComplexEnvironment(SimulationInternals simulation) {
		super(simulation);
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

	/**
	 * Loads a new complex environment using the data held within the simulation
	 * configuration object, p.  The following actions are performed during a load:
	 *
	 * <p>1. Attaches the scheduler, s, to the environment.
	 * <br>2. Stores parameters from the file or stream in to ComplexEnvironment.
	 * <br>3. Sets up location cache, and environment food and waste arrays.
	 * <br>4. Keeps or removes old agents.
	 * <br>5. Adds new stones, food, and agents.
	 *
	 */
	public synchronized void setParams(ComplexEnvironmentParams envParams, AgentParams agentParams, boolean keepOldAgents, boolean keepOldArray, boolean keepOldDrops) throws IllegalArgumentException {
		data = envParams;
		agentData = agentParams.agentParams;

		super.load(data.width, data.height, data.wrapMap, keepOldArray);

		// Remove old components
		if (keepOldAgents) {
			killOffgridAgents();
			loadOldAgents();
		} else {
			// do not call clearAgents(), it invokes mutators, etc
			agentTable.clear();
		}

		if (!keepOldDrops) {
			clearDrops();
		}
	}

	public void loadNew() {
		// add stones in to random locations
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

	/**
	 * Places agents in random locations.  If prisoner's dilemma is being used,
	 * a number of cheaters that is dependent on the probability of being a cheater
	 * are randomly assigned to agents.
	 */
	// FIXME: move out to simulation?
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

	/**
	 * Searches through each location to find every old agent.  Each agent that is found
	 * is added to the scheduler if the scheduler is new.  Agents that are off the new
	 * environment are removed from the environment.
	 */
	private void loadOldAgents() {
		// Add in-bounds old agents to the new scheduler and update new
		// constants
		// TODO: a way to keep old parameters for old agents?
		for (int x = 0; x < topology.width; ++x) {
			for (int y = 0; y < topology.height; ++y) {
				Location currentPos = new Location(x, y);
				ComplexAgent agent = (ComplexAgent) getAgent(currentPos);
				if (agent != null) {
					int theType = agent.getType();
					if(thetype < agentData.size()){
					agent.setParams(agentData[theType]);
					}
				}
			}
		}
	}

	/**
	 * tickNotification is the method called by the scheduler for each of its
	 * clients for every tick of the simulation. For environment,
	 * tickNotification performs all of the per-tick tasks necessary for the
	 * environment to function properly. These tasks include managing food
	 * depletion, food growth, and random food-"dropping".
	 */
	@Override
	public synchronized void update() {
		super.update();

		updateDrops();

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

	/**
	 *
	 */
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
