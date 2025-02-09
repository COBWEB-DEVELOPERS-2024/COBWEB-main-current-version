package org.cobweb.cobweb2.ui.swing.discretizedgravity;

import org.cobweb.cobweb2.Simulation;
import org.cobweb.cobweb2.impl.ComplexEnvironment;
import org.cobweb.cobweb2.ui.swing.DisplayOverlay;
import org.cobweb.cobweb2.ui.swing.DisplayPanel;
import org.cobweb.cobweb2.ui.swing.OverlayGenerator;
import org.cobweb.cobweb2.ui.swing.OverlayPluginViewer;

import java.util.List;

public class DiscretizedGravityViewer extends OverlayPluginViewer<DiscretizedGravityViewer> implements OverlayGenerator {
    private Simulation simulation;
    private ComplexEnvironment environment;
    private DiscretizedGravitySplit split;

    public DiscretizedGravityViewer(DisplayPanel panel, Simulation simulation) {
        super(panel);
        this.simulation = simulation;
        this.environment = simulation.theEnvironment;

        // default split stuff
        this.split = new DiscretizedGravitySplit();
        this.split.split();
        this.split.getUpLeft().split();
        this.split.getDownRight().split();
        this.split.getDownRight().getDownLeft().split();
    }

    @Override
    public DisplayOverlay getDrawInfo(Simulation sim) {
        return new DiscretizedGravityOverlay(this.split);
    }

    @Override
    protected DiscretizedGravityViewer createOverlay() {
        return this;
    }

    @Override
    public String getName() {
        return "Discretized Gravity";
    }
}
