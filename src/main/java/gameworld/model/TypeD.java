package gameworld.model;

/** Blue object is keyboard-controlled and stays still while deselected. */
public final class TypeD extends GameObject {
    public TypeD(int x, int y) { super('D', x, y, Direction.UP); }

    @Override protected Direction nextAutomaticDirection(int maxX, int maxY) { return null; }
}
