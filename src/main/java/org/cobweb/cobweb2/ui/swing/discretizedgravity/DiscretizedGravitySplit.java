package org.cobweb.cobweb2.ui.swing.discretizedgravity;

public class DiscretizedGravitySplit {
    private boolean isSplit = false;
    private DiscretizedGravitySplit upLeft = null;
    private DiscretizedGravitySplit upRight = null;
    private DiscretizedGravitySplit downLeft = null;
    private DiscretizedGravitySplit downRight = null;

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
}
