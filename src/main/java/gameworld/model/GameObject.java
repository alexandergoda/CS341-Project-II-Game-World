package gameworld.model;

import java.util.Objects;

/** Common autonomous movement algorithm and the only place where position changes. */
public abstract class GameObject {
    private static final int AUTO_STEP = 3;
    private static final int KEY_STEP = 6;
    private int x;
    private int y;
    private Direction facing;
    private final char type;

    protected GameObject(char type, int x, int y, Direction facing) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.facing = facing;
    }

    public final int x() { return x; }
    public final int y() { return y; }
    public final Direction facing() { return facing; }
    public final char spriteType() { return type; }

    /** Template Method: ask the subtype for its heading, then apply the common move. */
    final void advanceAutomatically(int maxX, int maxY) {
        Direction direction = nextAutomaticDirection(maxX, maxY);
        if (direction != null) move(direction, AUTO_STEP, maxX, maxY);
    }

    final void advanceManually(Direction direction, int maxX, int maxY) {
        move(Objects.requireNonNull(direction), KEY_STEP, maxX, maxY);
    }

    /** A null heading means this object waits when it is not selected. */
    protected abstract Direction nextAutomaticDirection(int maxX, int maxY);

    protected boolean allows(Direction direction) { return true; }

    protected final boolean hasRoom(Direction direction, int maxX, int maxY) {
        switch (direction) {
            case LEFT: return x > 0;
            case RIGHT: return x < maxX;
            case UP: return y > 0;
            case DOWN: return y < maxY;
            default: throw new AssertionError(direction);
        }
    }

    private void move(Direction direction, int steps, int maxX, int maxY) {
        if (!allows(direction)) return;
        facing = direction;
        x += direction.dx() * steps;
        y += direction.dy() * steps;
        fit(maxX, maxY);
    }

    final void fit(int maxX, int maxY) {
        x = Math.max(0, Math.min(x, maxX));
        y = Math.max(0, Math.min(y, maxY));
    }
}
