package org.cobweb.cobweb2.ui.swing.discretizedgravity;

import org.cobweb.cobweb2.core.Topology;
import org.cobweb.cobweb2.ui.swing.DisplayOverlay;
import org.cobweb.cobweb2.ui.swing.config.DisplaySettings;

import java.awt.*;

public class DiscretizedGravityOverlay implements DisplayOverlay {
    private DiscretizedGravitySplit split;

    public DiscretizedGravityOverlay(DiscretizedGravitySplit split) {
        this.split = split;
    }

    private void drawSplit(Graphics g, DiscretizedGravitySplit currSplit, int x, int y, int width, int height) {
        // Base case
        if (!currSplit.getIsSplit()) {
            return;
        }

        // Draw the split
        int newWidth = width / 2;
        int newHeight = height / 2;
        g.drawLine(x + newWidth, y, x + newWidth, y + height);
        g.drawLine(x, y + newHeight, x + width, y + newHeight);

        // Recurse on the splits
        drawSplit(g, currSplit.getUpLeft(), x, y, newWidth, newHeight);
        drawSplit(g, currSplit.getUpRight(), x + newWidth, y, newWidth, newHeight);
        drawSplit(g, currSplit.getDownLeft(), x, y + newHeight, newWidth, newHeight);
        drawSplit(g, currSplit.getDownRight(), x + newWidth, y + newHeight, newWidth, newHeight);
    }

    @Override
    public void draw(Graphics g, int tileWidth, int tileHeight, Topology topology, DisplaySettings settings) {
        // Draw the outer border
        g.drawLine(0, 0, tileWidth * topology.width, 0);
        g.drawLine(0, 0, 0, tileWidth * topology.height);
        g.drawLine(tileWidth * topology.width, 0, tileWidth * topology.width, tileWidth * topology.height);
        g.drawLine(0, tileWidth * topology.height, tileWidth * topology.width, tileWidth * topology.height);

        // recursively draw the splits
        drawSplit(g, this.split, 0, 0, tileWidth * topology.width, tileHeight * topology.height);
    }
}
