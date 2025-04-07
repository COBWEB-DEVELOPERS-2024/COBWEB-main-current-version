package org.cobweb.cobweb2.ui.swing.discretizedgravity;

import org.cobweb.cobweb2.core.Agent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DiscretizedGravitySplit {
    private boolean isSplit = false;
    private DiscretizedGravitySplit upLeft = null;
    private DiscretizedGravitySplit upRight = null;
    private DiscretizedGravitySplit downLeft = null;
    private DiscretizedGravitySplit downRight = null;
    private final DiscretizedGravitySplit parentSplit;
    private final int xLowerBound;
    private final int xUpperBound;
    private final int yLowerBound;
    private final int yUpperBound;

    public DiscretizedGravitySplit(DiscretizedGravitySplit parentSplit, int xLowerBound, int xUpperBound, int yLowerBound, int yUpperBound) {
        this.parentSplit = parentSplit;
        this.xLowerBound = xLowerBound;
        this.xUpperBound = xUpperBound;
        this.yLowerBound = yLowerBound;
        this.yUpperBound = yUpperBound;
    }

    public boolean getIsSplit() {
        return isSplit;
    }

    public DiscretizedGravitySplit getUpLeft() {
        return upLeft;
    }

    public DiscretizedGravitySplit getUpRight() {
        return upRight;
    }

    public DiscretizedGravitySplit getDownLeft() {
        return downLeft;
    }

    public DiscretizedGravitySplit getDownRight() {
        return downRight;
    }

    public DiscretizedGravitySplit getParentSplit() {
        return parentSplit;
    }

    public int getXLowerBound() {
        return xLowerBound;
    }

    public int getXUpperBound() {
        return xUpperBound;
    }

    public int getYLowerBound() {
        return yLowerBound;
    }

    public int getYUpperBound() {
        return yUpperBound;
    }

    private boolean agentInside(Agent agent) {
        boolean insideX = xLowerBound <= agent.getPosition().x && agent.getPosition().x < xUpperBound;
        boolean insideY = yLowerBound <= agent.getPosition().y && agent.getPosition().y < yUpperBound;
        return insideX && insideY;
    }

    public Collection<Agent> getAgents(Collection<Agent> agents) {
        List<Agent> result = new ArrayList<Agent>();
        for (Agent agent : agents) {
            if (agentInside(agent)) {
                result.add(agent);
            }
        }
        return result;
    }

    public void split() {
        isSplit = true;
        int xMiddle = xLowerBound + (xUpperBound - xLowerBound) / 2;
        int yMiddle = yLowerBound + (yUpperBound - yLowerBound) / 2;
        upLeft = new DiscretizedGravitySplit(this, xLowerBound, xMiddle, yLowerBound, yMiddle);
        upRight = new DiscretizedGravitySplit(this, xMiddle, xUpperBound, yLowerBound, yMiddle);
        downLeft = new DiscretizedGravitySplit(this, xLowerBound, xMiddle, yMiddle, yUpperBound);
        downRight = new DiscretizedGravitySplit(this, xMiddle, xUpperBound, yMiddle, yUpperBound);
    }

    public int unSplit() {
        if (isSplit) {
            int removedSplits = 1;
            // Unsplit inner splits so we don't get memory leakage, keep track of how many
            removedSplits += upLeft.unSplit();
            removedSplits += upRight.unSplit();
            removedSplits += downLeft.unSplit();
            removedSplits += downRight.unSplit();
            // Unsplit current split, set inner splits to null (so garbage collection can free them)
            isSplit = false;
            upLeft = null;
            upRight = null;
            downLeft = null;
            downRight = null;
            return removedSplits;
        }
        return 0;
    }
}
