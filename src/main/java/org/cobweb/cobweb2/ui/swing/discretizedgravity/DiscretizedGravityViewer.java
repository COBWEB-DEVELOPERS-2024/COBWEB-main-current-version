package org.cobweb.cobweb2.ui.swing.discretizedgravity;

import org.cobweb.cobweb2.Simulation;
import org.cobweb.cobweb2.ui.swing.DisplayOverlay;
import org.cobweb.cobweb2.ui.swing.DisplayPanel;
import org.cobweb.cobweb2.ui.swing.OverlayGenerator;
import org.cobweb.cobweb2.ui.swing.OverlayPluginViewer;
import org.cobweb.cobweb2.impl.ComplexEnvironment;
import org.cobweb.cobweb2.core.Location;

import java.util.Timer;
import java.util.TimerTask;

public class DiscretizedGravityViewer extends OverlayPluginViewer<DiscretizedGravityViewer> implements OverlayGenerator {
    private Simulation simulation;
    private ComplexEnvironment environment;
    private DiscretizedGravitySplit split;
    private Timer resizeTimer;

    public DiscretizedGravityViewer(DisplayPanel panel, Simulation simulation, ComplexEnvironment environment) {
        super(panel);
        this.simulation = simulation;
        this.environment = environment;
        this.split = new DiscretizedGravitySplit();
        startResizing();
    }

    @Override
    public DisplayOverlay getDrawInfo(Simulation sim) {
        return new DiscretizedGravityOverlay(this.split, environment);
    }

    @Override
    protected DiscretizedGravityViewer createOverlay() {
        return this;
    }

    @Override
    public String getName() {
        return "Discretized Gravity";
    }

    private void updateEnergyData() {
        for (Location loc : environment.getAllGridLocations()) {
            int energy = environment.getAggregatedEnergy(loc);
            split.setCellSize(loc, energy / 10);
        }
    }

    /**
     * Periodically resizes the grid every 500ms.
     */
    private void startResizing() {
        resizeTimer = new Timer(true);
        resizeTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateEnergyData();
            }
        }, 0, 500);
    }
}
