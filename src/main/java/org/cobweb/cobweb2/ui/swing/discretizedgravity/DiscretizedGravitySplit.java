package org.cobweb.cobweb2.ui.swing.discretizedgravity;

import org.cobweb.cobweb2.core.Location;

import java.util.HashMap;
import java.util.Map;

public class DiscretizedGravitySplit {

    private boolean isSplit = false;
    private DiscretizedGravitySplit upLeft = null;
    private DiscretizedGravitySplit upRight = null;
    private DiscretizedGravitySplit downLeft = null;
    private DiscretizedGravitySplit downRight = null;
    private final Map<Location, Integer> cellSizes = new HashMap<>();

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

    public void split() {
        isSplit = true;
        upLeft = new DiscretizedGravitySplit();
        upRight = new DiscretizedGravitySplit();
        downLeft = new DiscretizedGravitySplit();
        downRight = new DiscretizedGravitySplit();
    }

    public void unSplit() {
        isSplit = false;
        upLeft = null;
        upRight = null;
        downLeft = null;
        downRight = null;
    }

    public void setCellSize(Location loc, int size) {
        cellSizes.put(loc, size);
    }

    public int getCellSize(Location loc) {
        return cellSizes.getOrDefault(loc, 1);
    }
}
