package org.cobweb.cobweb2.ui.swing.discretizedgravity;

public class DiscretizedGravityStructure {
    private final DiscretizedGravitySplit baseSplit;
    private int numSplits = 0;
    private final int topologySize;

    public DiscretizedGravityStructure(int topologySize) {
        this.baseSplit = new DiscretizedGravitySplit(null, 0, topologySize, 0, topologySize);
        this.topologySize = topologySize;
    }

    public DiscretizedGravitySplit getBaseSplit() {
        return baseSplit;
    }

    public int getNumSplits() {
        return numSplits;
    }

    public int getNumPlaquettes() {
        return 3 * numSplits + 1;
    }

    public float getAvgPlaquetteSize() {
        return (float) (topologySize * topologySize) / getNumPlaquettes();
    }

    public DiscretizedGravitySplit getSplitAtIndex(int index) {
        // Using an array of an int to basically get a pointer to it
        return getSplitAtIndex(baseSplit, new int[]{0}, index);
    }

    private DiscretizedGravitySplit getSplitAtIndex(DiscretizedGravitySplit currSplit, int[] currIndex, int targetIndex) {
        // Base case
        if (currSplit == null) {
            return null;
        }
        // Check if the current split is a leaf node, then see if it's at the target index
        if (!(currSplit.getIsSplit())) {
            if (currIndex[0] == targetIndex) {
                return currSplit;
            }
            currIndex[0]++;
        }
        // Recurse on the 4 inner splits
        DiscretizedGravitySplit upLeftResult = getSplitAtIndex(currSplit.getUpLeft(), currIndex, targetIndex);
        if (upLeftResult != null) {
            return upLeftResult;
        }
        DiscretizedGravitySplit upRightResult = getSplitAtIndex(currSplit.getUpRight(), currIndex, targetIndex);
        if (upRightResult != null) {
            return upRightResult;
        }
        DiscretizedGravitySplit downLeftResult = getSplitAtIndex(currSplit.getDownLeft(), currIndex, targetIndex);
        if (downLeftResult != null) {
            return downLeftResult;
        }
        return getSplitAtIndex(currSplit.getDownRight(), currIndex, targetIndex);
    }

    public void makeSplitAtIndex(int index) {
        if (index >= getNumPlaquettes()) {
            System.out.println("Could not make split at index " + index);
            return;
        }
        DiscretizedGravitySplit splitAtIndex = getSplitAtIndex(index);
        splitAtIndex.split();
        numSplits++;
//        System.out.println("Num plaquettes: " + getNumPlaquettes());
    }

    public void makeUnSplitAtIndexParent(int index) {
        if (index >= getNumPlaquettes()) {
            System.out.println("Could not make split at index " + index);
            return;
        }
        DiscretizedGravitySplit splitAtIndex = getSplitAtIndex(index);
        numSplits -= splitAtIndex.getParentSplit().unSplit();
//        System.out.println("Num plaquettes: " + getNumPlaquettes());
    }

    public void makeRandomSplits(int splitAmount) {
        for (int i = 0; i < splitAmount; i++) {
            int randomIndex = (int) (Math.random() * getNumPlaquettes());
            makeSplitAtIndex(randomIndex);
        }
    }
}
