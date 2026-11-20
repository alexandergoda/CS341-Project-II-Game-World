package gameworld.model;

/** A one-axis object reverses at a wall and obeys the same axis under the keyboard. */
public abstract class AxisGameObject extends GameObject {
    private Direction travel;

    protected AxisGameObject(char type, int x, int y, Direction initialHeading) {
        super(type, x, y, initialHeading);
        travel = initialHeading;
    }

    @Override protected final boolean allows(Direction direction) {
        return direction == travel || direction == travel.opposite();
    }

    @Override protected final Direction nextAutomaticDirection(int maxX, int maxY) {
        if (!hasRoom(travel, maxX, maxY)) {
            if (!hasRoom(travel.opposite(), maxX, maxY)) return null;
            travel = travel.opposite();
        }
        return travel;
    }
}
