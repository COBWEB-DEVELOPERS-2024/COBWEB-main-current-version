package org.cobweb.cobweb2.ui.swing.discretizedgravity;

import org.cobweb.cobweb2.core.Topology;
import org.cobweb.cobweb2.ui.swing.DisplayOverlay;
import org.cobweb.cobweb2.ui.swing.config.DisplaySettings;

import java.awt.*;
import java.util.Collection;
import org.cobweb.cobweb2.core.Location;
import org.cobweb.cobweb2.impl.ComplexEnvironment;

public class DiscretizedGravityOverlay implements DisplayOverlay {
    private DiscretizedGravitySplit split;
    private ComplexEnvironment environment;

    public DiscretizedGravityOverlay(DiscretizedGravitySplit split, ComplexEnvironment environment) {
        this.split = split;
        this.environment = environment;
    }

    private void drawSplit(Graphics g, Collection<Location> locations) {
        for (Location loc : locations) {
            int size = environment.getCellSize(loc) * 10; // Scale for visibility
            g.drawRect(loc.x * size, loc.y * size, size, size);
        }
    }

    @Override
    public void draw(Graphics g, int tileWidth, int tileHeight, Topology topology, DisplaySettings settings) {
        g.setColor(Color.BLACK);

        // ✅ Prevent drawing outside the valid grid area
        int gridWidth = environment.data.width * tileWidth;
        int gridHeight = environment.data.height * tileHeight;
        g.setClip(0, 0, gridWidth, gridHeight);  // Clip rendering to grid bounds

        drawSplit(g, environment.getAllGridLocations());
    }

}
