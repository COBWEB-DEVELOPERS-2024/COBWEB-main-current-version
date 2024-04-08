package org.cobweb.cobweb2.plugins;

import java.util.*;

import org.cobweb.cobweb2.core.*;


public class MutatorListener implements AgentListener {

	private Set<SpawnMutator> spawnMutators = new LinkedHashSet<>();
	private Set<ContactMutator> contactMutators = new LinkedHashSet<>();
	private Set<StepMutator> stepMutators = new LinkedHashSet<>();
	private Set<EnergyMutator> energyMutators = new LinkedHashSet<>();
	private Set<UpdateMutator> updateMutators = new LinkedHashSet<>();
	private Set<LoggingMutator> loggingMutators = new LinkedHashSet<>();
	private Set<ConsumptionMutator> consumptionMutators = new LinkedHashSet<>();
	private Set<ControllerInputMutator> controllerMutators = new LinkedHashSet<>();
	private Set<MoveMutator> moveMutators = new LinkedHashSet<>();
	private Set<LocationMutator> locationMutators = new LinkedHashSet<>();
	private Set<AgentMutator> allMutators = new HashSet<>();

	public void addMutator(AgentMutator mutator) {
		if (mutator instanceof SpawnMutator)
			spawnMutators.add((SpawnMutator) mutator);

		if (mutator instanceof ContactMutator)
			contactMutators.add((ContactMutator) mutator);

		if (mutator instanceof StepMutator)
			stepMutators.add((StepMutator) mutator);

		if (mutator instanceof EnergyMutator)
			energyMutators.add((EnergyMutator) mutator);

		if (mutator instanceof UpdateMutator)
			updateMutators.add((UpdateMutator) mutator);

		if (mutator instanceof ConsumptionMutator)
			consumptionMutators.add((ConsumptionMutator) mutator);

		if (mutator instanceof LoggingMutator)
			loggingMutators.add((LoggingMutator) mutator);

		if (mutator instanceof ControllerInputMutator)
			controllerMutators.add((ControllerInputMutator) mutator);

		if (mutator instanceof MoveMutator)
			moveMutators.add((MoveMutator) mutator);

		if (mutator instanceof LocationMutator) {
			locationMutators.add((LocationMutator) mutator);
		}

		allMutators.add(mutator);
	}


	public void removeMutator(AgentMutator mutator) {
		spawnMutators.remove(mutator);
		contactMutators.remove(mutator);
		stepMutators.remove(mutator);
		energyMutators.remove(mutator);
		updateMutators.remove(mutator);
		loggingMutators.remove(mutator);
		consumptionMutators.remove(mutator);
		controllerMutators.remove(mutator);
		moveMutators.remove(mutator);
		locationMutators.remove(mutator);

		allMutators.remove(mutator);
	}

	public void clearMutators() {
		spawnMutators.clear();
		contactMutators.clear();
		stepMutators.clear();
		energyMutators.clear();
		updateMutators.clear();
		loggingMutators.clear();
		consumptionMutators.clear();
		controllerMutators.clear();
		moveMutators.clear();
		locationMutators.clear();

		allMutators.clear();
	}

	public <T extends AgentState> boolean supportsState(Class<T> type, T value) {
		for (AgentMutator mutator : allMutators) {
			if (mutator.acceptsState(type, value))
				return true;
		}

		return false;
	}

	@Override
	public boolean onNextMove(Agent agent) {
		for (MoveMutator mut : moveMutators) {
			if (mut.overrideMove(agent)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onContact(Agent bumper, Agent bumpee) {
		for (ContactMutator mut : contactMutators) {
			mut.onContact(bumper, bumpee);
		}
	}

	@Override
	public void onStep(Agent agent, LocationDirection from, LocationDirection to) {
		for (StepMutator m : stepMutators) {
			m.onStep(agent, from, to);
		}
	}

	@Override
	public void onSpawn(Agent agent, Agent parent1, Agent parent2) {
		for (SpawnMutator mutator : spawnMutators) {
			mutator.onSpawn(agent, parent1, parent2);
		}
	}

	@Override
	public void onSpawn(Agent agent, Agent parent) {
		for (SpawnMutator mutator : spawnMutators) {
			mutator.onSpawn(agent, parent);
		}
	}

	@Override
	public void onSpawn(Agent agent) {
		for (SpawnMutator mutator : spawnMutators) {
			mutator.onSpawn(agent);
		}
	}

	@Override
	public void onDeath(Agent agent) {
		for (SpawnMutator mutator : spawnMutators) {
			mutator.onDeath(agent);
		}
	}

	@Override
	public void onConsumeAgent(Agent agent, Agent food) {
		for(ConsumptionMutator mutator : consumptionMutators) {
			mutator.onConsumeAgent(agent, food);
		}
	}

	@Override
	public void onConsumeFood(Agent agent, int foodType) {
		for(ConsumptionMutator mutator : consumptionMutators) {
			mutator.onConsumeFood(agent, foodType);
		}
	}

	@Override
	public void onEnergyChange(Agent agent, int delta, Cause cause) {
		for(EnergyMutator mutator : energyMutators) {
			mutator.onEnergyChange(agent, delta, cause);
		}
	}

	@Override
	public void onUpdate(Agent agent) {
		for(UpdateMutator mutator : updateMutators) {
			mutator.onUpdate(agent);
		}
	}

	@Override
	public void beforeControl(Agent agent, ControllerInput cInput) {
		for(ControllerInputMutator mutator : controllerMutators) {
			mutator.onControl(agent, cInput);
		}
	}

	@Override
	public LocationDirection onTryStep(Agent agent, LocationDirection from, LocationDirection originalTo) {
	    List<LocationDirection> possibles = new LinkedList<>();
		for(LocationMutator mutator : locationMutators) {
            LocationDirection newLoc = mutator.getNewLocation(agent, from, originalTo);
            if (!newLoc.equals(originalTo)) {
                possibles.add(newLoc);
            }
		}
		if (possibles.isEmpty()) {
		    return originalTo;
        } else {
		    return possibles.get(new Random().nextInt(possibles.size()));
        }
	}

	public List<String> logDataAgent(int type) {
		List<String> res = new LinkedList<String>();
		for (LoggingMutator mut : loggingMutators) {
			res.addAll(mut.logDataAgent(type));
		}
		return res;
	}

	public List<String> logDataTotal() {
		List<String> res = new LinkedList<String>();
		for (LoggingMutator mut : loggingMutators) {
			res.addAll(mut.logDataTotal());
		}
		return res;
	}

	public List<String> logHeaderAgent() {
		List<String> res = new LinkedList<String>();
		for (LoggingMutator mut : loggingMutators) {
			res.addAll(mut.logHeadersAgent());
		}
		return res;
	}

	public List<String> logHeaderTotal() {
		List<String> res = new LinkedList<String>();
		for (LoggingMutator mut : loggingMutators) {
			res.addAll(mut.logHeaderTotal());
		}
		return res;
	}

}
