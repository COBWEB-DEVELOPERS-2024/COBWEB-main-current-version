package org.cobweb.cobweb2.core;


public class LocationDirection extends Location {
	public final Direction direction;

	public LocationDirection(Location l, Direction d) {
		super(l.x, l.y);
		direction = d;
	}

	public LocationDirection(Location l) {
		super(l.x, l.y);
		direction = Topology.NONE;
	}

	/**
	 * Returns the location without the direction.
	 */
	public Location toLocation() {
		return new Location(x, y);
	}

private static final long serialVersionUID = 2L;
}
