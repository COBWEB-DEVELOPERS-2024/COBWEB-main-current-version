// vector field extension for COBWEB, currently in progress
// Developed by Bala Venkataraman

package org.cobweb.cobweb2.plugins.vectorfield;


import javafx.util.Pair;
import org.cobweb.cobweb2.Simulation;
import org.cobweb.cobweb2.core.*;
import org.cobweb.cobweb2.impl.ComplexAgent;
import org.cobweb.cobweb2.impl.ComplexEnvironment;
import org.cobweb.cobweb2.plugins.AgentState;
import org.cobweb.cobweb2.plugins.EnvironmentMutator;
import org.cobweb.cobweb2.plugins.MoveMutator;

import java.util.ArrayList;

public class VectorField implements MoveMutator, EnvironmentMutator {

    public VectorField() {

    }

    private VectorFieldParams params;
    private SimulationTimeSpace sim;

    public double[][][] gradientArray;
    // this class is still in progress. The function has to fill in the gradient array with gradients of a multivariable function generated in a separate program
    // the format of the file (XML, JSON, etc.) is undecided, so this will be implemented later
    // all other functionalities are complete
    public void setParams(SimulationTimeSpace sim, VectorFieldParams params) {
        this.sim = sim;
        this.params = params;

        int w = sim.getTopology().width;
        int h = sim.getTopology().height;
        gradientArray = new double[w][h][2];

    }


    private Pair<Double, Double> calculateGravityProbability(Agent agent) {
        double forceX = 0; // + right, - left
        double forceY = 0; // + down, - up

        Location agentLoc = agent.getPosition();
        double[] g_vec = gradientArray[agentLoc.x][agentLoc.y];
        forceX = g_vec[0];
        forceY = g_vec[1];
        return new Pair<>(forceX, forceY);
    }

    @Override
    public boolean overrideMove(Agent agent) {

        if (params.vecFieldEnabled) {
            Pair<Double, Double> res = calculateGravityProbability(agent);
            double forceX = res.getKey();
            double forceY = res.getValue();
            // the gradient vector is the vector the agent will want to go down. With a probability of the X component it will travel in that direction. With the probability of the Y coordinate it will travel vertically.
            int moveX = (int) (forceX / Math.abs(forceX));
            int moveY = (int) (forceY / Math.abs(forceY));
            double random = Math.random();
            boolean moved = false;
            Direction to;
            ComplexAgent ag = (ComplexAgent) agent;
            Direction agentDir;

            if (random < Math.abs(forceX)) {
                to = moveX > 0 ? Topology.EAST : Topology.WEST;
                agentDir = agent.getPosition().direction;
                if (sim.getTopology().getRotationBetween(agentDir, to).equals(Rotation.Right)) {
                    ag.turnRight();
                } else if (sim.getTopology().getRotationBetween(agentDir, to).equals(Rotation.Left)) {
                    ag.turnLeft();
                }
                agentDir = agent.getPosition().direction;
                if (sim.getTopology().getRotationBetween(agentDir, to).equals(Rotation.Right)) {
                    ag.turnRight();
                } else if (sim.getTopology().getRotationBetween(agentDir, to).equals(Rotation.Left)) {
                    ag.turnLeft();
                }
                ag.step();
                moved = true;
            }
                if (random < Math.abs(forceY)) {
                    to = moveY > 0 ? Topology.NORTH : Topology.SOUTH;
                    agentDir = agent.getPosition().direction;
                    if (sim.getTopology().getRotationBetween(agentDir, to).equals(Rotation.Right)) {
                        ag.turnRight();
                    } else if (sim.getTopology().getRotationBetween(agentDir, to).equals(Rotation.Left)) {
                        ag.turnLeft();
                    }
                    agentDir = agent.getPosition().direction;
                    if (sim.getTopology().getRotationBetween(agentDir, to).equals(Rotation.Right)) {
                        ag.turnRight();
                    } else if (sim.getTopology().getRotationBetween(agentDir, to).equals(Rotation.Left)) {
                        ag.turnLeft();
                    }
                    ag.step();
                    moved = true;
                }
                return moved;
    }else {
            return false;
        }
    }

    @Override
    public void loadNew() {}; // Create the initial array of point masses

    @Override
    public void update() {}

    @Override
    public <T extends AgentState> boolean acceptsState(Class<T> type, T value) {
        return false;
    }
}
