package org.cobweb.cobweb2.core;

public class ExtendedLocationDirection extends Location {
    private final Direction direction;

    public ExtendedLocationDirection(Location l, Direction d) {
        super(l.x, l.y);
        this.direction = d;
    }

    public ExtendedLocationDirection(Location l) {
        super(l.x, l.y);
        this.direction = Topology.NONE;
    }

    public Direction getDirection() {
        return direction;
    }
}
