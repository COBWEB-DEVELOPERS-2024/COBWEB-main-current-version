package org.cobweb.cobweb2.ui.swing;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.cobweb.cobweb2.Simulation;
import org.cobweb.cobweb2.core.Agent;
import org.cobweb.cobweb2.core.Location;
import org.cobweb.cobweb2.core.Topology;
import org.cobweb.cobweb2.impl.ComplexAgent;
import org.cobweb.cobweb2.plugins.stats.AgentStatistics;
import org.cobweb.cobweb2.ui.swing.config.DisplaySettings;
import org.cobweb.util.Point2D;

/**
 * DrawInfo stores the frame data for the draw method to display. DrawInfo's
 * are built by calling newTileColors and newAgent in response to a
 * getDrawInfo call on the Environment or on an Agent. Note that DrawInfo is
 * a private class, and only LocalUIInterface knows of it's existence. For
 * this reason, DrawInfo is local to LocalUIInterface, and LocalUIInterface
 * reads DrawInfo members directly.
 */
class DrawInfo {

	private Topology topology;

	/**
	 * width * height array of colors for the tiles; The color for a
	 * specific tile at (x,y) is tileColors[y * width * + x]
	 */
	private java.awt.Color[] tileColors;

	/** Linked list of AgentDrawInfo for the display of agents. */
	private List<AgentDrawInfo> agents = new LinkedList<AgentDrawInfo>();

	private List<PathDrawInfo> paths = new LinkedList<PathDrawInfo>();

	private List<DropDrawInfo> drops = new LinkedList<DropDrawInfo>();

	private static final Color COLOR_GRIDLINES = Color.lightGray;

	private DisplaySettings displaySettings;

	private List<DisplayOverlay> overlays = new ArrayList<>();

	/**
	 * Construct a DrawInfo width specific width, height and tile colors.
	 * The tiles array is not copied; the caller is assumed to "give" the
	 * array to the drawing info, and not keep any local references around.
	 */
	DrawInfo(Simulation sim, List<ComplexAgent> observedAgents, DisplaySettings dispSettings, Collection<OverlayGenerator> gens) {
		this.displaySettings = dispSettings;
		this.topology = sim.theEnvironment.topology;
		tileColors = new Color[topology.width * topology.height];

		int tileIndex = 0;
		for (int y = 0; y < topology.height; ++y) {
			for (int x = 0; x < topology.width; ++x) {
				Location currentPos = new Location(x, y);

				if (sim.theEnvironment.hasStone(currentPos))
					tileColors[tileIndex++] = java.awt.Color.darkGray;

				else if (sim.theEnvironment.hasFood(currentPos))
					tileColors[tileIndex++] = displaySettings.agentColor.getColor(sim.theEnvironment.getFoodType(currentPos), sim.getAgentTypeCount());

				else
					tileColors[tileIndex++] = java.awt.Color.white;
			}
		}


		for (Agent a : sim.theEnvironment.getAgents()) {
			agents.add(new AgentDrawInfo((ComplexAgent) a, displaySettings.agentColor, sim));
		}

		for (ComplexAgent observedAgent: observedAgents) {
			AgentStatistics stats = observedAgent.getState(AgentStatistics.class);
			if (stats.path != null)
				paths.add(new PathDrawInfo(stats.path));
		}

		for (int y = 0; y < topology.height; ++y) {
			for (int x = 0; x < topology.width; ++x) {
				Location currentPos = new Location(x, y);
				if (sim.theEnvironment.hasDrop(currentPos)){
					drops.add(new DropDrawInfo(new Point2D(x, y), sim.theEnvironment.getDrop(currentPos)));
				}
			}
		}

		for (OverlayGenerator g : gens) {
			overlays.add(g.getDrawInfo(sim));
		}
	}



	/** Draw the tiles and the agents. */
	void draw(java.awt.Graphics g, int tileWidth, int tileHeight) {
		// Tiles
		int tileIndex = 0;
		for (int y = 0; y < topology.height; ++y) {
			for (int x = 0; x < topology.width; ++x) {
				g.setColor(tileColors[tileIndex++]);
				g.fillRect(x * tileWidth + 1, y * tileHeight + 1, tileWidth - 1, tileHeight - 1);
			}
		}

		int half =(int)( tileWidth / 2.0f + 0.5f);

		for (DropDrawInfo drop : drops) {
			int x = drop.pos.x;
			int y = drop.pos.y;
			g.setColor(drop.col);
			g.fillRect(x * tileWidth + 0, y * tileHeight + 0, half, half);
			g.fillRect(x * tileWidth + half, y * tileHeight + half, half, half);
		}

		// Grid lines
		g.setColor(DrawInfo.COLOR_GRIDLINES);
		int totalWidth = tileWidth * topology.width;
		for (int y = 0; y <= topology.height; y++) {
			g.drawLine(0, y * tileHeight, totalWidth, y * tileHeight);
		}
		int totalHeight = tileHeight * topology.height;
		for (int x = 0; x <= topology.width; x++) {
			g.drawLine(x * tileWidth, 0, x * tileWidth, totalHeight);
		}

		// Agents
		for (AgentDrawInfo a : agents) {
			a.draw(g, tileWidth, tileHeight);
		}

		// Paths
		for (PathDrawInfo path : paths) {
			path.draw(g, tileWidth, tileHeight);
		}

		for (DisplayOverlay overlay : overlays) {
			overlay.draw(g, tileWidth, tileHeight, topology, displaySettings);
		}
	}

}