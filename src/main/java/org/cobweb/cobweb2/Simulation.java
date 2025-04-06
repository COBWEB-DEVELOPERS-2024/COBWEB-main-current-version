package org.cobweb.cobweb2;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cobweb.cobweb2.core.Agent;
import org.cobweb.cobweb2.core.AgentListener;
import org.cobweb.cobweb2.core.AgentSimilarityCalculator;
import org.cobweb.cobweb2.core.SimulationInternals;
import org.cobweb.cobweb2.core.StateParameter;
import org.cobweb.cobweb2.core.StatePlugin;
import org.cobweb.cobweb2.core.Topology;
import org.cobweb.cobweb2.impl.AgentSpawner;
import org.cobweb.cobweb2.impl.ComplexAgent;
import org.cobweb.cobweb2.impl.ComplexEnvironment;
import org.cobweb.cobweb2.plugins.AgentState;
import org.cobweb.cobweb2.plugins.MutatorListener;
import org.cobweb.cobweb2.plugins.abiotic.AbioticMutator;
import org.cobweb.cobweb2.plugins.broadcast.PacketConduit;
import org.cobweb.cobweb2.plugins.disease.DiseaseMutator;
import org.cobweb.cobweb2.plugins.food.FoodGrowth;
import org.cobweb.cobweb2.plugins.fusion.FusionMutator;
import org.cobweb.cobweb2.plugins.genetics.GeneticsMutator;
import org.cobweb.cobweb2.plugins.gravity.GravityMutator;
import org.cobweb.cobweb2.plugins.learning.LearningMutator;
import org.cobweb.cobweb2.plugins.pd.PDMutator;
import org.cobweb.cobweb2.plugins.personalities.PersonalityMutator;
import org.cobweb.cobweb2.plugins.production.ProductionMapper;
import org.cobweb.cobweb2.plugins.stats.EnergyStats;
import org.cobweb.cobweb2.plugins.stats.StatsMutator;
import org.cobweb.cobweb2.plugins.swarm.SwarmMutator;
import org.cobweb.cobweb2.plugins.toxin.ToxinMutator;
import org.cobweb.cobweb2.plugins.vision.VisionMutator;
import org.cobweb.cobweb2.plugins.waste.WasteMutator;
import org.cobweb.cobweb2.ui.SimulationInterface;
import org.cobweb.util.RandomNoGenerator;


/**
 * This class provides the definitions for a user interface that is running
 * on a local machine.
 *
 */
public class Simulation implements SimulationInternals, SimulationInterface {

	public SimulationConfig simulationConfig;

	// TODO access level?
	public ComplexEnvironment theEnvironment;

	private RandomNoGenerator random;

	private int time = 0;

	private AgentSpawner agentSpawner;
	private List<Agent> agents = new LinkedList<Agent>();
	private int nextAgentId = 1;

	private AgentSimilarityCalculator similarityCalculator;

	// TODO: all of this should be in a collection

	private PDMutator pdMutator;
	private WasteMutator wasteMutator;
	public ProductionMapper prodMapper;
	private AbioticMutator abioticMutator;
	private SwarmMutator swarmMutator;
	private DiseaseMutator diseaseMutator;
	private FusionMutator fusionMutator;
	private ToxinMutator toxinMutator;
	public GeneticsMutator geneticMutator;
	public LearningMutator learningMutator;
	public StatsMutator statsMutator;
	private PersonalityMutator personalityMutator;
	private GravityMutator gravityMutator;
	private VisionMutator vision;


	/* return number of TYPES of agents in the environment */
	@Override
	public int getAgentTypeCount() {
		return simulationConfig.getAgentTypes();
	}

	@Override
	public long getTime() {
		return time;
	}

	public void resetTime() {
		time = 0;
	}

	/**
	 * Initialize the specified Environment class.  The environment is created using the
	 * environmentName.load method.
	 *
	 * @param environmentName Class name of the environment used in this simulation.
	 */
	private void InitEnvironment(String environmentName, boolean continuation) {
		try {
			@SuppressWarnings("unchecked")
			Class<ComplexEnvironment> environmentClass = (Class<ComplexEnvironment>) Class.forName(environmentName);

			if (continuation && theEnvironment != null && !theEnvironment.getClass().equals(environmentClass)) {
				throw new IllegalArgumentException("Cannot switch to different Environment/Agent type and continue simulation");
			}

			if (!continuation || theEnvironment == null) {
				theEnvironment = instantiateUsingSimconfig(environmentClass);
			}
		} catch (ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException ex) {
			throw new RuntimeException("Can't create Environment", ex);
		}
	}

	private <T> T instantiateUsingSimconfig(Class<T> clazz)
			throws IllegalAccessException, InstantiationException, InvocationTargetException {

		Constructor<?> ctor = clazz.getConstructors()[0];

		List<Object> args = new ArrayList<>();
		for (Class<?> pt : ctor.getParameterTypes()) {
			if (pt.isAssignableFrom(this.getClass())) {
				args.add(this);
			} else {
				Object arg = simulationConfig.getParam(pt);
				if (arg == null)
					throw new IllegalArgumentException("Could not bind argument type: " + pt.getName());
				args.add(arg);
			}
		}
		@SuppressWarnings("unchecked")
		T instance = (T) ctor.newInstance(args.toArray());
		return instance;
	}


	private int computeSplitCountInhomogeneous(int width) {
		int c = (int) Math.pow(2, width); // number of potential cells
		double x = 1.5;

		Map<Integer, Map<Integer, Double>> znMap = new LinkedHashMap<Integer, Map<Integer, Double>>();

		// Z1 = x
		Map<Integer, Double> Z1 = new LinkedHashMap<Integer, Double>();
		Z1.put(1, 1.0);
		znMap.put(1, Z1);

		int n = 4;
		int splitCount = 0;

		int n0 = 13;
		double k = 0.5;

		double decayWeight = 0.85;
		double structureWeight = 0.15;

		while (n <= c) {
			Map<Integer, Double> prevZ = znMap.get(n - 3);
			if (prevZ == null) break;

			Map<Integer, Double> Zn = polyPower(polyAddX(prevZ, x), 4);
			znMap.put(n, Zn);

			double An = Zn.containsKey(n) ? Zn.get(n) : 0.0;

			double totalCoeff = 0.0;
			for (double coeff : Zn.values()) {
				totalCoeff += coeff;
			}

			if (totalCoeff == 0.0) break;

			double decayComponent = 1.0 / (1.0 + Math.exp(k * (n - n0)));
			double structureComponent = An / totalCoeff;

			double p = decayWeight * decayComponent + structureWeight * structureComponent;
			p = Math.min(p, 1.0);

			// System.out.printf("n = %d, An = %.2f, total = %.2f, decay = %.4f, structure = %.6f, p = %.4f%n",
			// 		n, An, totalCoeff, decayComponent, structureComponent, p);

			if (Math.random() < p) {
				splitCount++;
				n += 3;
			} else {
				break;
			}
		}

		return splitCount;
	}



	// Add x to the polynomial: Zn(x) = Zn + x
	private Map<Integer, Double> polyAddX(Map<Integer, Double> poly, double x) {
		Map<Integer, Double> result = new LinkedHashMap<Integer, Double>(poly);
		double coeff = result.containsKey(1) ? result.get(1) : 0.0;
		result.put(1, coeff + 1.0);
		return result;
	}


	// Multiply two polynomials
	private Map<Integer, Double> polyMultiply(Map<Integer, Double> a, Map<Integer, Double> b) {
		Map<Integer, Double> result = new LinkedHashMap<Integer, Double>();
		for (Map.Entry<Integer, Double> termA : a.entrySet()) {
			for (Map.Entry<Integer, Double> termB : b.entrySet()) {
				int newExp = termA.getKey() + termB.getKey();
				double newCoeff = termA.getValue() * termB.getValue();
				double existing = result.containsKey(newExp) ? result.get(newExp) : 0.0;
				result.put(newExp, existing + newCoeff);
			}
		}
		return result;
	}


	// Raise a polynomial to an integer power
	private Map<Integer, Double> polyPower(Map<Integer, Double> base, int power) {
		Map<Integer, Double> result = new LinkedHashMap<>();
		result.put(0, 1.0);

		for (int i = 0; i < power; i++) {
			result = polyMultiply(result, base);
		}
		return result;
	}

	// Evaluate polynomial at a specific x
	private double evalPoly(Map<Integer, Double> poly, double x) {
		double result = 0;
		for (Map.Entry<Integer, Double> term : poly.entrySet()) {
			result += term.getValue() * Math.pow(x, term.getKey());
		}
		return result;
	}


	/**
	 * Initialize the specified environment class with state data read from the
	 * reader. It first adds the various mutators used to modify actions in the
	 * event that the agent spawns, steps, or contacts another agent.  It then
	 * uses the simulation parameters to modify the properties of the mutators.
	 * It then initializes the simulation environment (InitEnvironment) using
	 * the simulation configuration object.  Finally, it will start the scheduler,
	 * which will start the simulation.  This is a private helper to the
	 * LocalUIInterface constructor.
	 */
	//TODO use reflection to automate this
	public void load(SimulationConfig p) {
		this.simulationConfig = p;
		agentSpawner = new AgentSpawner(ComplexAgent.class.getName(), this);

		// 0 = use default seed
		if (p.randomSeed == 0)
			random = new RandomNoGenerator();
		else
			random = new RandomNoGenerator(p.randomSeed);

		// Create new environment or reuse current if continuing simulation
		InitEnvironment(p.environmentName, p.isContinuation());

		// Update environment settings
		theEnvironment.setParams(p.envParams, p.agentParams, p.keepOldAgents, p.keepOldArray, p.keepOldDrops);

		// Update environment plugins with new environment settings
		if (!p.isContinuation()) {
			theEnvironment.addPlugin(new FoodGrowth(this));
			theEnvironment.addPlugin(new PacketConduit());
		} else {
			PacketConduit packetConduit = theEnvironment.getPlugin(PacketConduit.class);
			if (!p.keepOldPackets)
				packetConduit.clearPackets();
		}


		// Remove all agent mutators if removing old agents
		if (!p.keepOldAgents) {
			nextAgentId = 1;
			mutatorListener.clearMutators();
			aiStatePlugins.clear();
			agents.clear();
			geneticMutator = null;
			learningMutator = null;
			diseaseMutator = null;
			toxinMutator = null;
			abioticMutator = null;
			swarmMutator = null;
			prodMapper = null;
			wasteMutator = null;
			statsMutator = null;
			vision = null;
			pdMutator = null;
			energyStats = null;
			personalityMutator = null;
			fusionMutator = null;
			gravityMutator = null;
		}


		// Create agent plugins if start of a new simulation
		if (pdMutator == null) {
			pdMutator = new PDMutator(this);
			mutatorListener.addMutator(pdMutator);
		}
		if (wasteMutator == null) {
			wasteMutator = new WasteMutator(this);
			mutatorListener.addMutator(wasteMutator);
		}
		if (prodMapper == null) {
			prodMapper = new ProductionMapper(this);
			aiStatePlugins.add(prodMapper);
			mutatorListener.addMutator(prodMapper);
		}
		if (abioticMutator == null) {
			abioticMutator = new AbioticMutator();
			aiStatePlugins.add(abioticMutator);
			mutatorListener.addMutator(abioticMutator);
		}
		if (swarmMutator == null) {
			swarmMutator = new SwarmMutator();
			aiStatePlugins.add(swarmMutator);
			mutatorListener.addMutator(swarmMutator);
		}
		if (diseaseMutator == null) {
			diseaseMutator = new DiseaseMutator();
			mutatorListener.addMutator(diseaseMutator);
		}
		if (toxinMutator == null) {
			toxinMutator = new ToxinMutator(this);
			mutatorListener.addMutator(toxinMutator);
		}
		if (geneticMutator == null) {
			geneticMutator = new GeneticsMutator();
			mutatorListener.addMutator(geneticMutator);
			similarityCalculator = geneticMutator;
		}
		if (learningMutator == null) {
			learningMutator = new LearningMutator(this);
			mutatorListener.addMutator(learningMutator);
		}
		if (vision == null) {
			vision = new VisionMutator();
			mutatorListener.addMutator(vision);
		}
		if (statsMutator == null) {
			statsMutator = new StatsMutator(this);
			mutatorListener.addMutator(statsMutator);
		}
		if (energyStats == null) {
			energyStats = new EnergyStats();
			mutatorListener.addMutator(energyStats);
		}
		if (personalityMutator == null) {
			personalityMutator = new PersonalityMutator(this);
			mutatorListener.addMutator(personalityMutator);
		}
		if (fusionMutator == null) {
			fusionMutator = new FusionMutator();
			mutatorListener.addMutator(fusionMutator);
		}

		if (gravityMutator == null) {
			gravityMutator = new GravityMutator();
			mutatorListener.addMutator(gravityMutator);
		}


		// Load environment plugin settings
		FoodGrowth foodGrowth = theEnvironment.getPlugin(FoodGrowth.class);
		foodGrowth.setParams(theEnvironment, p.foodParams);
		PacketConduit packetConduit = theEnvironment.getPlugin(PacketConduit.class);
		packetConduit.setParams(theEnvironment.topology);

		// plugins are keyed against class, they get replaced
		theEnvironment.addPlugin(abioticMutator);
		theEnvironment.addPlugin(energyStats);
		theEnvironment.addPlugin(prodMapper);
		theEnvironment.addPlugin(toxinMutator);
		theEnvironment.addPlugin(gravityMutator);

		// Load agent plugin settings
		pdMutator.setParams(p.pdParams);
		wasteMutator.setParams(p.wasteParams, theEnvironment);
		prodMapper.setParams(simulationConfig.prodParams, theEnvironment, p.keepOldDrops);
		abioticMutator.setParams(this, p.abioticParams);
		swarmMutator.setParams(this, simulationConfig.swarmParams, theEnvironment);
		diseaseMutator.setParams(this, p.diseaseParams, p.getAgentTypes());
		toxinMutator.setParams(p.toxinParams, p.getAgentTypes());
		geneticMutator.setParams(this, p.geneticParams, p.getAgentTypes());
		learningMutator.setParams(p.learningParams);
		personalityMutator.setParams(p.personalityParams);
		fusionMutator.setParams(this, p.fusionParams);
		gravityMutator.setParams(this, p.gravityParams);


		// Update AI state plugins
		setupAIStatePlugins();


		// This is where the setup ends and simulation begins

		// Create initial environment state, spawn stones, plugins
		theEnvironment.loadNew();
		int inhomogeneousSplits = computeSplitCountInhomogeneous(simulationConfig.envParams.width);
		// System.out.println("Inhomogeneous Split Count: " + inhomogeneousSplits);
		theEnvironment.applyInhomogeneousSplits(inhomogeneousSplits);

		// Create initial agent population
		if (p.spawnNewAgents) {
			theEnvironment.loadNewAgents();
		}
	}

	@Override
	public void step() {

		theEnvironment.update();

		// TODO synchronize on something other than environment?
		synchronized(theEnvironment) {
			for (Agent agent : new LinkedList<Agent>(agents)) {
				agent.update();

				mutatorListener.onUpdate(agent);

				if (!agent.isAlive())
					agents.remove(agent);
			}
		}

		time++;
	}


	@Override
	public void addAgent(Agent agent) {
		agents.add(agent);
		agent.id = nextAgentId++;
	}

	@Override
	public RandomNoGenerator getRandom() {
		return random;
	}

	@Override
	public Agent newAgent(int type) {
		ComplexAgent agent = (ComplexAgent) agentSpawner.spawn(type);
		agent.setController(simulationConfig.controllerParams.createController(this, type));
		return agent;
	}

	@Override
	public Topology getTopology() {
		return theEnvironment.topology;
	}

	@Override
	public StateParameter getStateParameter(String name) {
		return aiStateMap.get(name);
	}

	private Map<String, StateParameter> aiStateMap = new LinkedHashMap<String, StateParameter>();

	private List<StatePlugin> aiStatePlugins = new LinkedList<StatePlugin>();

	private void setupAIStatePlugins() {
		aiStateMap.clear();
		for (StatePlugin plugin : aiStatePlugins) {
			for (StateParameter param : plugin.getParameters()) {
				aiStateMap.put(param.getName(), param);
			}
		}
	}

	@Override
	public Set<String> getStatePluginKeys() {
		return aiStateMap.keySet();
	}

	@Override
	public AgentSimilarityCalculator getSimilarityCalculator() {
		return similarityCalculator;
	}

	public MutatorListener mutatorListener = new MutatorListener();

	private EnergyStats energyStats;

	@Override
	public AgentListener getAgentListener() {
		return mutatorListener;
	}

	/**
	 * Checks whether given AgentState can be used in the current simulation configuration
	 * @param type specific Class of AgentState
	 * @param value value of AgentState
	 * @return true if AgentState supported in current configuration
	 */
	public <T extends AgentState> boolean supportsState(Class<T> type, T value) {
		return mutatorListener.supportsState(type, value);
	}

}
