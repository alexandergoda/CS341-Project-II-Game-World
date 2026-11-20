package gameworld.model;

/** Red patrols the perimeter, moving right along the bottom and up the right wall. */
public final class TypeB extends GameObject {
    public TypeB(int x, int y) { super('B', x, y, Direction.RIGHT); }

    @Override protected Direction nextAutomaticDirection(int maxX, int maxY) {
        if (maxX == 0) return alongLine(y(), maxY, Direction.DOWN);
        if (maxY == 0) return alongLine(x(), maxX, Direction.RIGHT);

        int left = x();
        int right = maxX - x();
        int top = y();
        int bottom = maxY - y();
        if (bottom == 0 && right > 0) return Direction.RIGHT;
        if (right == 0 && top > 0) return Direction.UP;
        if (top == 0 && left > 0) return Direction.LEFT;
        if (left == 0 && bottom > 0) return Direction.DOWN;

        // Manual steering or an enlarged window can leave red inside the perimeter.
        int nearest = Math.min(Math.min(left, right), Math.min(top, bottom));
        if (bottom == nearest) return Direction.DOWN;
        if (right == nearest) return Direction.RIGHT;
        if (top == nearest) return Direction.UP;
        return Direction.LEFT;
    }

    private Direction alongLine(int position, int limit, Direction forward) {
        if (limit == 0) return null;
        if (position == 0) return forward;
        if (position == limit) return forward.opposite();
        return facing() == forward.opposite() ? forward.opposite() : forward;
    }
}
