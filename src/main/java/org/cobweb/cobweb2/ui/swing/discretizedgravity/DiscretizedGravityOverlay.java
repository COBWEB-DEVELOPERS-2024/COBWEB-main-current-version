package org.cobweb.cobweb2.ui.swing.discretizedgravity;

import org.cobweb.cobweb2.core.Topology;
import org.cobweb.cobweb2.impl.ComplexEnvironment;
import org.cobweb.cobweb2.ui.swing.DisplayOverlay;
import org.cobweb.cobweb2.ui.swing.config.DisplaySettings;

import java.awt.*;

public class DiscretizedGravityOverlay implements DisplayOverlay {
    private ComplexEnvironment environment;

    public DiscretizedGravityOverlay(ComplexEnvironment environment) {
        this.environment = environment;
    }

    private void drawSplit(Graphics g, int currSplitNum, int x, int y, int width, int height) {
        // Base case
        if (currSplitNum <= 1) {
            return;
        }

        // Draw the split
        int newWidth = width / 2;
        int newHeight = height / 2;
        g.drawLine(x + newWidth, y, x + newWidth, y + height);
        g.drawLine(x, y + newHeight, x + width, y + newHeight);

        // Recurse on the splits
        drawSplit(g, currSplitNum - 1, x, y, newWidth, newHeight);
        drawSplit(g, currSplitNum - 1, x + newWidth, y, newWidth, newHeight);
        drawSplit(g, currSplitNum - 1, x, y + newHeight, newWidth, newHeight);
        drawSplit(g, currSplitNum - 1, x + newWidth, y + newHeight, newWidth, newHeight);
    }

    @Override
    public void draw(Graphics g, int tileWidth, int tileHeight, Topology topology, DisplaySettings settings) {
        // Draw the outer border
        g.drawLine(0, 0, tileWidth * topology.width, 0);
        g.drawLine(0, 0, 0, tileWidth * topology.height);
        g.drawLine(tileWidth * topology.width, 0, tileWidth * topology.width, tileWidth * topology.height);
        g.drawLine(0, tileWidth * topology.height, tileWidth * topology.width, tileWidth * topology.height);

        // recursively draw the splits
        drawSplit(g, environment.getSplitCount(), 0, 0, tileWidth * topology.width, tileHeight * topology.height);
    }
}
